package execution;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AllMovies;
import database.AllUsers;
import database.Movie;
import database.User;
import io.ActionIO;
import io.CredentialsIO;
import io.FiltersIO;

import java.util.ArrayList;
import java.util.Comparator;

import static execution.BuyPremiumAccount.buyPremiumAccount;
import static execution.BuyTokens.buyTokens;
import static execution.Filter.filter;
import static execution.Like.like;
import static execution.Like.updateAllMovieLists;
import static execution.Login.loggedSuccessfully;
import static execution.Login.login;
import static execution.OnPageActions.SIX;
import static execution.OnPageActions.TEN;
import static execution.OnPageActions.putActionOutput;
import static execution.Purchase.purchase;
import static execution.Rate.rateAMovie;
import static execution.Register.register;
import static execution.Search.search;
import static execution.Subscribe.subscribe;
import static execution.Watch.watch;

/**toate actiunile de tip "on page"*/
public final class OnPageActions {
    public static final int SIX = 6;
    public static final int TEN = 10;
    private OnPageActions() {
    }

    /**
     * verifica daca actiunea de tip "on page" e permisa
     */
    public static boolean isOnPageActionPermitted(final String feature, final String subGenre) {
        boolean movieWasPurchased = false;
        boolean movieWasWatched = false;
        if (RealTimePage.getIt().getUser() != null) {
            for (Movie movie : RealTimePage.getIt().getUser().getPurchasedMovies()) {
                if (movie.getName().equals(RealTimePage.getIt().getMovieList().get(0).getName())) {
                    movieWasPurchased = true;
                    break;
                }
            }
            for (Movie movie : RealTimePage.getIt().getUser().getWatchedMovies()) {
                if (movie.getName().equals(RealTimePage.getIt().getMovieList().get(0).getName())) {
                    movieWasWatched = true;
                    break;
                }
            }
        }
        switch (feature) {
            case "login" -> {
                return RealTimePage.getIt().getPageName().equals("login page");
            }
            case "register" -> {
                return RealTimePage.getIt().getPageName().equals("register page");
            }
            case "search", "filter" -> {
                return RealTimePage.getIt().getPageName().equals("movies page");
            }
            case "buy tokens", "buy premium account" -> {
                return RealTimePage.getIt().getPageName().equals("upgrades page");
            }
            case "purchase" -> {
                return RealTimePage.getIt().getPageName().equals("see details page")
                        && !movieWasPurchased;
            }
            case "watch" -> {
                return RealTimePage.getIt().getPageName().equals("see details page")
                        && movieWasPurchased;
            }
            case "like", "rate" -> {
                return RealTimePage.getIt().getPageName().equals("see details page")
                        && movieWasPurchased && movieWasWatched;
            }
            case "subscribe" -> {
                return RealTimePage.getIt().getPageName().equals("see details page")
                        && RealTimePage.getIt().getUser() != null
                        && RealTimePage.getIt().getMovieList().get(0).getGenres().contains(subGenre)
                        && !RealTimePage.getIt().getUser().getSubGenres().contains(subGenre);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * executa actiunea de tip "on page"
     */
    public static void doOnPageAction(final ActionIO action, final ObjectNode node,
                                      final ArrayNode output) {
        switch (action.feature()) {
            case "login" -> login(action.credentials(), node, output);
            case "register" -> register(action.credentials(), node, output);
            case "search" -> search(action.startsWith(), node, output);
            case "filter" -> filter(action.filters(), node, output);
            case "buy tokens" -> buyTokens(action.count(), node, output);
            case "buy premium account" -> buyPremiumAccount(node, output);
            case "purchase" -> purchase(node, output);
            case "watch" -> watch(node, output);
            case "like" -> like(node, output);
            case "rate" -> rateAMovie(action.rate(), node, output);
            case "subscribe" -> subscribe(action.subscribedGenre());
            default -> System.out.println("ERROR!");
        }
    }

    /**adauga nodul pentru output in array*/
    public static void putActionOutput(final boolean errorless, final ObjectNode node,
                                       final ArrayNode output) {
        if (errorless) {
            node.putPOJO("error", null);
            ArrayList<Movie> realTimeMovieList =
                    new ArrayList<>(RealTimePage.getIt().getMovieList());
            node.putPOJO("currentMoviesList", realTimeMovieList);
            User realTimeUser = new User(RealTimePage.getIt().getUser());
            node.putPOJO("currentUser", realTimeUser);
            output.add(node);
        } else {
            node.put("error", "Error");
            ArrayList<Movie> empty = new ArrayList<>();
            node.putPOJO("currentMoviesList", empty);
            node.putPOJO("currentUser", null);
            output.add(node);
        }
    }
}

final class Login {
    private Login() {
    }
    /**comanda 'on page' de login*/
    static void login(final CredentialsIO credentials, final ObjectNode node,
                      final ArrayNode output) {
        boolean userIsInDatabase = false;
        for (User user : AllUsers.getDatabase().getAllUsers()) {
            if (user.getCredentials().getName().equals(credentials.getName())
                    && user.getCredentials().getPassword().equals(credentials.getPassword())) {
                userIsInDatabase = true;
                loggedSuccessfully(user);
                putActionOutput(true, node, output);
                break;
            }
        }
        if (!userIsInDatabase) {
            putActionOutput(false, node, output);
            RealTimePage.getIt().setPageName("logout page");
        }
    }

    /** actiunea propriu-zisa de logare cu succes*/
    static void loggedSuccessfully(final User user) {
        RealTimePage.getIt().setPageName("homepage");
        User copyUser = new User(user);
        RealTimePage.getIt().setUser(copyUser);
        History.getIt();
        RealTimePage.getIt().getCountryPermittedMovies().clear();
        for (Movie movie : AllMovies.getDatabase().getAllMovies()) {
            String uCountry = RealTimePage.getIt().getUser().getCredentials().getCountry();
            if (!movie.getCountriesBanned().contains(uCountry)) {
                Movie copyMovie = new Movie(movie);
                RealTimePage.getIt().getCountryPermittedMovies().add(copyMovie);
            }
        }
    }
}

final class Register {
    private Register() {
    }
    /**comanda 'on page' de register*/
    static void register(final CredentialsIO credentials, final ObjectNode node,
                         final ArrayNode output) {
        boolean userIsNotInDatabase = true;
        for (User user: AllUsers.getDatabase().getAllUsers()) {
            if (user.getCredentials().getName().equals(credentials.getName())) {
                userIsNotInDatabase = false;
                RealTimePage.getIt().setPageName("logout page");
                putActionOutput(false, node, output);
                break;
            }
        }
        if (userIsNotInDatabase) {
            User registeredUser = new User(credentials);
            AllUsers.getDatabase().getAllUsers().add(registeredUser);
            loggedSuccessfully(registeredUser);
            putActionOutput(true, node, output);
        }
    }
}

final class Search {
    private Search() {
    }
    /**comanda 'on page' de search*/
    static void search(final String startWith, final ObjectNode node, final ArrayNode output) {
        RealTimePage.getIt().getMovieList().clear();
        for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
            if (movie.getName().startsWith(startWith)) {
                RealTimePage.getIt().getMovieList().add(movie);
            }
        }
        putActionOutput(true, node, output);
    }
}

final class Filter {
    private Filter() {
    }
    /**comanda 'on page' de filter*/
    static void filter(final FiltersIO filters, final ObjectNode node, final ArrayNode output) {
        if (filters.contains() != null) {
            RealTimePage.getIt().getMovieList().clear();
            if (filters.contains().genre() != null
                    && filters.contains().actors() != null) {
                for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
                    if (movie.getGenres().containsAll(filters.contains().genre())
                            && movie.getActors().containsAll(filters.contains().actors())) {
                        RealTimePage.getIt().getMovieList().add(movie);
                    }
                }
            } else if (filters.contains().genre() != null) {
                for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
                    if (movie.getGenres().containsAll(filters.contains().genre())) {
                        RealTimePage.getIt().getMovieList().add(movie);
                    }
                }
            } else {
                for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
                    if (movie.getActors().containsAll(filters.contains().actors())) {
                        RealTimePage.getIt().getMovieList().add(movie);
                    }
                }
            }
        }
        if (filters.sort() != null) {
            if (filters.sort().rating() != null) {
                Comparator<Movie> increasingRating = Comparator.comparing(Movie::getRating);
                if (filters.sort().rating().equals("increasing")) {
                    RealTimePage.getIt().getMovieList().sort(increasingRating);
                } else {
                    RealTimePage.getIt().getMovieList().sort(increasingRating.reversed());
                }
            }
            if (filters.sort().duration() != null) {
                Comparator<Movie> increasingDuration = Comparator.comparing(Movie::getDuration);
                if (filters.sort().duration().equals("increasing")) {
                    RealTimePage.getIt().getMovieList().sort(increasingDuration);
                } else {
                    RealTimePage.getIt().getMovieList().sort(increasingDuration.reversed());
                }
            }
        }
        putActionOutput(true, node, output);
    }
}

