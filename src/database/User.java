package database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.CredentialsIO;
import execution.Notification;
import io.UserIO;

import java.util.ArrayList;

public final class User extends UserIO {
    private int tokensCount;
    private int numFreePremiumMovies;
    private ArrayList<Movie> purchasedMovies;
    private ArrayList<Movie> watchedMovies;
    private ArrayList<Movie> likedMovies;
    private ArrayList<Movie> ratedMovies;
    private ArrayList<Notification> notifications;
    @JsonIgnore
    private ArrayList<String> subGenres;

    public User(final UserIO user) {
        super(user);
        this.tokensCount = 0;
        this.numFreePremiumMovies = 15;
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
        subGenres = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public User(final User user) {
        super(user);
        tokensCount = user.getTokensCount();
        numFreePremiumMovies = user.getNumFreePremiumMovies();
        purchasedMovies = new ArrayList<>(user.getPurchasedMovies());
        watchedMovies = new ArrayList<>(user.getWatchedMovies());
        likedMovies = new ArrayList<>(user.getLikedMovies());
        ratedMovies = new ArrayList<>(user.getRatedMovies());
        subGenres = new ArrayList<>(user.getSubGenres());
        notifications = new ArrayList<>(user.getNotifications());
    }

    public User(final CredentialsIO credentials) {
        super(credentials);
        this.tokensCount = 0;
        this.numFreePremiumMovies = 15;
        purchasedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        likedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();
        subGenres = new ArrayList<>();
        notifications = new ArrayList<>();
    }

    public int getTokensCount() {
        return tokensCount;
    }

    public void setTokensCount(final int tokensCount) {
        this.tokensCount = tokensCount;
    }

    public int getNumFreePremiumMovies() {
        return numFreePremiumMovies;
    }

    public void setNumFreePremiumMovies(final int numFreePremiumMovies) {
        this.numFreePremiumMovies = numFreePremiumMovies;
    }

    public ArrayList<Movie> getPurchasedMovies() {
        return purchasedMovies;
    }

    public void setPurchasedMovies(final ArrayList<Movie> purchasedMovies) {
        this.purchasedMovies = purchasedMovies;
    }

    public ArrayList<Movie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(final ArrayList<Movie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    public ArrayList<Movie> getLikedMovies() {
        return likedMovies;
    }

    public void setLikedMovies(final ArrayList<Movie> likedMovies) {
        this.likedMovies = likedMovies;
    }

    public ArrayList<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public void setRatedMovies(final ArrayList<Movie> ratedMovies) {
        this.ratedMovies = ratedMovies;
    }

    public ArrayList<String> getSubGenres() {
        return subGenres;
    }

    public void setSubGenres(ArrayList<String> subGenres) {
        this.subGenres = subGenres;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
}
