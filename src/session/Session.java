package session;

import model.User;

public class Session {
    private static User currentUser;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }

    public static boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }

    public static boolean isLoggedIn() { return currentUser != null; }
}
