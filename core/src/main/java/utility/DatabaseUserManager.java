package utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import data.User;
import processing.Console;

public class DatabaseUserManager {
    private final DatabaseHandler databaseHandler;

    private static final String INSERT_USER = "INSERT INTO " + 
                        DatabaseHandler.USERS_TABLE + " (" +
                        DatabaseHandler.USERS_TABLE_LOGIN_COLUMN + ", " +
                        DatabaseHandler.USERS_TABLE_PASSWORD_COLUMN + ") VALUES (?, ?)";
    private static final String SELECT_USER_BY_LOGIN = "SELECT * FROM " + DatabaseHandler.USERS_TABLE + 
                        " WHERE " + DatabaseHandler.USERS_TABLE_LOGIN_COLUMN + " = ?";
    
    public DatabaseUserManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
        // System.out.println(INSERT_USER);
    }
    

    public boolean insertUser(User user) {
        try (PreparedStatement preparedInserUser = databaseHandler.getPreparedStatement(INSERT_USER, false)) {
            System.out.println(user);
            preparedInserUser.setString(1, user.getLogin());
            preparedInserUser.setString(2, SHA256Hashing.hash(user.getPasword()));
            if (preparedInserUser.executeUpdate() == 0) 
                throw new SQLException();
            Console.println("INSERT_USER request completed");
            return true;
        } catch (SQLException e) {
            Console.println("An error occurred while executing the INSERT_USER query");
        }
        return false;
    }

    public User getUserByLogin(String login) {
        User user = null;
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(SELECT_USER_BY_LOGIN, false)) {
            preparedStatement.setString(1, login);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(
                        resultSet.getString(DatabaseHandler.USERS_TABLE_LOGIN_COLUMN),
                        resultSet.getString(DatabaseHandler.USERS_TABLE_PASSWORD_COLUMN)
                    );
                }
            } catch (SQLException exception) {
                Console.println("Error while executing SELECT_USER_BY_LOGIN query");
            }
        } catch (SQLException e) {
            Console.println("Error with prepared statement on SELECT_USER_BY_LOGIN query");
        }
        return user;
    }
}
