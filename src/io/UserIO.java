package io;

public class UserIO {
    private CredentialsIO credentials;

    public UserIO() {
    }
    /***/
    public UserIO(final UserIO user) {
        this.credentials = new CredentialsIO(user.getCredentials());
    }
    /***/
    public UserIO(final CredentialsIO credentials) {
        this.credentials = new CredentialsIO(credentials);
    }
    /***/
    public CredentialsIO getCredentials() {
        return credentials;
    }
    /***/
    public void setCredentials(final CredentialsIO credentials) {
        this.credentials = credentials;
    }
}
