package execution;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AllUsers;
import database.Movie;
import database.User;

import java.util.ArrayList;

import static execution.OnPageActions.putActionOutput;

/**aceasta va vi pagina in timp real a platformei*/
public final class RealTimePage {
    private static RealTimePage it = null;
    private String pageName;
    private User user;
    private final ArrayList<Movie> countryPermittedMovies;
    private ArrayList<Movie> movieList;


    private RealTimePage() {
        pageName = "logout page";
        movieList = new ArrayList<>();
        user = null;
        countryPermittedMovies = new ArrayList<>();
    }

    /**singleton*/
    public static RealTimePage getIt() {
        if (it == null) {
            it = new RealTimePage();
        }
        return it;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(final String pageName) {
        this.pageName = pageName;
    }

    public ArrayList<Movie> getMovieList() {
        return movieList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public ArrayList<Movie> getCountryPermittedMovies() {
        return countryPermittedMovies;
    }

    /**verifica daca actiunea de "change page" e permiasa*/
    public boolean isChangePagePermitted(final String goToPage) {
        switch (pageName) {
            case "login page" -> {
                return goToPage.equals("register");
            }
            case "register page" -> {
                return goToPage.equals("login");
            }
            case "homepage", "upgrades page" -> {
                return goToPage.equals("movies") || goToPage.equals("upgrades")
                        || goToPage.equals("logout");
            }
            case "movies page", "see details page" -> {
                return goToPage.equals("see details") || goToPage.equals("movies")
                        || goToPage.equals("upgrades") || goToPage.equals("logout");
            }
            case "logout page" -> {
                return goToPage.equals("register") || goToPage.equals("login");
            }
            default -> {
                return false;
            }
        }
    }

    /**executa actiunea de "change page"*/
    public void changePage(final String type, final String page, final String movieName,
                           final Movie previousMovie, final ObjectNode node,
                           final ArrayNode output) {
        if (!type.equals("back")) {
            if (RealTimePage.getIt().getUser() != null && (page.equals("movies")
                    || page.equals("see details") || page.equals("upgrades"))) {
                if (pageName.equals("see details")) {
                    History.getIt().getPageHistory().push(pageName);
                    History.getIt().getMovieHistory().push(new Movie(movieList.get(0)));
                } else {
                    if (page.equals("see details")) {
                        ArrayList<String> movieNames = new ArrayList<>();
                        for (Movie movie : movieList) {
                            movieNames.add(movie.getName());
                        }
                        if (movieNames.contains(movieName)) {
                            History.getIt().getPageHistory().push(pageName);
                        }
                    } else {
                        History.getIt().getPageHistory().push(pageName);
                    }
                }
            }
            switch (page) {
                case "login" -> setPageName("login page");
                case "register" -> setPageName("register page");
                case "movies" -> {
                    movieList = new ArrayList<>(countryPermittedMovies);
                    setPageName("movies page");
                    putActionOutput(true, node, output);
                }
                case "see details" -> {
                    ArrayList<String> movieNames = new ArrayList<>();
                    for (Movie movie : movieList) {
                        movieNames.add(movie.getName());
                    }
                    if (movieNames.contains(movieName)) {
                        movieList.removeIf(movie -> !movie.getName().equals(movieName));
                        movieList = new ArrayList<>(movieList);
                        setPageName("see details page");
                        putActionOutput(true, node, output);
                    } else {
                        putActionOutput(false, node, output);
                    }
                }
                case "upgrades" -> setPageName("upgrades page");
                case "logout" -> {
                    for (int i = 0; i < AllUsers.getDatabase().getAllUsers().size(); i++) {
                        if (user.getCredentials().getName().equals(AllUsers.getDatabase()
                                .getAllUsers().get(i).getCredentials().getName())) {
                            AllUsers.getDatabase().getAllUsers().set(i, new User(user));
                            break;
                        }
                    }
                    user = null;
                    movieList.clear();
                    countryPermittedMovies.clear();
                    setPageName("logout page");
                    History.getIt().clear();
                }
                default -> System.out.println("ERROR simple change page!");
            }
        } else {
            switch (page) {
                case "homepage" -> {
                    setPageName("homepage");
                    movieList.clear();
                }
                case "movies page" -> {
                    movieList = new ArrayList<>(countryPermittedMovies);
                    setPageName("movies page");
                    putActionOutput(true, node, output);
                }
                case "see details page" -> {
                    movieList = new ArrayList<>();
                    movieList.add(previousMovie);
                    setPageName("see details page");
                    putActionOutput(true, node, output);
                }
                case "upgrades page" -> setPageName("upgrades page");
                default -> System.out.println("ERROR! back change page");
            }
        }
    }
}
