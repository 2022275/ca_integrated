package mainmenu;
//Imports used.
import ioutils.IOUtils;
import java.sql.SQLException;
//This class is used to handle the logins.
public class LoginAuthenticator {

    //Method to handle the logins.
    public static void handleLogin() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        //Import IOUtils to collect users' input
        IOUtils myInput = new IOUtils();
        
        //Variables to store the username and password inputs.
        String usernameLogin = myInput.getUserText2("Hello! Enter your username:");
        String passwordLogin = myInput.getUserText2("Enter your password:");

        //It runs the dbReader to check the user_credentials tables.
        dbReader reader = new dbReader();

        //It checks if the entered credentials are for the administrator
        if (reader.validateAdmin(usernameLogin, passwordLogin)) {
            //If so, Admin login successful.
            System.out.println("Administrator login successful!");
            //It creates an instance of Admin so we can use the adminMenu method.
            Admin admin = new Admin();
            //It checks for the database everytime the Admin is logging in.
            boolean setupDB = dbSetUp.setupDB();
            //It calls the adminMenu method.
            admin.adminMenu();
            System.out.println();
            
        } else {
            //It checks if the regulars user's credentials.
            if (reader.validateUser(usernameLogin, passwordLogin)) {
                System.out.println("Regular user login successful!");
                //It creates an instance of a regularUser so we can use the userMenu.
                User regularUser = new User("x");
                //It checks for the database everytime a regular user logs in.
                boolean setupDB2 = dbSetUp.setupDB();
                //It calls the regularUserMenu method.
                regularUser.regularUserMenu();           
                System.out.println();
            } else {
                //Error message.
                System.out.println("Login failed. Invalid username or password.");
                System.out.println();
            }
        }
    }
    
}