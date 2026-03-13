import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        occupied = false;
    }
}

public class ParkingLot {

    private static final int SIZE = 500;

    private ParkingSpot[] table = new ParkingSpot[SIZE];

    private int occupiedSpots = 0;
    private int totalProbes = 0;
    private int parkOperations = 0;

    public ParkingLot() {
        for (int i = 0; i < SIZE; i++)
            table[i] = new ParkingSpot();
    }

    // Hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % SIZE; // linear probing
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedSpots++;
        totalProbes += probes;
        parkOperations++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].occupied) {

            if (table[index].licensePlate.equals(plate)) {

                long durationMs =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMs / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                occupiedSpots--;

                System.out.println("exitVehicle(\"" + plate + "\") → Spot #" +
                        index + " freed, Duration: " +
                        String.format("%.2f", hours) +
                        "h, Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestAvailable() {

        for (int i = 0; i < SIZE; i++) {
            if (!table[i].occupied)
                return i;
        }

        return -1;
    }

    // Parking statistics
    public void getStatistics() {

        double occupancy = (occupiedSpots * 100.0) / SIZE;

        double avgProbes =
                parkOperations == 0 ? 0 :
                        (double) totalProbes / parkOperations;

        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");

        System.out.println("Avg Probes: " +
                String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}