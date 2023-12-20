package mainmenu;
//Imports.
import ioutils.IOUtils;
import java.sql.SQLException;

public class MainMenu {

    //Main code, where all the pieces come together.
    //Here we also throw all the necessary exceptions. 
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        //It initializes the database by calling the setupDB method.
        if (dbSetUp.setupDB()) {
            //Success message.
            System.out.println("Database setup successful.");
            System.out.println();
        } else {
            //Error message. 
            System.out.println("Database setup failed.");
        }

        //Try catch to run the main menu. 
        try {

            //Ioutils to collect user's input.
            IOUtils myInput = new IOUtils();
            //Boolean variable to control for how long the menu will keep on being looped.
            boolean leave = false;

            //Loop for the menu, it will keep looping until the variable leave changes to true. 
            //The idea of the main menu is to give 3 options, 1 to log-in in case the user already have an account. 
            //2 for registration and 3 to exit. 
            do {
                //Variable that will decide how the program will respond depending on the user's choice.
                int command = myInput.getUserInt("Welcome to the tax calculator! You need to log-in to use it. \n1-Login \n2-Register \n3-Exit");

                //Switch to work with the menu.
                switch (command) {

                    //Case 1 allows users to login. Both the administrator and regular users.
                    case 1:
                        //It uses the LoginAuthenticator class to handle the login proccess. 
                        LoginAuthenticator.handleLogin();
                        System.out.println();
                        break;

                    //Case 2 allows to register a new user.
                    case 2:

                        //It initializes the dbWriter so we can write into the database.
                        dbWriter dbw = new dbWriter();
                        //It calls the createUser method to create a new user.
                        if (dbw.createUser()) {
                            //If it succeeds a message will confirm it. 
                            System.out.println("User added successfully!");
                        } else {
                            //If it fails a message will be displayed as well.
                            System.out.println("Failed to add user.");
                        }
                        System.out.println();
                        break;

                    //Case 3 exits the program. 
                    case 3:
                        System.out.println("Thanks for using the tax calculator!");
                        //It changes the state of the boolean variable leave to true and the loop ends.
                        leave = true;
                        System.out.println();
                        break;

                    //Default error message.
                    default:
                        System.out.println("Oops! Select available options only.");
                        System.out.println();
                }

                //The loop will continue as long as leave is false.
            } while (!leave);

        //Catch to avoid the code from crashing.
        } catch (Exception e) {
            //Error message specifying what part of the code went wrong. 
            System.out.println("MainMenu - Something went very bad!");
            System.out.println();
            //Error tracer.
            e.printStackTrace();
        //Finally, last message to the user.
        } finally {
            System.out.println("Have a great day!");
        }
    }
}
