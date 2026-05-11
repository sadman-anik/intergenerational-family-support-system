import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// Database username and password are loaded from .env file.
public class DatabaseUtility {
    private String MYSQL_URL;
    final String DB_URL;

    private Connection sqlConnection;
    Connection dbConnection;
    private Statement statement;

    private final String dbCreateSQL;
    private String USER_NAME;
    private String PASSWORD;

    private final String TABLE_MEMBERS_QRY;
    private final String TABLE_CHILDREN_QRY;
    private final String TABLE_EVENTS_QRY;

    private final String TABLE_SET_FIRST_MEMBER_ID;
    private final String TABLE_SET_FIRST_EVENT_ID;

    public DatabaseUtility() {
        Properties env = loadEnv();

        String dbHost = env.getProperty("DB_HOST", "localhost");
        String dbPort = env.getProperty("DB_PORT", "3306");
        String dbName = env.getProperty("DB_NAME", "igfss");

        USER_NAME = env.getProperty("DB_USER", "root");
        PASSWORD = env.getProperty("DB_PASSWORD", "");

        MYSQL_URL = "jdbc:mysql://" + dbHost + ":" + dbPort;
        DB_URL = MYSQL_URL + "/" + dbName;

        dbCreateSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;

        TABLE_MEMBERS_QRY = "CREATE TABLE IF NOT EXISTS MEMBERS "
                + "(fidn INTEGER not NULL AUTO_INCREMENT, "
                + "memberType VARCHAR(30) NOT NULL, "
                + "spouse1Name VARCHAR(50) NOT NULL, "
                + "spouse2Name VARCHAR(50) NOT NULL, "
                + "spouse1Phone VARCHAR(20) NOT NULL, "
                + "spouse2Phone VARCHAR(20) NOT NULL, "
                + "spouse1Email VARCHAR(80) NOT NULL UNIQUE, "
                + "spouse2Email VARCHAR(80) NOT NULL, "
                + "address VARCHAR(150) NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "yearsMarried INTEGER, "
                + "PRIMARY KEY (fidn) )";

        TABLE_CHILDREN_QRY = "CREATE TABLE IF NOT EXISTS CHILDREN "
                + "(childId INTEGER not NULL AUTO_INCREMENT, "
                + "fidn INTEGER NOT NULL, "
                + "gender VARCHAR(20) NOT NULL, "
                + "age INTEGER NOT NULL, "
                + "PRIMARY KEY (childId), "
                + "FOREIGN KEY (fidn) REFERENCES MEMBERS(fidn) "
                + "ON DELETE CASCADE )";

        TABLE_EVENTS_QRY = "CREATE TABLE IF NOT EXISTS EVENTS "
                + "(eventId INTEGER not NULL AUTO_INCREMENT, "
                + "fidn INTEGER NOT NULL, "
                + "eventType VARCHAR(50) NOT NULL, "
                + "eventDate VARCHAR(30) NOT NULL, "
                + "eventTime VARCHAR(30) NOT NULL, "
                + "duration VARCHAR(50) NOT NULL, "
                + "venue VARCHAR(100) NOT NULL, "
                + "cost DOUBLE NOT NULL, "
                + "PRIMARY KEY (eventId), "
                + "FOREIGN KEY (fidn) REFERENCES MEMBERS(fidn) "
                + "ON DELETE CASCADE )";

        TABLE_SET_FIRST_MEMBER_ID = "ALTER TABLE MEMBERS AUTO_INCREMENT = 501";
        TABLE_SET_FIRST_EVENT_ID = "ALTER TABLE EVENTS AUTO_INCREMENT = 1001";

        statement = null;
        initialiseDatabase();
    }

    private Properties loadEnv() {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("../.env")) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Could not load .env file: " + e.getMessage());
            System.out.println("Using default database configuration.");
        }

        return props;
    }

    private void initialiseDatabase() {
        try {
            sqlConnection = DriverManager.getConnection(MYSQL_URL, USER_NAME, PASSWORD);
            statement = sqlConnection.createStatement();

            boolean dbExists = false;
            DatabaseMetaData dbmd = sqlConnection.getMetaData();
            ResultSet dbData = dbmd.getCatalogs();

            while (dbData.next()) {
                String databaseName = dbData.getString(1);
                if (databaseName.equalsIgnoreCase("igfss")) {
                    dbExists = true;
                }
            }

            if (!dbExists) {
                statement.executeUpdate(dbCreateSQL);
            }

            if (sqlConnection != null) {
                sqlConnection.close();
            }

            dbConnection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            statement = dbConnection.createStatement();

            createTables();

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console.");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        statement.executeUpdate(TABLE_MEMBERS_QRY);
        statement.executeUpdate(TABLE_CHILDREN_QRY);
        statement.executeUpdate(TABLE_EVENTS_QRY);

        statement.executeUpdate(TABLE_SET_FIRST_MEMBER_ID);
        statement.executeUpdate(TABLE_SET_FIRST_EVENT_ID);
    }

    public Connection getDbConnection() {
        return dbConnection;
    }
}