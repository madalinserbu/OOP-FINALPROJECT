import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AllMovies;
import database.AllUsers;
import execution.History;
import execution.RealTimePage;
import io.ActionIO;
import io.IO;

import java.io.File;
import java.io.IOException;

import static execution.Notification.recommendation;
import static execution.OnPageActions.isOnPageActionPermitted;
import static execution.OnPageActions.putActionOutput;
import static execution.OnPageActions.doOnPageAction;

public final class Main {
    private Main() {
    }
    /**main*/
    public static void main(final String[] args) throws IOException {
        ObjectMapper objMap = new ObjectMapper();
        IO input = objMap.readValue(new File(args[0]), IO.class);
        //se citeste input-ul

        AllUsers.getDatabase(input);
        AllMovies.getDatabase(input);
        // se contruiesc database-urile

        ArrayNode output = objMap.createArrayNode();
        // se parcurg actiunile
        for (int i = 0; i < input.actions().size(); i++) {
            ObjectNode node = objMap.createObjectNode();
            ActionIO action = input.actions().get(i);
            if (action.type().equals("change page")) {
                if (RealTimePage.getIt().isChangePagePermitted(action.page())) {
                    RealTimePage.getIt().changePage(action.type(), action.page(), action.movie(),
                            null, node, output);
                } else {
                    putActionOutput(false, node, output);
                }
            } else if (action.type().equals("on page")) {
                if (isOnPageActionPermitted(action.feature(), action.subscribedGenre())) {
                    doOnPageAction(input.actions().get(i), node, output);
                } else {
                    putActionOutput(false, node, output);
                }
            } else if (action.type().equals("database")) {
                switch (input.actions().get(i).feature()) {
                    case "add" -> AllMovies.addMovie(action.addedMovie(), node, output);
                    case "delete" -> AllMovies.deleteMovie(action.deletedMovie(), node, output);
                    default -> System.out.println("error database command");
                }
            } else if (action.type().equals("back")) {
                History.back(node, output);
            }
        }
        // in functie de tip se executa actiunile

        if (RealTimePage.getIt().getUser() != null
                && RealTimePage.getIt().getUser().getCredentials()
                .getAccountType().equals("premium")) {
            recommendation(output);
        }
        // se ofera recomandarea daca este cazul

        RealTimePage.getIt().setUser(null);
        RealTimePage.getIt().getMovieList().clear();
        RealTimePage.getIt().getCountryPermittedMovies().clear();
        RealTimePage.getIt().setPageName("logout page");
        History.getIt().clear();
        AllUsers.freeDatabase();
        AllMovies.freeDatabase();
        // se goleste tot pentru urmatorul test

        ObjectWriter objWrt = objMap.writerWithDefaultPrettyPrinter();
        objWrt.writeValue(new File(args[1]), output);
        //se afiseaza output-ul
    }
}
