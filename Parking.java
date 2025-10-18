import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

// create object class
class ParkingRecord {
    String Date, Time_in, PlateNo, Vehicle, Time_out;
    int Slot;
    double Hours, Fee;
    Date Entry_date;

    // object variable assignment
    public ParkingRecord(String date, String TimeIn, String PlateNo, String Vehicle, int Slot, Date Entry_date) {
        this.Date = Date;
        this.Time_in = TimeIn;
        this.PlateNo = PlateNo;
        this.Vehicle = Vehicle;
        this.Slot = Slot;
        this.Time_out = "Still Parked";
        this.Hours = 0.0;
        this.Fee = 0.0;
        this.Entry_date = Entry_date;
    }

    public void updateOnExit(String timeOut, double hours, double fee) {
        this.Time_out = timeOut;
        this.Hours = hours;
        this.Fee = fee;
    }

    
    public String toString() {
        return String.format("%-10s | %-8s | %-8s | %-9s | %-4d | %-13s | %-6.2f | $%-5.2f",
                Date, Time_in, PlateNo, Vehicle, Slot, Time_out, Hours, Fee);
    }

    public String toFileString() {
        return Date + "," + Time_in + "," + PlateNo + "," + Vehicle + "," + Slot + "," + Time_out + "," + Hours + "," + Fee;
    }
}

public class ParkingLotManagementSystem {

    static Scanner sc = new Scanner(System.in);

    private static HashMap<String, ParkingRecord> CurrentParked = new HashMap<>();
    private static ArrayList<ParkingRecord> AllRecords = new ArrayList<>(); // create array list with variable AllRecords
    private static int capacity = 5;
    private static double feePerHour = 5.0;
    private static int nextSlot = 1;

    public static void main(String[] args) {
        String choice;

        while (true) {
            System.out.println("\n=== PARKING LOT MENU ===");
            System.out.println("A. Park a Vehicle");
            System.out.println("B. Remove a Vehicle");
            System.out.println("C. View all Parking Records");
            System.out.println("D. Save Records to File");
            System.out.println("E. Exit");
            System.out.print("Enter your choice (A-E): ");
            choice = sc.nextLine().trim().toUpperCase();

            // choice validation
            switch (choice) {
                case "A" -> Addvehicle(sc);
                case "B" -> RemoveVehicle(sc);
                case "C" -> PrintparkingRecords();
                case "D" -> SaveTofile();
                case "E" -> {
                    System.out.println("Exiting.");
                    SaveTofile();
                    return;
                }
                default -> System.out.println("Invalid input. Enter a valid letter from A-E.");
            }
        }
    }

    // method for adding a vehicle
    public static void Addvehicle(Scanner sc) {

        // condition to check if there is still capacity for parking space
        if (CurrentParked.size() >= capacity) {
            System.out.println("Parking is full.");
            return;
        }

        // asks for user plate number
        System.out.print("Enter your plate number: ");
        String plateNo = sc.nextLine().toUpperCase();

        if (CurrentParked.containsKey(plateNo)) {
            System.out.println("vehicle is already parked.");
            return;
        }
        if (plateNo.isEmpty()) {
            System.out.println("Plate number cannot be empty!");
            return;
        }

        System.out.print("Enter what type of vehicle (Car/Van/Motorcycle): ");
        String vehicle = sc.nextLine();

        // condition to check if vehicle type is valid
        if (!vehicle.equalsIgnoreCase("Car") &&
            !vehicle.equalsIgnoreCase("Motorcycle") &&
            !vehicle.equalsIgnoreCase("Van")) {
            System.out.println("Invalid vehicle type! Please enter Car, Motorcycle, or Van!");
            return;
        }

        Date now = new Date();
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd"); // formatting for date
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss"); // formatting for time
        String date = dateFmt.format(now);
        String timeIn = timeFmt.format(now);
        int slot = nextSlot++; // automatically set for the next slot

        ParkingRecord record = new ParkingRecord(date, timeIn, plateNo, vehicle, slot, now);
        CurrentParked.put(plateNo, record);
        AllRecords.add(record);

        System.out.println(plateNo + " (" + vehicle + ") parked in slot " + slot + ".");
        SaveTofile(); // save to file automatically
    }

    // method for removing vehicle
    public static void RemoveVehicle(Scanner sc) {

        // asks for user plate number
        System.out.print("Enter plate number: ");
        String plateNo = sc.nextLine().toUpperCase();

        if(plateNo.isEmpty()) {
            System.out.println("Plate number cannot be empty!");
            return;
        }

        ParkingRecord record = CurrentParked.get(plateNo);
        if (record == null) {
            System.out.println("Vehicle can't be found.");
            return;
        }

        Date exitDate = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
        long timeDiffMs = exitDate.getTime() - record.Entry_date.getTime();

        // fee calculation
        double hours = timeDiffMs / (1000.0 * 60 * 60);
        double fee = Math.round(hours * feePerHour * 100) / 100.0;
        String timeOut = timeFmt.format(exitDate);

        record.updateOnExit(timeOut, hours, fee);
        CurrentParked.remove(plateNo);

        System.out.println(plateNo + " left. Hours: " + String.format("%.2f", hours) + ", Fee: $" + fee + ".");
        SaveTofile(); // save to database
    }

    // method for view all parked vehicles
    public static void PrintparkingRecords() {

        // condition to check if AllRecords is empty
        if (AllRecords.isEmpty()) {
            System.out.println("No records yet.");
            return;
        }

        // print table format
        System.out.println("\n--- Parking Records ---");
        System.out.println("Date       | Time In  | Plate No | Vehicle   | Slot | Time Out      | Hours  | Fee");
        System.out.println("--------------------------------------------------------------------------------");

        // loop inside record array list and input the correct datas
        for (ParkingRecord record : AllRecords) {
            System.out.println(record.toString());
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total records: " + AllRecords.size());
    }

    // method for saving data in databaase
    public static void SaveTofile() {

        // creates a file named parking_records.txt for database
        try (FileWriter writer = new FileWriter("parking_records.txt")) {
            for (ParkingRecord record : AllRecords) {
                writer.write(record.toFileString() + "\n");
            }
            System.out.println("Records saved to parking_records.txt successfully.");
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
}
