package mainmenu;
//Imports used.
import TaxCalculator.TaxCalculator;
import ioutils.IOUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import static mainmenu.DataBase.DB_URL;
import static mainmenu.DataBase.PASSWORD;
import static mainmenu.DataBase.TABLE_NAME;
import static mainmenu.DataBase.USER;

//This is the Admin class.
public class Admin {

    //It declares a constant reference to the instance of the dbWriter class so I can use it in this class.
    //It needs to be initialized.
    private final dbWriter writer = new dbWriter();

    //IOUtils to let the admin input.
    IOUtils myInput = new IOUtils();
    //Method for the adminMenu.
    public void adminMenu() {

        //Boolean to control the menu loop.
        boolean leave = false;
        do {

            try {
                System.out.println();
                //Variable that will decide how the program will respond depending on the admin's choice.
                //The Admin have many options.
                int command = myInput.getUserInt("Welcome to the Admin menu! \n1-Modify Own Profile \n2-Create a new user \n3-List all Users \n4-Remove Users \n5-DELETE WHOLE SCHEMA \n6-Tax Calculator \n7-Check previous calculations \n8-Exit");
                //Switch to deal with the menu's options.
                switch (command) {
                    //Case 1 modifies the Admin's profile.
                    case 1:

                        System.out.println("Follow the instructions to modify your profile.");
                        //Method used to modify Amdin's info.
                        modifyAdminInfo();

                        break;
                    //Case 2 allows the admin to create new users. 
                    case 2:
                        
                        System.out.println("Follow the instructions to create a new user: ");
                        //Method to create new users. 
                        createUser();

                        break;
                    //Case 3 lists all users.
                    case 3:

                        System.out.println("List of all registred users:");
                        //Method used to list all users. 
                        listUsers();

                        break;
                    //Case 4 allows the admin to delete users by selecting an ID.
                    case 4:

                        int userId = myInput.getUserInt("Type the ID you want to delete.");
                        //It uses a method from the dbWriter class to delete users from the database. 
                        dbWriter.deleteUser(userId);

                        break;
                    //Case 5 allows the Admin to drop the whole schema and start it from the scratch. 
                    case 5:

                        //Option to drop Schema, it's good for testing. 
                        boolean dropSuccess = dbSetUp.dropSchema();
                        if (dropSuccess) {
                            System.out.println("Schema dropped successfully.");
                        } else {
                            System.out.println("Failed to drop schema.");
                        }
                        //It creates a new database after deleting the previous one to avoid database issues.
                        //It also makes sure to always keep the admin registred in the database.
                        dbSetUp.setupDB();
                        System.out.println("Fresh database created.");

                        break;
                    //Case 6 allows the admin to use the tax calculator.
                    case 6:

                        //Method to use the TaxCalculator.
                        TaxCalculator.runTaxCalculator();
                        System.out.println();

                        break;
                    //Case 7 allows the admin to check the previous calculations.
                    case 7:
                        //***************IT'S NOT FUNCTIONAL!!!*****************//
                        System.out.println("Previous calculations: ");
                        int userID = myInput.getUserInt("What ID would you like to check?");
                        // Display previous calculations for this user.
                        List<String> userCalculations = TaxCalculator.getCalculationsForUser(userID);
                            for (String calculation : userCalculations) {
                        System.out.println(calculation);}

                        break;
                    //Case 8 allows the admin to exit and go back to the main menu.
                    case 8:

                        System.out.println("See you again, admin!");
                        System.out.println("Logged out.");
                        leave = true;
                        break;
                    default:
                        System.out.println("Something in the Admin menu went wrong!");
                }//Error message.
            } catch (Exception e) {
                System.out.println("Something wrong with the try-catch admin menu!");
            } finally {
                System.out.println();
            }
        } while (!leave);
    }

