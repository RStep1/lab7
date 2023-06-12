package utility;

import java.util.concurrent.ConcurrentHashMap;

import data.Vehicle;


public class DatabaseCollectionManager {
    private final DatabaseHandler databaseHandler;

    public DatabaseCollectionManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public ConcurrentHashMap<Long, Vehicle> loadDataBase() {
        
        return new ConcurrentHashMap<>();
    }
    
}
