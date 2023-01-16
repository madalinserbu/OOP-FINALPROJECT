package io;

import java.util.ArrayList;

public class MovieIO {
    private String name;
    private String year;
    private int duration;
    private ArrayList<String> genres;
    private ArrayList<String> actors;
    private ArrayList<String> countriesBanned;

    public MovieIO() {
    }

    public MovieIO(final MovieIO movie) {
        this.name = movie.getName();
        this.year = movie.getYear();
        this.duration = movie.getDuration();
        this.genres = new ArrayList<>(movie.getGenres());
        this.actors = new ArrayList<>(movie.getActors());
        this.countriesBanned = new ArrayList<>(movie.getCountriesBanned());
    }
    /***/
    public String getName() {
        return name;
    }
    /***/
    public void setName(final String name) {
        this.name = name;
    }
    /***/
    public String getYear() {
        return year;
    }
    /***/
    public void setYear(final String year) {
        this.year = year;
    }
    /***/
    public int getDuration() {
        return duration;
    }
    /***/
    public void setDuration(final int duration) {
        this.duration = duration;
    }
    /***/
    public ArrayList<String> getGenres() {
        return genres;
    }
    /***/
    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }
    /***/
    public ArrayList<String> getActors() {
        return actors;
    }
    /***/
    public void setActors(final ArrayList<String> actors) {
        this.actors = actors;
    }
    /***/
    public ArrayList<String> getCountriesBanned() {
        return countriesBanned;
    }
    /***/
    public void setCountriesBanned(final ArrayList<String> countriesBanned) {
        this.countriesBanned = countriesBanned;
    }
}
