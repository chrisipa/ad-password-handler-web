package de.papke.ad.password.handler.web.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PasswordChangeService {

    private static final String SCRIPT_FILE_PREFIX = "change_password";
    private static final String SCRIPT_FILE_SUFFIX = ".sh";

    @Value("${ad.server.host}")
    private String adServerHost;

    public File createScriptFile() {

        File scriptFile = null;
        FileOutputStream fout = null;

        try {

            // write out script file an set executable
            String scriptFilePath = "/" + SCRIPT_FILE_PREFIX + SCRIPT_FILE_SUFFIX;
            InputStream is = getClass().getResourceAsStream(scriptFilePath);
            scriptFile = File.createTempFile(SCRIPT_FILE_PREFIX, SCRIPT_FILE_SUFFIX);
            fout = new FileOutputStream(scriptFile);
            IOUtils.copy(is, fout);
            scriptFile.setExecutable(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return scriptFile;
    }

    public boolean changePassword(String username, String password, String newPassword) {

        boolean success = false;
        File scriptFile = null;

        try {

            // write out change password script as temp file
            scriptFile = createScriptFile();

            // generate command line for change password script
            CommandLine cmdLine = new CommandLine(scriptFile);
            cmdLine.addArgument(username);
            cmdLine.addArgument(adServerHost);
            cmdLine.addArgument(password);
            cmdLine.addArgument(newPassword);

            // execute change password script
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine);

            // operation was successfull
            success = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (scriptFile != null) {
                scriptFile.delete();
            }
        }

        return success;
    }
}
