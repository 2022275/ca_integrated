package mainmenu;
public interface DataBase {
    
    //Interface to define the details of the database and establish connection in a safe and controlled way.
    static final String DB_BASE_URL = "jdbc:mysql://localhost";
    static final String USER = "ooc2023";
    static final String PASSWORD = "ooc2023";
    static final String DB_NAME = "Integrated_CA";
    static final String TABLE_NAME = "userData";
    static final String DB_URL = DB_BASE_URL + "/" + DB_NAME;    
    
}