final class BuyTokens {
    private BuyTokens() {
    }
    /**comanda 'on page' de buy tokens*/
    static void buyTokens(final String count, final ObjectNode node, final ArrayNode output) {
        String userBalance = RealTimePage.getIt().getUser().getCredentials().getBalance();
        if (Integer.parseInt(count) <= Integer.parseInt(userBalance)) {
            RealTimePage.getIt().getUser().getCredentials().setBalance(Integer
                    .toString(Integer.parseInt(userBalance) - Integer.parseInt(count)));
            RealTimePage.getIt().getUser().setTokensCount(
                    RealTimePage.getIt().getUser().getTokensCount() + Integer.parseInt(count));
        } else {
            putActionOutput(false, node, output);
        }
    }
}

final class BuyPremiumAccount {
    private BuyPremiumAccount() {
    }
    /**comanda 'on page' de buy premium account*/
    static void buyPremiumAccount(final ObjectNode node, final ArrayNode output) {
        if (RealTimePage.getIt().getUser().getCredentials().getAccountType().equals("standard")
                && RealTimePage.getIt().getUser().getTokensCount() >= TEN) {
            RealTimePage.getIt().getUser().setTokensCount(
                    RealTimePage.getIt().getUser().getTokensCount() - TEN);
            RealTimePage.getIt().getUser().getCredentials().setAccountType("premium");
        } else {
            putActionOutput(false, node, output);
        }
    }
}

