package mainmenu;
//Imports used.
import TaxCalculator.TaxCalculator;
import static TaxCalculator.TaxCalculator.storeCalculationsForUser;
import ioutils.IOUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class User {

    //Atributes.
    //User's id.
    private int id;
    //User's user name. 
    private String un;
    //User's first name.
    private String fn;
    //User's last name.
    private String ln;
    //User's age.
    private int age;
    //User's marital status.
    private String ms;
    //User's weekly income.
    private double wi;

    //We use this to auto generate the patient ids
    private static int currentID = 1;

    //Constructor for creating a new user (for registration, etc.)
    //It collects the user's input and use it to construct the User object. 
    public User() {
        //IOUtils to collect user's input.
        IOUtils myInput = new IOUtils();
        //Assigns the next id to the currentID.
        this.id = currentID;
        this.fn = myInput.getUserText("What's your first name?");
        this.ln = myInput.getUserText("What's your last name?");
        this.age = myInput.getUserInt("What's your age name?");

        System.out.println("Select your marital status or exit:");
        // Get user choice
        int choice = myInput.getUserInt("1-Single \n2-Single with children \n3-Married (Both Spouses with Income) \n4-Married (One Spouse with Income) \n5-Exit", 1, 5);

        // Determine marital status based on user choice
        String maritalStatus = "";
        switch (choice) {
            case 1:
                maritalStatus = "single";
                break;

            case 2:
                maritalStatus = "single with children";
                break;

            case 3:
                maritalStatus = "married_both_earners";
                break;
            case 4:
                maritalStatus = "married_one_earner";
                break;
            case 5:
                System.out.println("Exiting the program.");
                return; // Exit the program
            default:
                System.out.println("Invalid choice.");

        }
        this.ms = maritalStatus;
        this.wi = myInput.getUserDecimal("What's your weekly income?");
        //Increment the currentID for the next user
        currentID++;
    }

    //Constructor for creating a user from the database's data.
    public User(int id, String fn, String ln, int age, String ms, double wi) {
        this.id = id;
        this.fn = fn;
        this.ln = ln;
        this.age = age;
        this.ms = ms;
        this.wi = wi;

        //If the retrieved ID is greater than the currentID, update currentID
        if (id >= currentID) {
            currentID = id + 1;
        }
    }

    //Constructor used to initialize the regularUserMenu, no parameters needed in this case. 
    public User(String regularUser) {
        
    }

    //Method to display a regularUser menu whenever a regular user logs in.
    public void regularUserMenu() {

        
        //IOUtils to let the admin input.
        IOUtils myInput = new IOUtils();
        //Boolean to control the menu loop.
        boolean leave = false;
        do {

            try {
                System.out.println();
                //Variable that will decide how the program will respond depending on the admin's choice.
                //The regular user have 4 options, 1 to use the tax calculator, 2 to modify their own profile, 3 to review their calculations and 4 to exit. 
                int command = myInput.getUserInt("Welcome to the Tax Calculator menu! \n1-Use the Tax Calculator \n2-Modify own Profile \n3-Review own Calculations \n4-Exit");
                //Switch to deal with the user's choice. 
                switch (command) {
                    //Case 1 to use the tax calculator.
                    case 1:

                        System.out.println("Follow the instructions to calculate your Taxes.");
                        TaxCalculator.runTaxCalculator();
                        // Store the calculations for this user.
                        storeCalculationsForUser(getId());

                        break;
                    //Case 2 to modify their own profile.
                    case 2:

                        System.out.println("Follow the instructions to modify your profile: ");
                        modifyProfile();

                        break;
                    //Case 3 to see their previous calculations. 
                    case 3:

                        System.out.println("My previous calculations: ");
                        //Display previous calculations for this user.
                        //Here we used an arraylist to store the results of all the calculations. 
                        //Used an enhanced loop to display the results stored into the arraylist.
                        List<String> userCalculations = TaxCalculator.getCalculationsForUser(getId());
                            for (String calculation : userCalculations) {
                        System.out.println(calculation);
                    }
                        break;
                    //Case 4 to exit the regular user menu and go back to the main menu. 
                    case 4:

                        System.out.println("See you again!");
                        System.out.println("Logged out.");
                        leave = true;

                        break;
                    //Default error message. 
                    default:

                        System.out.println("Something in the regular user menu went wrong!");
                }
            //Catch and finally. 
            } catch (Exception e) {
                System.out.println("Something wrong with the try-catch regular user menu!");
            } finally {
                System.out.println();
            }
        //The loop will keep looing as long as leave is false. 
        } while (!leave);
    }

    //Initializes dbWriter so we can be able to use their methods.
    private final dbWriter writer = new dbWriter();
    
        //Method that allows the regular users to modify their own profiles. 
        //It's also directly connected to the database so all the modifications are also changed in the database. 
        public void modifyProfile() {
        try (
            //It connects to the database through the DriverManager. It uses the database's information from the Database interface. 
            Connection conn = DriverManager.getConnection(DataBase.DB_BASE_URL, DataBase.USER, DataBase.PASSWORD);  
            //PreparedStatement that will give the commands in workbench in SQL. 
            //In this case we're selecting the table userData using the id to find the user. 
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM userData WHERE id = ?");) {
            
            //Command "USE" statement to make sure to use the selected database
            stmt.execute("USE " + DataBase.DB_NAME);
            //It sets the admin ID to retrieve information
            stmt.setInt(1, 1);
            //Now the database returns the values found and we execute the query of changing the profile. 
            try ( ResultSet rs = stmt.executeQuery()) {
                //It reads through the columns. 
                if (rs.next()) {
                    //It iterates through the table and collects the information, the informations are being stored into variables.
                    int userId = rs.getInt("id");
                    String userFirstName = rs.getString("first_name");
                    String userLastName = rs.getString("last_name");
                    int userAge = rs.getInt("age");
                    String userMaritalStatus = rs.getString("marital_status");
                    double userWeeklyIncome = rs.getDouble("weekly_income");
                    System.out.println();
                    //It displays the retrieved user information from the userData table so the user can confirm their own profile.
                    System.out.println("Current user Information:");
                    System.out.println("First Name: " + userFirstName);
                    System.out.println("Last Name: " + userLastName);
                    System.out.println("Age: " + userAge);
                    System.out.println("Marital Status: " + userMaritalStatus);
                    System.out.println("Weekly Income: " + userWeeklyIncome);
                    System.out.println();

                    //It allows the user to modify their information and we store them into new variables. 
                    IOUtils myInput = new IOUtils();
                    String newFN = myInput.getUserText("Enter new first name:");
                    String newLN = myInput.getUserText("Enter new last name:");
                    int newAge = myInput.getUserInt("What's your age?");
                   
                    int choice = myInput.getUserInt("What is your marital status? \n1-Single \n2-Single with children \n3-Married (Both Spouses with Income) \n4-Married (One Spouse with Income)", 1, 4);
                    // Determine marital status based on user choice
                    String maritalStatus = "";
                    switch (choice) {
                        case 1:
                            maritalStatus = "single";
                            break;

                        case 2:
                            maritalStatus = "single with children";
                            break;

                        case 3:
                            maritalStatus = "married_both_earners";
                            break;
                        case 4:
                            maritalStatus = "married_one_earner";
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
                    String newMaritalStatus = maritalStatus;
                    
                    
                    double newWeeklyIncome = myInput.getUserDecimal("What's your weekly income?");

                    //After collecting all the new information we use the updateUserInfo method to update the userData table.
                    //We use the values we just collected as parameter to the method being used. 
                    //This method will be further explained in its class. 
                    writer.updateUserInfo(userId, newFN, newLN, newAge, newMaritalStatus, newWeeklyIncome);
                    System.out.println();
                    //Display updated user information.
                    System.out.println("User Information Updated Successfully:");
                    System.out.println("First Name: " + newFN);
                    System.out.println("Last Name: " + newLN);
                    System.out.println("Age: " + newAge);
                    System.out.println("Marital Status: " + newMaritalStatus);
                    System.out.println("Weekly Income: " + newWeeklyIncome);
                    System.out.println();
                //Error message specifying where the error occurred. 
                } else {
                    System.out.println("User ModifyProfile() - User not found in the database.");
                }
            }
        //Catch with stackTrace.     
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    //It returns the currentID. 
    public static int getCurrentID() {
        return currentID;
    }

    //Setters and Getters.
    public String getUn() {
        return un;
    }

    public String getMs() {
        return ms;
    }

    public double getWi() {
        return wi;
    }

    public int getId() {
        return id;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }

    public void setWi(double wi) {
        this.wi = wi;
    }

    public static void setCurrentID(int currentID) {
        User.currentID = currentID;
    }

}
