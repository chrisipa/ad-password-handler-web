package de.papke.ad.password.handler.web.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.papke.ad.password.handler.web.model.ActiveDirectoryUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Service class for changing a password of an active directory user.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Service
public class PasswordChangeService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordChangeService.class);

    private static final String SCRIPT_FILE_PREFIX = "change_password";
    private static final String SCRIPT_FILE_SUFFIX = ".sh";
    private static final String EMAIL_IDENTIFIER = "@";

    @Value("${ad.server.host}")
    private String adServerHost;

    @Value("${password.change.user.filter}")
    private String userFilter;    
    
    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    /**
     * Method for creating the bash script file for changing the password.
     *
     * @return script file handle
     */
    public File createScriptFile() {

        File scriptFile = null;
        FileOutputStream fout = null;

        try {

            // get script file path
            String scriptFilePath = "/" + SCRIPT_FILE_PREFIX + SCRIPT_FILE_SUFFIX;

            // logging
            LOG.info("Creating script file with path '{}'", scriptFilePath);

            // write out script file
            InputStream is = getClass().getResourceAsStream(scriptFilePath);
            scriptFile = File.createTempFile(SCRIPT_FILE_PREFIX, SCRIPT_FILE_SUFFIX);
            fout = new FileOutputStream(scriptFile);
            IOUtils.copy(is, fout);

            // make script file executable
            if (!scriptFile.setExecutable(true)) {
                LOG.error("Cannot make script file '{}' executable.", scriptFile.getAbsolutePath());
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return scriptFile;
    }
    
    
    /**
     * Method for checking if a user is allowed to change a password
     * 
     * @param accountName - the SAMAccountName of the user
     * @return boolean value if changing password is allowed
     */
    private boolean isAllowedToChangePassword(String accountName) {
    	
    	List<ActiveDirectoryUser> userList = activeDirectoryService.getUserList(userFilter);
        for (ActiveDirectoryUser user : userList) {
        	if (user.getsAMAccountName().equals(accountName)) {
        		return true;
        	}
        }
        
        return false;
    }

    /**
     * Method for changing the password by calling the bash script file.
     *
     * @param username - the name of the user
     * @param password - the password of the user
     * @param newPassword - the new password of the user
     * @return boolean value if changing password was successful
     */
    public boolean changePassword(String username, String password, String newPassword) {

        boolean success = false;
        File scriptFile = null;

        try {

            // logging
            LOG.info("Changing active directory password for user '{}'", username);

            // get sAMAccountName if email is entered
            String accountName = null;
            if (username.contains(EMAIL_IDENTIFIER)) {
                accountName = activeDirectoryService.getSAMAccountName(username);
            }
            else {
                accountName = username;
            }
            
            // check if user is allowed to change the password
            if (isAllowedToChangePassword(accountName)) {
            
	            // write out change password script as temp file
	            scriptFile = createScriptFile();
	
	            // generate command line for change password script
	            CommandLine cmdLine = new CommandLine(scriptFile);
	            cmdLine.addArgument(accountName);
	            cmdLine.addArgument(adServerHost);
	            cmdLine.addArgument(password);
	            cmdLine.addArgument(newPassword);
	
	            // execute change password script
	            DefaultExecutor executor = new DefaultExecutor();
	            executor.execute(cmdLine);
	
	            // operation was successfull
	            success = true;
            }
            else {
            	LOG.error("User with account name '{}' is not allowed to change the password.", accountName);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            // remove script file if possible
            if (scriptFile != null && !scriptFile.delete()) {
                LOG.error("Script file '{}' could not be deleted. Please remove manually.", scriptFile.getAbsolutePath());
            }
        }

        return success;
    }
}
