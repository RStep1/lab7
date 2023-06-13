package data;

/**
 * Used as a separate element in the database.
 */
public class Vehicle {
    private long id;
    private String name;
    private Coordinates coordinates;
    private String creationDate;
    private int enginePower;
    private long distanceTravelled;
    private VehicleType type;
    private FuelType fuelType;
    private String username;

    private static final int COUNT_OF_CHANGEABLE_FIELDS = 7;

    private Vehicle() {

    }

    public Vehicle(long id, String name, Coordinates coordinates,
                   String creationDate, int enginePower,
                   long distanceTravelled, VehicleType type, FuelType fuelType) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.distanceTravelled = distanceTravelled;
        this.type = type;
        this.fuelType = fuelType;
    }

    public Vehicle(long id, String name, Coordinates coordinates,
                   String creationDate, int enginePower,
                   long distanceTravelled, VehicleType type, FuelType fuelType, String username) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.distanceTravelled = distanceTravelled;
        this.type = type;
        this.fuelType = fuelType;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getEnginePower() {
        return enginePower;
    }

    public long getDistanceTravelled() {
        return distanceTravelled;
    }

    public VehicleType getType() {
        return type;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public static int getCountOfChangeableFields() {
        return COUNT_OF_CHANGEABLE_FIELDS;
    }

    public String getUsername() {
        return username;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreationDate(String date) {
        this.creationDate = date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("""
                        id:                 %s
                        name:               %s
                        coodrinates:        %s
                        creation date:      %s
                        engine power:       %s
                        distance travelled: %s
                        vehicle type:       %s (%s)
                        fuel type:          %s (%s)
                        owner:              %s
                        ____________________________________________________________""",
                id, name, coordinates, creationDate,
                enginePower, distanceTravelled, type.getSerialNumber(), type, fuelType.getSerialNumber(), fuelType, username);
    }
}
