package io;

public final class CredentialsIO {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    public CredentialsIO() {
    }

    public CredentialsIO(final CredentialsIO credentials) {
        this.name = credentials.getName();
        this.password = credentials.getPassword();
        this.accountType = credentials.getAccountType();
        this.country = credentials.getCountry();
        this.balance = credentials.getBalance();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(final String balance) {
        this.balance = balance;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
