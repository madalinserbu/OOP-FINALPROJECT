package execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Movie;
import database.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public final class Notification {
    private String movieName;
    private String message;

    public Notification(final String title, final String msg) {
        movieName = title;
        message = msg;
    }

    /** metoda responsabila cu oferirea de recomandari conturilor premium */
    public static void recommendation(final ArrayNode output) {
        ArrayList<String> availableGenres = new ArrayList<>();
        for (Movie movie : RealTimePage.getIt().getUser().getLikedMovies()) {
            for (String genre : movie.getGenres()) {
                if (!availableGenres.contains(genre)) {
                    availableGenres.add(genre);
                }
            }
        }

        TreeMap<String, Integer> genresAndLikes = new TreeMap<>();
        for (int i = 0; i < availableGenres.size(); i++) {
            int numLikes = 0;
            for (Movie movie : RealTimePage.getIt().getUser().getLikedMovies()) {
                if (movie.getGenres().contains(availableGenres.get(i))) {
                    numLikes = numLikes + 1;
                }
            }
            if (numLikes > 0) {
                genresAndLikes.put(availableGenres.get(i), numLikes);
            }
        }

        String recommendation = "No recommendation";

        while (true) {
            Map.Entry<String, Integer> mostLikedGenre =
                    getMaxEntryInMapBasedOnValue(genresAndLikes);
            if (mostLikedGenre == null) {
                break;
            }
            genresAndLikes.remove(mostLikedGenre.getKey(), mostLikedGenre.getValue());

            Comparator<Movie> decreasingLikes =
                    Comparator.comparing(Movie::getNumLikes).reversed();
            ArrayList<Movie> mostLikedMovies = new ArrayList<>();
            for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
                if (movie.getGenres().contains(mostLikedGenre.getKey())) {
                    mostLikedMovies.add(movie);
                }
            }
            mostLikedMovies.sort(decreasingLikes);

            boolean alreadyWatched = false;
            for (Movie movie : mostLikedMovies) {
                alreadyWatched = false;
                for (Movie wMovie : RealTimePage.getIt().getUser().getWatchedMovies()) {
                    if (wMovie.getName().equals(movie.getName())) {
                        alreadyWatched = true;
                        break;
                    }
                }
                if (!alreadyWatched) {
                    recommendation = movie.getName();
                    break;
                }
            }

            if (!alreadyWatched || genresAndLikes.size() == 0) {
                break;
            }
        }
        Notification notify = new Notification(recommendation, "Recommendation");
        RealTimePage.getIt().getUser().getNotifications().add(notify);
        ObjectMapper objMapper = new ObjectMapper();
        ObjectNode node = objMapper.createObjectNode();
        node.putPOJO("error", null);
        node.putPOJO("currentMoviesList", null);
        User realTimeUser = new User(RealTimePage.getIt().getUser());
        node.putPOJO("currentUser", realTimeUser);
        output.add(node);
    }

    /** returneaza entry-ul cu cea mai mare valoare dintr-un map si il
     * folosim pentru a afla genul cu cele mai multe like-uri*/
    private static Map.Entry<String, Integer>
    getMaxEntryInMapBasedOnValue(final TreeMap<String, Integer> map) {
        Map.Entry<String, Integer> entryWithMaxValue = null;
        for (Map.Entry<String, Integer> currentEntry : map.entrySet()) {
            if (entryWithMaxValue == null
                    || currentEntry.getValue() > entryWithMaxValue.getValue()) {
                entryWithMaxValue = currentEntry;
            }
        }
        return entryWithMaxValue;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(final String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
