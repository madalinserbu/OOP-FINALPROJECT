package database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.MovieIO;

import java.util.ArrayList;

public final class Movie extends MovieIO {
    private int numLikes;
    private double rating;
    private int numRatings;
    @JsonIgnore
    private int usersRaters;
    @JsonIgnore
    private int ratingsSum;

    public Movie() {

    }

    public Movie(final Movie movie) {
        super(movie);
        this.numLikes = movie.getNumLikes();
        this.rating = movie.getRating();
        this.numRatings = movie.getNumRatings();
        this.ratingsSum = movie.getRatingsSum();
        this.usersRaters = movie.getUsersRaters();
    }

    public Movie(final MovieIO movie) {
        super(movie);
        this.numLikes = 0;
        this.rating = 0;
        this.numRatings = 0;
        this.ratingsSum = 0;
        this.usersRaters = 0;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(final int numLikes) {
        this.numLikes = numLikes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(final double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(final int numRatings) {
        this.numRatings = numRatings;
    }

    @JsonIgnore
    public int getRatingsSum() {
        return ratingsSum;
    }

    public void setRatingsSum(final int ratingsSum) {
        this.ratingsSum = ratingsSum;
    }

    public int getUsersRaters() {
        return usersRaters;
    }

    public void setUsersRaters(int usersRaters) {
        this.usersRaters = usersRaters;
    }
}
