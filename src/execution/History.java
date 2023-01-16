package execution;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Movie;

import java.util.Stack;

import static execution.OnPageActions.putActionOutput;

/** istoricul paginilor/filmelor (in cazul painilor 'see details') */
public final class History {
    private final Stack<String> pageHistory;
    private final Stack<Movie> movieHistory;
    private static History it = null;

    /**singleton*/
    public static History getIt() {
        if (it == null) {
            it = new History();
        }
        return it;
    }

    public History() {
        pageHistory = new Stack<>();
        movieHistory = new Stack<>();
    }

    /** elibereaza istoricul */
    public void clear() {
        pageHistory.clear();
        movieHistory.clear();
        it = null;
    }

    /** metoda este responsabila pentru a ne intoarce pe pagina precedenta */
    public static void back(final ObjectNode node, final ArrayNode output) {
        if (it != null) {
            if (History.getIt().pageHistory.size() > 0) {
                String previousPage = History.getIt().pageHistory.pop();
                if (previousPage.equals("see details")) {
                    Movie previousMovie = History.getIt().movieHistory.pop();
                    RealTimePage.getIt().changePage("back", previousPage, null, previousMovie,
                            node, output);
                } else {
                    RealTimePage.getIt().changePage("back", previousPage, null, null, node,
                            output);
                }
            } else {
                putActionOutput(false, node, output);
            }
        } else {
            putActionOutput(false, node, output);
        }
    }

    public Stack<String> getPageHistory() {
        return pageHistory;
    }

    public Stack<Movie> getMovieHistory() {
        return movieHistory;
    }
}