    //Method that allows the admin to create new users and add them to the database.
    public static boolean createUser() throws SQLException {

        //IOUtils to collect admin's input.
        IOUtils myInput = new IOUtils();

        //Variables to store the user's credentials, userName and password
        String userName = myInput.getUserText("Enter username:");
        String userPassword = myInput.getUserText2("Enter password:");

        //It creates a new User object that holds the user's input.
        User newUser = new User();

        //Try catch to write the user's input into the database.
        try (
            //It connects to the database.    
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);  Statement stmt = conn.createStatement();) {
            // How the info is going to be written into the userData table.
            String userDataSql = String.format("INSERT INTO %s (id, first_name, last_name, age, marital_status, weekly_income) VALUES "
                + "(%d,'%s', '%s', %d, '%s', %.2f);",
                TABLE_NAME, 
                newUser.getId(), 
                newUser.getFn(), 
                newUser.getLn(), 
                newUser.getAge(), 
                newUser.getMs(), 
                newUser.getWi());

        //It executes the command to insert into the userData table.
        stmt.execute(userDataSql);

            //Using a prepared statement for the user_credentials table.
            String credentialsSql = "INSERT INTO user_credentials (username, password) VALUES (?, ?)";
            //Part dealing with the username.
            try ( PreparedStatement stmt2 = conn.prepareStatement(credentialsSql)) {

                //Setting values for placeholders, this will save the username and the password.
                stmt2.setString(1, userName);
                stmt2.setString(2, userPassword);

                //Executing the prepared statement.
                stmt2.executeUpdate();

                return true;
                //Catch errors.
            } catch (Exception e) {
                System.out.println("CreateUser() - Error creating new user.");
                e.printStackTrace();
                return false;
            }
        }
    }

    //Method to modify the admin's info.
    public void modifyAdminInfo() {
        try (
            //It connects to the database.
            Connection conn = DriverManager.getConnection(DataBase.DB_BASE_URL, DataBase.USER, DataBase.PASSWORD);  
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM userData WHERE id = ?");) {
            
            //It uses the "USE" statement to select the database
            stmt.execute("USE " + DataBase.DB_NAME);
            //It sets the admin ID to retrieve information
            stmt.setInt(1, 1);

            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //It retrieves admin information from the userData table.
                    int adminId = rs.getInt("id");
                    String adminFirstName = rs.getString("first_name");
                    String adminLastName = rs.getString("last_name");
                    int adminAge = rs.getInt("age");
                    String adminMaritalStatus = rs.getString("marital_status");
                    double adminWeeklyIncome = rs.getDouble("weekly_income");
                    System.out.println();
                    //It displays the current admin information.
                    System.out.println("Current Admin Information:");
                    System.out.println("First Name: " + adminFirstName);
                    System.out.println("Last Name: " + adminLastName);
                    System.out.println("Age: " + adminAge);
                    System.out.println("Marital Status: " + adminMaritalStatus);
                    System.out.println("Weekly Income: " + adminWeeklyIncome);
                    System.out.println();

                    //Then it lers the admin modify the new first and last name.
                    IOUtils myInput = new IOUtils();
                    String newFN = myInput.getUserText("Enter new first name:");
                    String newLN = myInput.getUserText("Enter new last name:");
                    //We kept the age, marital status and income always set as default. 
                    int newAge = 0;
                    String newMaritalStatus = "x";
                    double newWeeklyIncome = 0.0;

                    //It updates the admin's information in the userData table using a method from the dbWriter class. 
                    writer.updateAdminInfo(adminId, newFN, newLN, newAge, newMaritalStatus, newWeeklyIncome);
                    System.out.println();
                    //Then it displays the updated admin information
                    System.out.println("Admin Information Updated Successfully:");
                    System.out.println("First Name: " + newFN);
                    System.out.println("Last Name: " + newLN);
                    System.out.println("Age: " + newAge);
                    System.out.println("Marital Status: " + newMaritalStatus);
                    System.out.println("Weekly Income: " + newWeeklyIncome);
                    System.out.println();
                } else {
                    //Error message.
                    System.out.println("Admin not found in the database.");
                }
            }
        } catch (SQLException e) {
            System.out.println("ModifyAdminInfo() - Error updating admin's info.");
            e.printStackTrace();
        }
    }

    //Method to list all users with their details already registred in the database.
    public void listUsers() throws SQLException {
        //It initializes the dbReader. 
        dbReader reader = new dbReader();
        //ArrayList to store and loop to display the users' data
        ArrayList<User> allUsers = reader.getAllUsers();

        //Displaying user's data with an enhanced loop.
        for (User user : allUsers) {
            System.out.println(
                    "ID: " + user.getId()
                    + ", First Name: " + user.getFn()
                    + ", Last Name: " + user.getLn()
                    + ", Age: " + user.getAge()
                    + ", Marital Status: " + user.getMs()
                    + ", Weekly Income: " + user.getWi()
            );
        }
        System.out.println();
    }

    //Method to delete users by inputting their IDs. 
    public void deleteUser(int userId) throws SQLException {

        //It calls the static method from dbWriter that is a method to drop users from the table in the database.
        boolean success = dbWriter.deleteUser(userId);

        //It checks and display a message of the result.
        if (success) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user.");
        }
    }    
}