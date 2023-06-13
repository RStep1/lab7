package utility;

import java.sql.*;

import processing.Console;

public class DatabaseHandler {
    public static final String USERS_TABLE = "USERS";
    public static final String VEHICLE_TABLE = "VEHICLE";
    public static final String COORDINATES_TABLE = "COORDINATES";

    public static final String USERS_TABLE_LOGIN_COLUMN = "LOGIN";
    public static final String USERS_TABLE_PASSWORD_COLUMN = "PASSWORD";

    public static final String VEHICLE_TABLE_ID_COLUMN = "ID";
    public static final String VEHICLE_TABLE_KEY_COLUMN = "KEY";
    public static final String VEHICLE_TABLE_NAME_COLUMN = "NAME";
    public static final String VEHICLE_TABLE_CREATION_DATE_COLUMN = "CREATION_DATE";
    public static final String VEHICLE_TABLE_ENGINE_POWER_COLUMN = "ENGINE_POWER";
    public static final String VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN = "DISTANCE_TRAVELLED";
    public static final String VEHICLE_TABLE_VEHICLE_TYPE_COLUMN = "VEHICLE_TYPE";
    public static final String VEHICLE_TABLE_FUEL_TYPE_COLUMN = "FUEL_TYPE";
    public static final String VEHICLE_TABLE_USER_LOGIN_COLUMN = "USER_LOGIN";
    public static final String VEHICLE_TABLE_X_COLUMN = "X_COORD";
    public static final String VEHICLE_TABLE_Y_COLUMN = "Y_COORD";

    public static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + " (" +
                        USERS_TABLE_LOGIN_COLUMN + " TEXT PRIMARY KEY NOT NULL, " + 
                        USERS_TABLE_PASSWORD_COLUMN + " TEXT NOT NULL)";
    public static final String CREATE_VEHICLE_TABLE = "CREATE TABLE IF NOT EXISTS " + VEHICLE_TABLE + " (" + 
                        VEHICLE_TABLE_ID_COLUMN + " SERIAL PRIMARY KEY, " + 
                        VEHICLE_TABLE_KEY_COLUMN + " BIGINT NOT NULL, " + 
                        VEHICLE_TABLE_NAME_COLUMN + " TEXT, " +
                        VEHICLE_TABLE_X_COLUMN + " REAL, " +
                        VEHICLE_TABLE_Y_COLUMN + " DOUBLE PRECISION, " + 
                        VEHICLE_TABLE_CREATION_DATE_COLUMN + " TEXT, " + 
                        VEHICLE_TABLE_ENGINE_POWER_COLUMN + " INT, " +
                        VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN + " BIGINT, " +
                        VEHICLE_TABLE_VEHICLE_TYPE_COLUMN + " TEXT, " +
                        VEHICLE_TABLE_FUEL_TYPE_COLUMN + " TEXT, " + 
                        VEHICLE_TABLE_USER_LOGIN_COLUMN + " TEXT REFERENCES " + 
                            USERS_TABLE + "(" + USERS_TABLE_LOGIN_COLUMN + "))";

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    
    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseHandler(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        connectToDatabase();
        createTables();
    }
    
    private void connectToDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение с базой данных установлено");
        } catch (SQLException exception) {
            // exception.printStackTrace();
            System.out.println("Произошла ошибка при подключении к базе данных");
        } catch (ClassNotFoundException exception) {
            System.out.println("Драйвер управления базой дынных не найден");
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_USERS_TABLE);
            statement.execute(CREATE_VEHICLE_TABLE);
            Console.println("Tables created successfully");
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Ошибка при создании таблиц");
        } catch (NullPointerException exception) {
            System.out.println("Невозможно создать statement без подключения");
        }
    }

    public PreparedStatement getPreparedStatement(String sqlStatement, boolean generateKeys) throws SQLException {
        PreparedStatement preparedStatement;
        try {
            if (connection == null) 
                throw new SQLException();
            int autoGeneratedKeys = generateKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
            preparedStatement = connection.prepareStatement(sqlStatement, autoGeneratedKeys);
            System.out.println("Подготовлен SQL запрос '" + sqlStatement + "'.");
            return preparedStatement;
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при подготовке SQL запроса '" + sqlStatement + "'.");
            if (connection == null)
                System.out.println("Соединение с базой данных не установлено!");
            throw new SQLException(exception);
        }
    }

    public void closePreparedStatement(PreparedStatement sqlStatement) {
        if (sqlStatement == null)
            return;
        try {
            sqlStatement.close();
            Console.println("Закрыт SQL запрос '" + sqlStatement + "'.");
        } catch (SQLException exception) {
            Console.println("Произошла ошибка при закрытии SQL запроса '" + sqlStatement + "'.");
        }
    }

    public void closeConnection() {
        if (connection == null)
            return;
        try {
            connection.close();
            Console.println("Соединение с базой данных разорвано.");
        } catch (SQLException exception) {
            Console.println("Произошла ошибка при разрыве соединения с базой данных!");
        }
    }

    public void setCommitMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при установлении режима транзакции базы данных!");
        }
    }

    public void setNormalMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при установлении режима транзакции базы данных!");
        }
    }

    public void commit() {
        try {
            if (connection == null) throw new SQLException();
            connection.commit();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при подтверждении нового состояния базы данных!");
        }
    }

    public void rollback() {
        try {
            if (connection == null) throw new SQLException();
            connection.rollback();
        } catch (SQLException exception) {
            Console.println("Произошла ошибка при возврате исходного состояния базы данных!");
        }
    }

    public void setSavepoint() {
        try {
            if (connection == null) throw new SQLException();
            connection.setSavepoint();
        } catch (SQLException exception) {
            System.out.println("Произошла ошибка при сохранении состояния базы данных!");
        }
    }

    public static String makeSqlQuery(String... arguments) {
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < arguments.length - 1; i++) {
            query.append(arguments[i] + " ");
        }
        query.append(arguments[arguments.length - 1] + ";");
        Console.println(query.toString());
        return query.toString();
    }
}

