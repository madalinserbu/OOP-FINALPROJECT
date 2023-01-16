package database;

import io.IO;
import io.UserIO;

import java.util.ArrayList;

/**baza de date useri*/
public final class AllUsers {
    private static AllUsers database = null;
    private final ArrayList<User> allUsers;
    private AllUsers(final IO input) {
        allUsers = new ArrayList<>();
        for (UserIO userIO : input.users()) {
            User user = new User(userIO);
            allUsers.add(user);
        }
    }

    /**singleton*/
    public static AllUsers getDatabase(final IO input) {
        if (database == null) {
            database = new AllUsers(input);
        }
        return database;
    }

    public static AllUsers getDatabase() {
        return database;
    }

    /**goleste baza de date la finalul unui test*/
    public static void freeDatabase() {
        database = null;
    }

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

}
