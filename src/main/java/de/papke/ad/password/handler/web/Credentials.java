package de.papke.ad.password.handler.web;

public class Credentials {

    private String loginName;
    private String currentPassword;
    private String confirmPassword;
    private String newPassword;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword (String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword (String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword (String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}