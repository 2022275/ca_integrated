package mainmenu;
//Imports used.
import ioutils.IOUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import static mainmenu.DataBase.*;

//This class is used for writing into the database, it implements the DataBase interface.
public class dbWriter implements DataBase {

    //Method that allows to create new users and add them to the database.
    public static boolean createUser() throws SQLException {

        //IOUtils to collect user's input.
        IOUtils myInput = new IOUtils();

        //Variables to store the user's credentials, userName and password
        String userName = myInput.getUserText2("Enter username:");
        String userPassword = myInput.getUserText2("Enter password:");

        
        //It creates a new User object that holds the user's input.
        User newUser = new User();
        

        //Try catch to write the user's input into the database.
        try (
            //It connects to the database.   
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);  
            Statement stmt = conn.createStatement();) {
            //It defines how the info is going to be written into the table.
            //SQL is very case sensitve, so any mistakes here will cause errors. 
            String sql = String.format("INSERT INTO %s (id, first_name, last_name, age, marital_status, weekly_income) VALUES "
                + "(%d,'%s', '%s', %d, '%s', %.2f);",
                TABLE_NAME,
                //We use getters to access the user's information that was just created.
                newUser.getCurrentID(), 
                newUser.getFn(), 
                newUser.getLn(), 
                newUser.getAge(), 
                newUser.getMs(), 
                newUser.getWi());

            //What executes the commands written here to the database.
            stmt.execute(sql);

            //It uses a prepared statement for the user_credentials table to store the username and password into the user_credentials table.
            String credentialsSql = "INSERT INTO user_credentials (id, username, password) VALUES (?, ?, ?)";
            try ( PreparedStatement stmt2 = conn.prepareStatement(credentialsSql)) {

                //It saves the id, username and the password.
                stmt2.setInt(1, newUser.getCurrentID());
                stmt2.setString(2, userName);
                stmt2.setString(3, userPassword);

                //It executes and updates.
                stmt2.executeUpdate();

                return true;
            //Catch errors.
            } catch (Exception e) {
                System.out.println("CreateUser() - Failed to create new user.");
                e.printStackTrace();
                return false;
            }
        }
    }

    //Method to delete a user from the database by providing the user ID.
    public static boolean deleteUser(int userId) throws SQLException {
        //Way to prevent ADM's id deletion. 
        try{
            if(userId !=1){
        try (
            //It connects to the database and prepares to make statments in SQL.
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);  
            Statement stmt = conn.createStatement();) {
            //Simple SQL query to delete an user from the database by the id.
            String sql = String.format("DELETE FROM %s WHERE id = %d;", TABLE_NAME, userId);
            //It executes the command. 
            stmt.execute(sql);
            return true;
        } catch (Exception e) {
            System.out.println("DeleteUser() - Failed to delete user.");
            e.printStackTrace();
            return false;
        }
            }else{
            //Error message, deleting the ADM's id is not allowed.
            System.out.println("Denied! Can't delete admin's id!");
            return false;
            }
        }catch(Exception e){
        return false;
        }
    }  
    
//Method to update the admin's information.
public static boolean updateAdminInfo(int adminId, String newFirstName, String newLastName, int newAge, String newMaritalStatus, double newWeeklyIncome) {
    // This method updates the Admin info
    try (
        //It connects to the database.     
        Connection conn = DriverManager.getConnection(DB_BASE_URL, USER, PASSWORD);
        //It updates the table with SQL statments, in this case the key one is "UPDATE".
        PreparedStatement stmt = conn.prepareStatement("UPDATE userData SET first_name = ?, last_name = ?, age = ?, marital_status = ?, weekly_income = ? WHERE id = ?");    
    ) {
        //"USE" statement to select the database
        stmt.execute("USE " + DataBase.DB_NAME);
        //It sets the admin ID to retrieve information
        stmt.setInt(1, 1);
        
        //It sets the new values in the prepared statement
        stmt.setString(1, newFirstName);
        stmt.setString(2, newLastName);
        stmt.setInt(3, newAge);
        stmt.setString(4, newMaritalStatus);
        stmt.setDouble(5, newWeeklyIncome);
        stmt.setInt(6, adminId);

        //It executes the update statement
        stmt.executeUpdate();

        return true;
    } catch (SQLException e) {
        //Error message.
        System.out.println("updateAdminInfo() - Error updating admin information.");
        e.printStackTrace();
        return false;
    }
}

    
//Method to update the admin's information including first name, last name, age, marital status, and weekly income.
public static boolean updateUserInfo(int userId, String newFirstName, String newLastName, int newAge, String newMaritalStatus, double newWeeklyIncome) {
    // This method updates the regular user info
    try (
        //It connects to the database and updates the table.    
        Connection conn = DriverManager.getConnection(DB_BASE_URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement("UPDATE userData SET first_name = ?, last_name = ?, age = ?, marital_status = ?, weekly_income = ? WHERE id = ?");       
    ) {       
        //"USE" statement to select the database
        stmt.execute("USE " + DataBase.DB_NAME);
        // Set the admin ID to retrieve information
        stmt.setInt(1, 1);
        
        //It sets the new values in the prepared statement
        stmt.setString(1, newFirstName);
        stmt.setString(2, newLastName);
        stmt.setInt(3, newAge);
        stmt.setString(4, newMaritalStatus);
        stmt.setDouble(5, newWeeklyIncome);
        stmt.setInt(6, userId);

        //It executes the update statement
        stmt.executeUpdate();
        //If it all works out, return true.
        return true;
    } catch (SQLException e) {
        System.out.println("UpdateUserInfo() - Error updating user information.");
        e.printStackTrace();
        return false;}
     }   
}

