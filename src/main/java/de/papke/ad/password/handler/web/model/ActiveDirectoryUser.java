package de.papke.ad.password.handler.web.model;

/**
 * Data class representing an active directory user.
 *
 * @author Christoph Papke (info@papke.it)
 */
public class ActiveDirectoryUser {

    private String sAMAccountName;
    private String userPrincipalName;
    private String mail;
    private String name;

    /**
     * Method for getting the sAMAccountName.
     *
     * @return sAMAccountName
     */
    public String getsAMAccountName() {
        return sAMAccountName;
    }

    /**
     * Method for setting the sAMAccountName.
     *
     * @param sAMAccountName
     */
    public void setsAMAccountName(String sAMAccountName) {
        this.sAMAccountName = sAMAccountName;
    }

    /**
     * Method for getting the userPrincipalName.
     *
     * @return userPrincipalName
     */
    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    /**
     * Method for setting the UserPrincipalName.
     *
     * @param userPrincipalName
     */
    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    /**
     * Method for getting the mail address.
     *
     * @return mail address
     */
    public String getMail() {
        return mail;
    }

    /**
     * Method for setting the mail address.
     *
     * @param mail - mail address
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Method for getting the name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Method for setting the name.
     *
     * @param name - name of the user
     */
    public void setName(String name) {
        this.name = name;
    }
}
