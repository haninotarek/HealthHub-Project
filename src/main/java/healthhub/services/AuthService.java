package healthhub.services;

import healthhub.dao.UserDAO;
import healthhub.models.User;

public class AuthService {

    private static User currentUser = null;
    private static UserDAO userDAO = new UserDAO();

    // ── Login ──
    public static boolean login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login success: " + user.getFullName());
            return true;
        }

        System.out.println("Login failed: wrong credentials");
        return false;
    }

    // ── Logout ──
    public static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    // ── Get Current User ──
    public static User getCurrentUser() {
        return currentUser;
    }

    // ── Is Logged In ──
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}