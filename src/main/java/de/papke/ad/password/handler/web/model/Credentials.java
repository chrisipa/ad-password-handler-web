package de.papke.ad.password.handler.web.model;

/**
 * Data class representing the entered user credentials.
 *
 * @author Christoph Papke (info@papke.it)
 */
public class Credentials {

    private String username;
    private String password;
    private String newPassword;
    private String newPasswordConfirm;

    /**
     * Method for getting the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Method for setting the username.
     *
     * @param username - name of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Method for getting the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method for setting the password.
     *
     * @param password - user password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Method for getting the new user password.
     *
     * @return newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Method for setting the new user password.
     *
     * @param newPassword - new user password
     */
    public void setNewPassword (String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Method for getting the new password confirmation.
     *
     * @return newPasswordConfirm
     */
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    /**
     * Method for setting the new password confirmation.
     *
     * @param newPasswordConfirm - new user password confirmed
     */
    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }
}