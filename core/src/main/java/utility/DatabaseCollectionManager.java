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
import mods.RemoveMode;
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

    private static final String SELECT_VEHICLE_BY_ID_AND_LOGIN = "SELECT * FROM " +
                        DatabaseHandler.VEHICLE_TABLE + " WHERE " +
                        DatabaseHandler.VEHICLE_TABLE_ID_COLUMN + " = ? AND " + 
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";

    private static final String DELETE_VEHICLE_BY_KEY = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_KEY_COLUMN + " = ?";

    private static final String DELETE_VEHICLE_BY_LOGIN = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " + 
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ?";

    private static final String DELETE_VEHICLE_GREATER_THAN_DISTANCE_TRAVELLED = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ? AND " + 
                        DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN + " > ?";
    private static final String DELETE_VEHICLE_LOWER_THAN_DISTANCE_TRAVELLED = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ? AND " + 
                        DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN + " < ?";
    
    private static final String DELETE_VEHICLE_GREATER_THAN_KEY = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ? AND " + 
                        DatabaseHandler.VEHICLE_TABLE_KEY_COLUMN + " > ?";

    private static final String DELETE_VEHICLE_BY_ENGINE_POWER = "DELETE FROM " + DatabaseHandler.VEHICLE_TABLE + " WHERE " +
                        DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN + " = ? AND " + 
                        DatabaseHandler.VEHICLE_TABLE_ENGINE_POWER_COLUMN + " = ?";

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
        String login = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_USER_LOGIN_COLUMN);
        VehicleType vehicleType = ValueTransformer.SET_VEHICLE_TYPE.apply(stringVehicleType);
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(stringFuelType);
        Coordinates coordinates = new Coordinates(x, y);
        return new Vehicle(id, name, coordinates, creationDate, enginePower, distanceTravelled, vehicleType, fuelType, login);
    }
    

    public long insertVehicle(long key, Vehicle vehicle, User user) throws SQLException {
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
            Console.println("Failed to execute INSERT_VEHICLE query");
            throw new SQLException("Failed to insert element to database by key = " + key);
        } finally {
            databaseHandler.setNormalMode();
        }
        return id;
    }

    public void updateVehicleByIdAndLogin(Vehicle vehicle, User user) throws SQLException {
        try (PreparedStatement preparedUpdateName = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_NAME_BY_ID, false);
            PreparedStatement preparedUpdateX = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_X_COORDINATE_BY_ID, false);
            PreparedStatement preparedUpdateY = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_Y_COORDINATE_BY_ID, false);
            PreparedStatement preparedUpdateEnginePower = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_ENGINE_POWER_BY_ID, false);
            PreparedStatement preparedUpdateDistanceTravelled = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_DISTANCE_TRAVELLED_BY_ID, false);
            PreparedStatement preparedUpdateType = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_TYPE_BY_ID, false);
            PreparedStatement preparedUpdateFuelType = databaseHandler.getPreparedStatement(UPDATE_VEHICLE_FUEL_TYPE_BY_ID, false)) {
                databaseHandler.setCommitMode();
                databaseHandler.setSavepoint();
                long id = vehicle.getId();
                String login = user.getLogin();

                preparedUpdateName.setString(1, vehicle.getName());
                preparedUpdateName.setLong(2, id);
                preparedUpdateName.setString(3, login);

                preparedUpdateX.setFloat(1, vehicle.getCoordinates().getX());
                preparedUpdateX.setLong(2, id);
                preparedUpdateX.setString(3, login);

                preparedUpdateY.setDouble(1, vehicle.getCoordinates().getY());
                preparedUpdateY.setLong(2, id);
                preparedUpdateY.setString(3, login);

                preparedUpdateEnginePower.setInt(1, vehicle.getEnginePower());
                preparedUpdateEnginePower.setLong(2, id);
                preparedUpdateEnginePower.setString(3, login);

                preparedUpdateDistanceTravelled.setLong(1, vehicle.getDistanceTravelled());
                preparedUpdateDistanceTravelled.setLong(2, id);
                preparedUpdateDistanceTravelled.setString(3, login);

                preparedUpdateType.setString(1, vehicle.getType().toString());
                preparedUpdateType.setLong(2, id);
                preparedUpdateType.setString(3, login);

                preparedUpdateFuelType.setString(1, vehicle.getFuelType().toString());
                preparedUpdateFuelType.setLong(2, id);
                preparedUpdateFuelType.setString(3, login);

                if (preparedUpdateName.executeUpdate() == 0 || preparedUpdateX.executeUpdate() == 0 || preparedUpdateY.executeUpdate() == 0 ||
                    preparedUpdateEnginePower.executeUpdate() == 0 || preparedUpdateDistanceTravelled.executeUpdate() == 0 ||
                    preparedUpdateType.executeUpdate() == 0 || preparedUpdateFuelType.executeUpdate() == 0)
                     throw new SQLException();
                
                databaseHandler.commit();
                Console.println("query UPDATE_VEHICLE_BY_ID successfully completed");
            } catch (SQLException e) {
                databaseHandler.rollback();
                e.printStackTrace();
                Console.println("Failed to upate element by id = " + vehicle.getId() + " and login = " + user.getLogin());
                throw new SQLException("Failed to update element in database by id = " + vehicle.getId());
            } finally {
                databaseHandler.setNormalMode();
            }
    }

    public boolean hasVehicleWithIdAndLogin(long id, String login) {
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(SELECT_VEHICLE_BY_ID_AND_LOGIN, false)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, login);
            if (!preparedStatement.executeQuery().next()) {
                return false;
            }
        } catch (SQLException e) {
            Console.println("Failed to execute SELECT_VEHICLE_BY_ID_AND_LOGIN query");
        }
        return true;
    }

    public void deleteByKey(long key) throws SQLException {
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(DELETE_VEHICLE_BY_KEY, false)) {
            preparedStatement.setLong(1, key);
            if (!preparedStatement.executeQuery().next()) {
                throw new SQLException();
            }
            Console.println("DELETE_VEHICLE_BY_KEY");
        } catch (SQLException e) {
            Console.println("Failed to delete vehicle by key = " + key);
            throw new SQLException("Failed to delete element from database by key");
        }
    }

    public void deleteByLogin(String login) throws SQLException {
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(DELETE_VEHICLE_BY_LOGIN, false)) {
            preparedStatement.setString(1, login);
            int deleted = preparedStatement.executeUpdate();
            Console.println("DELETE_VEHICLE_BY_LOGIN: count of deleted = " + deleted);
        } catch (SQLException e) {
            Console.println(String.format("Failed to DELETE_VEHICLE_BY_LOGIN '%s", login));
            throw new SQLException("Failed to clear collection");
        }
    }

    public void deleteByDistanceTravelled(long distanceTravelled, String login, RemoveMode removeMode) throws SQLException {
        if (removeMode == RemoveMode.REMOVE_GREATER) {
            deleteGreaterByDistanceTravelled(distanceTravelled, login);
        } else {
            deleteLowerByDistanceTravelled(distanceTravelled, login);
        }
    }

    private void deleteGreaterByDistanceTravelled(long distanceTravelled, String login) throws SQLException {
        try (PreparedStatement preparedStatement = 
            databaseHandler.getPreparedStatement(DELETE_VEHICLE_GREATER_THAN_DISTANCE_TRAVELLED, false)) {
            preparedStatement.setString(1, login);
            preparedStatement.setLong(2, distanceTravelled);
            int deleted = preparedStatement.executeUpdate();
            Console.println("DELETE_VEHICLE_GREATER_THAN_DISTANCE_TRAVELLED: deleted = " + deleted);
        } catch (SQLException e) {
            Console.println("Failed to DELETE_VEHICLE_GREATER_THAN_DISTANCE_TRAVELLED");
            throw new SQLException("Failed to delete elements greater than distance travelled from database");
        }
    }

    private void deleteLowerByDistanceTravelled(long distanceTravelled, String login) throws SQLException {
        try (PreparedStatement preparedStatement = 
            databaseHandler.getPreparedStatement(DELETE_VEHICLE_LOWER_THAN_DISTANCE_TRAVELLED, false)) {
            preparedStatement.setString(1, login);
            preparedStatement.setLong(2, distanceTravelled);
            int deleted = preparedStatement.executeUpdate();
            Console.println("DELETE_VEHICLE_LOWER_THAN_DISTANCE_TRAVELLED: deleted = " + deleted);
        } catch (SQLException e) {
            Console.println("Failed to DELETE_VEHICLE_LOWER_THAN_DISTANCE_TRAVELLED");
            throw new SQLException("Failed to delete elements lower than distance travelled from database");
        }
    }

    public void deleteByGreaterKey(long key, String login) throws SQLException {
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(DELETE_VEHICLE_GREATER_THAN_KEY, false)) {
            preparedStatement.setString(1, login);
            preparedStatement.setLong(2, key);
            int deleted = preparedStatement.executeUpdate();
            Console.println("DELETE_VEHICLE_GREATER_THAN_KEY: deleted = " + deleted);
        } catch (SQLException e) {
            Console.println("Failed to DELETE_VEHICLE_GREATER_THAN_KEY query");
            throw new SQLException("Failed to delete elements greater than key from database");
        }
    }

    public void deleteByEnginePower(int enginePower, String login) throws SQLException {
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(DELETE_VEHICLE_BY_ENGINE_POWER, false)) {
            preparedStatement.setString(1, login);
            preparedStatement.setInt(2, enginePower);
            int deleted = preparedStatement.executeUpdate();
            Console.println("DELETE_VEHICLE_BY_ENGINE_POWER: deleted = " + deleted);
        } catch (SQLException e) {
            Console.println("Failed to DELETE_VEHICLE_BY_ENGINE_POWER query");
            throw new SQLException("Failed to delete elements by engine power = " + enginePower);
        }
    }
}