final class Purchase {
    private Purchase() {
    }
    /**comanda 'on page' de purchase*/
    static void purchase(final ObjectNode node, final ArrayNode output) {
        if (RealTimePage.getIt().getUser().getCredentials().getAccountType().equals("premium")) {
            if (RealTimePage.getIt().getUser().getNumFreePremiumMovies() != 0) {
                RealTimePage.getIt().getUser().setNumFreePremiumMovies(
                        RealTimePage.getIt().getUser().getNumFreePremiumMovies() - 1);
                Movie paidMovie = new Movie(RealTimePage.getIt().getMovieList().get(0));
                RealTimePage.getIt().getUser().getPurchasedMovies().add(paidMovie);
                putActionOutput(true, node, output);
            } else {
                standardWayOfBuyingMovie(node, output);
            }
        } else {
            standardWayOfBuyingMovie(node, output);
        }
    }

    /** metoda contului standard de a cumpara un film */
    private static void standardWayOfBuyingMovie(final ObjectNode node, final ArrayNode output) {
        if (RealTimePage.getIt().getUser().getTokensCount() > 1) {
            RealTimePage.getIt().getUser().setTokensCount(
                    RealTimePage.getIt().getUser().getTokensCount() - 2);
            Movie paidMovie = new Movie(RealTimePage.getIt().getMovieList().get(0));
            RealTimePage.getIt().getUser().getPurchasedMovies().add(paidMovie);
            putActionOutput(true, node, output);
        } else {
            putActionOutput(false, node, output);
        }
    }
}

final class Watch {
    private Watch() {
    }
    /**comanda 'on page' de watch*/
    static void watch(final ObjectNode node, final ArrayNode output) {
        boolean alreadyWatched = false;
        Movie watchedMovie = new Movie(RealTimePage.getIt().getMovieList().get(0));
        for (Movie movie : RealTimePage.getIt().getUser().getWatchedMovies()) {
            if (movie.getName().equals(watchedMovie.getName())) {
                alreadyWatched = true;
            }
        }
        if (!alreadyWatched) {
            RealTimePage.getIt().getUser().getWatchedMovies().add(watchedMovie);
            putActionOutput(true, node, output);
        } else {
            putActionOutput(true, node, output);
        }
    }
}

final class Like {
    private Like() {
    }
    /**comanda 'on page' de like*/
    static void like(final ObjectNode node, final ArrayNode output) {
        Movie likedMovie = new Movie(RealTimePage.getIt().getMovieList().get(0));
        likedMovie.setNumLikes(likedMovie.getNumLikes() + 1);
        RealTimePage.getIt().getMovieList().clear();
        RealTimePage.getIt().getMovieList().add(likedMovie);
        RealTimePage.getIt().getUser().getLikedMovies().add(likedMovie);
        updateAllMovieLists(likedMovie, RealTimePage.getIt().getUser());
        putActionOutput(true, node, output);
    }

