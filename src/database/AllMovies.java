package database;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import execution.RealTimePage;
import io.IO;
import io.MovieIO;
import execution.Notification;

import java.util.ArrayList;

import static execution.OnPageActions.putActionOutput;

/**baza de date filme*/
public final class AllMovies {
    private static AllMovies database = null;
    private final ArrayList<Movie> allMovies;
    private AllMovies(final IO input) {
        allMovies = new ArrayList<>();
        for (MovieIO movieIO : input.movies()) {
            Movie movie = new Movie(movieIO);
            allMovies.add(movie);
        }
    }

    /**singleton*/
    public static AllMovies getDatabase(final IO input) {
        if (database == null) {
            database = new AllMovies(input);
        }
        return database;
    }

    public static AllMovies getDatabase() {
        return database;
    }

    /**adauga noul film in toate listele necesare si notifica utilizatorii abonati de acest lcuru*/
    public static void addMovie(final MovieIO addedMovie, final ObjectNode node,
                                final ArrayNode output) {
        Movie newMovie = new Movie(addedMovie);
        int control = 0;
        for (int i = 0; i < AllMovies.getDatabase().allMovies.size(); i++) {
            if (AllMovies.getDatabase().allMovies.get(i).getName().equals(newMovie.getName())) {
                putActionOutput(false, node, output);
                control = 1;
                break;
            }
        }

        if (control == 0) {
            Notification notify = new Notification(newMovie.getName(), "ADD");
            AllMovies.getDatabase().allMovies.add(newMovie);
            for (User databaseUser : AllUsers.getDatabase().getAllUsers()) {
                for (String genre : databaseUser.getSubGenres()) {
                    if (newMovie.getGenres().contains(genre)) {
                        if (!newMovie.getCountriesBanned().contains(databaseUser.getCredentials()
                                .getCountry())) {
                            databaseUser.getNotifications().add(notify);
                            break;
                        }
                    }
                }
            }

            if (RealTimePage.getIt().getUser() != null) {
                if (!newMovie.getCountriesBanned().contains(RealTimePage.getIt().getUser()
                        .getCredentials().getCountry())) {
                    RealTimePage.getIt().getCountryPermittedMovies().add(newMovie);
                }
                for (String genre : RealTimePage.getIt().getUser().getSubGenres()) {
                    if (newMovie.getGenres().contains(genre)) {
                        if (!newMovie.getCountriesBanned().contains(RealTimePage.getIt().getUser()
                                .getCredentials().getCountry())) {
                            RealTimePage.getIt().getUser().getNotifications().add(notify);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**sterge filmul selectat din toate listele unde apare
     * si notifica utilizatorii abonati de acest lcuru*/
    public static void deleteMovie(final String deletedMovie, final ObjectNode node,
                                   final ArrayNode output) {
        int control = 0;
        for (int i = 0; i < AllMovies.getDatabase().allMovies.size(); i++) {
            if (AllMovies.getDatabase().allMovies.get(i).getName().equals(deletedMovie)) {
                AllMovies.getDatabase().allMovies.remove(i);
                removeMovieByName(RealTimePage.getIt().getCountryPermittedMovies(), deletedMovie);
                for (User databaseUser : AllUsers.getDatabase().getAllUsers()) {
                    boolean movieWasPurchased = false;
                    for (Movie movie : databaseUser.getPurchasedMovies()) {
                        if (movie.getName().equals(deletedMovie)) {
                            movieWasPurchased = true;
                        }
                    }
                    if (movieWasPurchased) {
                        removeMovieByName(databaseUser.getPurchasedMovies(), deletedMovie);
                        removeMovieByName(databaseUser.getWatchedMovies(), deletedMovie);
                        removeMovieByName(databaseUser.getLikedMovies(), deletedMovie);
                        removeMovieByName(databaseUser.getRatedMovies(), deletedMovie);
                        switch (databaseUser.getCredentials().getAccountType()) {
                            case "premium" -> databaseUser.setNumFreePremiumMovies(databaseUser
                                    .getNumFreePremiumMovies() + 1);
                            case "standard" -> databaseUser.setTokensCount(databaseUser
                                    .getTokensCount() + 2);
                            default -> System.out.println("unknown acc type");
                        }
                        Notification notify = new Notification(deletedMovie, "DELETE");
                        databaseUser.getNotifications().add(notify);
                    }
                    if (RealTimePage.getIt().getUser().getCredentials().getName().equals(
                            databaseUser.getCredentials().getName())) {
                        RealTimePage.getIt().setUser(new User(databaseUser));
                    }
                }
                control = 1;
                break;
            }
        }

        if (control == 0) {
            putActionOutput(false, node, output);
        }
    }

    /** sterge un film dintr-o lisa dupa numele acestuia */
    private static void removeMovieByName(final ArrayList<Movie> movieCollection,
                                          final String deletedMovie) {
        for (int i = 0; i < movieCollection.size(); i++) {
            if (movieCollection.get(i).getName().equals(deletedMovie)) {
                movieCollection.remove(i);
                break;
            }
        }
    }

    /**goleste baza de date la finalul unui test*/
    public static void freeDatabase() {
        database = null;
    }

    public ArrayList<Movie> getAllMovies() {
        return allMovies;
    }
}
