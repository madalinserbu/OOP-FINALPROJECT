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

//import static execution.Notification.recommendation;
import static execution.OnPageActions.*;

public class Main {
    /**main*/
    public static void main(final String[] args) throws IOException {
        ObjectMapper objMap = new ObjectMapper();
        IO input = objMap.readValue(new File(args[0]), IO.class);

        AllUsers.getDatabase(input);
        AllMovies.getDatabase(input);
        ArrayNode output = objMap.createArrayNode();
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

        if (RealTimePage.getIt().getUser() != null &&
            RealTimePage.getIt().getUser().getCredentials().getAccountType().equals("premium")) {
            //recommendation(output);
        }

        RealTimePage.getIt().setUser(null);
        RealTimePage.getIt().getMovieList().clear();
        RealTimePage.getIt().getCountryPermittedMovies().clear();
        RealTimePage.getIt().setPageName("logout page");
        History.getIt().clear();
        AllUsers.freeDatabase();
        AllMovies.freeDatabase();

        ObjectWriter objWrt = objMap.writerWithDefaultPrettyPrinter();
        objWrt.writeValue(new File(args[1]), output);

        char[] inPath = args[0].toCharArray();
        String outPath;
        if (inPath[inPath.length - 6] == '0') {
            outPath = "checker\\resources\\out\\" + args[1] + "10.json";
        } else {
            outPath = "checker\\resources\\out\\" + args[1]
                    + inPath[inPath.length - 6] + ".json";
        }
        objWrt.writeValue(new File(outPath), output);
    }
}