    /** updateaza toate listele unui user cu filmul actualizat */
    static void updateAllMovieLists(final Movie actualisedMovie, final User user) {
        int cnt = 0;
        int idx;
        for (Movie movie : AllMovies.getDatabase().getAllMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                AllMovies.getDatabase().getAllMovies().set(idx, actualisedMovie);
                cnt = 0;
                break;
            }
            cnt++;
        }
        for (Movie movie : RealTimePage.getIt().getCountryPermittedMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                RealTimePage.getIt().getCountryPermittedMovies().set(idx, actualisedMovie);
                cnt = 0;
                break;
            }
            cnt++;
        }
        for (Movie movie : user.getPurchasedMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                user.getPurchasedMovies().set(idx, actualisedMovie);
                cnt = 0;
                break;
            }
            cnt++;
        }
        for (Movie movie : user.getWatchedMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                user.getWatchedMovies().set(idx, actualisedMovie);
                cnt = 0;
                break;
            }
            cnt++;
        }
        for (Movie movie : user.getLikedMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                user.getLikedMovies().set(idx, actualisedMovie);
                cnt = 0;
                break;
            }
            cnt++;
        }
        for (Movie movie : user.getRatedMovies()) {
            if (movie.getName().equals(actualisedMovie.getName())) {
                idx = cnt;
                user.getRatedMovies().set(idx, actualisedMovie);
                break;
            }
            cnt++;
        }
    }
}

final class Rate {
    private Rate() {
    }
    /**comanda 'on page' de rate*/
    static void rateAMovie(final int rating, final ObjectNode node, final ArrayNode output) {
        if (rating > 0 && rating < SIX) {
            Movie ratedMovie = new Movie(RealTimePage.getIt().getMovieList().get(0));
            boolean movieWasRated = false;
            for (int i = 0; i < RealTimePage.getIt().getUser().getRatedMovies().size(); i++) {
                if (RealTimePage.getIt().getUser().getRatedMovies().get(i).getName()
                        .equals(ratedMovie.getName())) {
                    movieWasRated = true;
                    break;
                }
            }
            if (!movieWasRated) {
                ratedMovie.setNumRatings(ratedMovie.getNumRatings() + 1);
                ratedMovie.setUsersRaters(ratedMovie.getUsersRaters() + 1);
                ratedMovie.setRatingsSum(ratedMovie.getRatingsSum() + rating);
                ratedMovie.setRating((double) ratedMovie.getRatingsSum()
                        / ratedMovie.getUsersRaters());
                RealTimePage.getIt().getMovieList().clear();
                RealTimePage.getIt().getMovieList().add(ratedMovie);
                RealTimePage.getIt().getUser().getRatedMovies().add(ratedMovie);
                updateAllMovieLists(ratedMovie,  RealTimePage.getIt().getUser());
                for (User user : AllUsers.getDatabase().getAllUsers()) {
                    updateAllMovieLists(ratedMovie, user);
                }
                putActionOutput(true, node, output);
            } else {
                ratedMovie.setUsersRaters(ratedMovie.getUsersRaters() + 1);
                ratedMovie.setRatingsSum(ratedMovie.getRatingsSum() + rating);
                ratedMovie.setRating((double) ratedMovie.getRatingsSum()
                        / ratedMovie.getUsersRaters());
                RealTimePage.getIt().getMovieList().clear();
                RealTimePage.getIt().getMovieList().add(ratedMovie);
                updateAllMovieLists(ratedMovie,  RealTimePage.getIt().getUser());
                for (User user : AllUsers.getDatabase().getAllUsers()) {
                    updateAllMovieLists(ratedMovie, user);
                }
                putActionOutput(true, node, output);
            }
        } else {
            putActionOutput(false, node, output);
        }
    }
}

final class Subscribe {
    private Subscribe() {
    }
    /**comanda 'on page' de subscribe*/
    static void subscribe(final String subscribedGenre) {
        User realTimeUser = RealTimePage.getIt().getUser();
        realTimeUser.getSubGenres().add(subscribedGenre);
        for (User databaseUser : AllUsers.getDatabase().getAllUsers()) {
            if (databaseUser.getCredentials().getName().equals(realTimeUser
                    .getCredentials().getName())) {
                databaseUser.getSubGenres().add(subscribedGenre);
            }
        }
    }
}
