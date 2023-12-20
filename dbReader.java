package mainmenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static mainmenu.DataBase.*;

//This class is used for reading information from the database. It implements the DataBase interface. 
public class dbReader implements DataBase {

    //Method to retrieve all users registered in the database.
    public static ArrayList<User> getAllUsers() throws SQLException {
            //The idea of this method is to create an ArrayList so we can read from the database.
            //While we select the info from the database, we create an object to hold the information.
            //We add the objects with the information from the database so we can loop and display the info.
            ArrayList<User> users = new ArrayList<>();

        try ( 
            //It connects to the database.
            Connection conn = DriverManager.getConnection(DB_BASE_URL, USER, PASSWORD);  
            Statement stmt = conn.createStatement()) {

            //It makes sure to select the database before executing queries.
            String useDatabaseQuery = "USE " + DB_NAME;
            stmt.executeUpdate(useDatabaseQuery);
            //We use Result to conduct searches in the database, in this case we are selecting the userData table.
            ResultSet results = stmt.executeQuery(String.format("SELECT * FROM %s;", TABLE_NAME));
            //Loop to go through thre table and retrieve the information.
            //We store the found information into the variables declared below. 
            while (results.next()) {
                int userId = results.getInt("id");
                String userFN = results.getString("first_name");
                String userLN = results.getString("last_name");
                int userAge = results.getInt("age");
                String userMS = results.getString("marital_status");
                double userWI = results.getDouble("weekly_income");

                //It uses the appropriate constructor based on whether the user is new or if they are already existent in the database.
                users.add(new User(userId, userFN, userLN, userAge, userMS, userWI));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //Error message.
            System.out.println("getAllUsers() - Sorry, there was an error retrieving the users from the arrayList.");
        }
        //It returns the List.
        return users;
    }


    //This method is for retrieving the login information and validating it.
    public static boolean validateUser(String username, String password) {
        try (
            //It connects to the database.
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD); 
            //It selects from the table user_credentials, where the usernames and passworsds are stored.
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_credentials WHERE username = ? AND password = ?");) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            //It executes the query by reading the username and the password. 
            ResultSet results = stmt.executeQuery();
            //And finally it returns true if there's a matching user. Login validated.
            return results.next();

        } catch (Exception e) {
            //Default error message.
            System.out.println("validateUser() - No matches found.");
            e.printStackTrace();
            return false;
        }
    }

    //This method validates the admin's credentials.
    public boolean validateAdmin(String username, String password) {
        //These are the admin's credentials.
        String adminUsername = "CCT";
        String adminPassword = "Dublin";
        //It just checks if the input matches with the pre-defined credentials and return the result 
        //as a boolean. 
        return adminUsername.equals(username) && adminPassword.equals(password);
    }
}
