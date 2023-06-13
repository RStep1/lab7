package utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import data.Coordinates;
import data.FuelType;
import data.User;
import data.Vehicle;
import data.VehicleType;
import processing.Console;


public class DatabaseCollectionManager {
    private final DatabaseHandler databaseHandler;

    private static final String SELECT_ALL_VEHICLES = "SELECT * FROM " + DatabaseHandler.VEHICLE_TABLE;

    private static final String INSERT_VEHICLE = "INSERT INTO " + DatabaseHandler.VEHICLE_TABLE + " (" + 
                        DatabaseHandler.VEHICLE_TABLE_KEY_COLUMN + ", " +
                        DatabaseHandler.VEHICLE_TABLE_NAME_COLUMN + ", " + 
                        DatabaseHandler.VEHICLE_TABLE_X_COLUMN + ", " +
                        DatabaseHandler.VEHICLE_TABLE_Y_COLUMN + ", " +
                        DatabaseHandler.VEHICLE_TABLE_CREATION_DATE_COLUMN + ", " +
                        DatabaseHandler.VEHICLE_TABLE_ENGINE_POWER_COLUMN + ", " + 
                        DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN + ", " + 
                        DatabaseHandler.VEHICLE_TABLE_VEHICLE_TYPE_COLUMN + ", " + 
                        DatabaseHandler.VEHICLE_TABLE_FUEL_TYPE_COLUMN + ", " + 
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_VEHICLE_NAME_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_NAME_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_X_COORDINATE_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_X_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_Y_COORDINATE_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_Y_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_ENGINE_POWER_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_ENGINE_POWER_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_DISTANCE_TRAVELLED_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_TYPE_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_VEHICLE_TYPE_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";
    private static final String UPDATE_VEHICLE_FUEL_TYPE_BY_ID = "UPDATE " + DatabaseHandler.VEHICLE_TABLE + " SET " + 
                        DatabaseHandler.VEHICLE_TABLE_FUEL_TYPE_COLUMN + " = ? WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";

    public DatabaseCollectionManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public ConcurrentHashMap<Long, Vehicle> loadDataBase() {
        ConcurrentHashMap<Long, Vehicle> database = new ConcurrentHashMap<>();
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(SELECT_ALL_VEHICLES, false)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    long key = resultSet.getLong(DatabaseHandler.VEHICLE_TABLE_KEY_COLUMN);
                    database.put(key, createVehicle(resultSet));
                }
            }
        } catch (SQLException e) {
            Console.println("Failed to load collection from database");
        }
        return database;
    }

    private Vehicle createVehicle(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(DatabaseHandler.VEHICLE_TABLE_ID_COLUMN);
        String name = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_NAME_COLUMN);
        String creationDate = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_CREATION_DATE_COLUMN);
        float x = resultSet.getFloat(DatabaseHandler.VEHICLE_TABLE_X_COLUMN);
        double y = resultSet.getDouble(DatabaseHandler.VEHICLE_TABLE_Y_COLUMN);
        int enginePower = resultSet.getInt(DatabaseHandler.VEHICLE_TABLE_ENGINE_POWER_COLUMN);
        long distanceTravelled = resultSet.getLong(DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN);
        String stringVehicleType = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_VEHICLE_TYPE_COLUMN);
        String stringFuelType = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_FUEL_TYPE_COLUMN);
        VehicleType vehicleType = ValueTransformer.SET_VEHICLE_TYPE.apply(stringVehicleType);
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(stringFuelType);
        Coordinates coordinates = new Coordinates(x, y);
        return new Vehicle(id, name, coordinates, creationDate, enginePower, distanceTravelled, vehicleType, fuelType);
    }
    

    public long insertVehicle(long key, Vehicle vehicle, User user) {
        long id = -1;
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(INSERT_VEHICLE, true)) {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedStatement.setLong(1, key);
            preparedStatement.setString(2, vehicle.getName());
            preparedStatement.setFloat(3, vehicle.getCoordinates().getX());
            preparedStatement.setDouble(4, vehicle.getCoordinates().getY());
            preparedStatement.setString(5, vehicle.getCreationDate());
            preparedStatement.setInt(6, vehicle.getEnginePower());
            preparedStatement.setLong(7, vehicle.getDistanceTravelled());
            preparedStatement.setString(8, vehicle.getType().toString());
            preparedStatement.setString(9, vehicle.getFuelType().toString());
            preparedStatement.setString(10, user.getLogin());
            if (preparedStatement.executeUpdate() == 0) {
                throw new SQLException();
            }

            ResultSet keyResultSet = preparedStatement.getGeneratedKeys();
            if (keyResultSet.next()) {
                id = keyResultSet.getLong(1);
            }

            databaseHandler.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            Console.println("Fail");
        } finally {
            databaseHandler.setNormalMode();
        }
        return id;
    }

    public void updateVehicleById(Vehicle vehicle, User user) {
        try (PreparedStatement preparedUpdateName = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateX = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateY = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateEnginePower = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateDistanceTravelled = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateType = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateFuelType = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false)) {
                databaseHandler.setCommitMode();
                databaseHandler.setSavepoint();
                long id = vehicle.getId();
                String login = user.getLogin();

                preparedUpdateName.setString(1, vehicle.getName());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setFloat(1, vehicle.getCoordinates().getX());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setDouble(1, vehicle.getCoordinates().getY());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setInt(1, vehicle.getEnginePower());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setLong(1, vehicle.getDistanceTravelled());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setString(1, vehicle.getType().toString());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateName.setString(1, vehicle.getFuelType().toString());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                if (preparedUpdateName.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateX.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateY.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateEnginePower.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateDistanceTravelled.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateType.executeUpdate() == 0) throw new SQLException();
                if (preparedUpdateFuelType.executeUpdate() == 0) throw new SQLException();
                Console.println("query UPDATE_VEHICLE_BY_ID successfully completed");
                databaseHandler.commit();
            } catch (SQLException e) {
                databaseHandler.rollback();
                Console.println("Fail to upate element on id = " + vehicle.getId() + " and login = " + user.getLogin());
            } finally {
                databaseHandler.setNormalMode();
            }
        
    }

    public void deleteVehicleByID(long id) {

    }
}
