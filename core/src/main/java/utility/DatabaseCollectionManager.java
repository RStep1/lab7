package utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import data.Coordinates;
import data.FuelType;
import data.Vehicle;
import data.VehicleType;
import processing.Console;


public class DatabaseCollectionManager {
    private final DatabaseHandler databaseHandler;

    private static final String SELECT_ALL_VEHICLES = "SELECT * FROM " + DatabaseHandler.VEHICLE_TABLE;
    private static final String SELECT_COORDINATES_BY_ID = "SELECT * FROM " + DatabaseHandler.COORDINATES_TABLE + 
                        " WHERE " + DatabaseHandler.COORDINATES_TABLE_ID_COLUMN + " = ?"; 

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
        long coordinatesId = resultSet.getInt(DatabaseHandler.VEHICLE_TABLE_COORDINATES_ID_COLUMN);
        Coordinates coordinates = getCoordinatesById(coordinatesId);
        int enginePower = resultSet.getInt(DatabaseHandler.VEHICLE_TABLE_ENGINE_POWER_COLUMN);
        long distanceTravelled = resultSet.getLong(DatabaseHandler.VEHICLE_TABLE_DISTANCE_TRAVELLED_COLUMN);
        String stringVehicleType = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_VEHICLE_TYPE_COLUMN);
        String stringFuelType = resultSet.getString(DatabaseHandler.VEHICLE_TABLE_FUEL_TYPE_COLUMN);
        VehicleType vehicleType = ValueTransformer.SET_VEHICLE_TYPE.apply(stringVehicleType);
        FuelType fuelType = ValueTransformer.SET_FUEL_TYPE.apply(stringFuelType);
        return new Vehicle(id, name, coordinates, creationDate, enginePower, distanceTravelled, vehicleType, fuelType);
    }
    
    private Coordinates getCoordinatesById(long id) {
        Coordinates coordinates = null;
        try (PreparedStatement preparedStatement = databaseHandler.getPreparedStatement(SELECT_COORDINATES_BY_ID, false)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coordinates = new Coordinates(
                        resultSet.getFloat(DatabaseHandler.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getDouble(DatabaseHandler.COORDINATES_TABLE_Y_COLUMN)
                    );
                }
            }
        } catch (SQLException e) {
            Console.println("Failed to get coordinates by their id");
        }
        return coordinates;
    }
}
