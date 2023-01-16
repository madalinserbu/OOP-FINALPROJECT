package io;

import database.User;

public class UserIO {
    private CredentialsIO credentials;

    public UserIO() {

    }

    public UserIO(UserIO user) {
        this.credentials = new CredentialsIO(user.getCredentials());
    }

    public UserIO(CredentialsIO credentials) {
        this.credentials = new CredentialsIO(credentials);
    }

    public CredentialsIO getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsIO credentials) {
        this.credentials = credentials;
    }
}
