import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

class ParkingRecord {
    String Date, Time_in, PlateNo, Vehicle, Time_out;
    int Slot;
    double Hours, Fee;
    Date Entry_date;

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

    private static HashMap<String, ParkingRecord> CurrentParked = new HashMap<>();
    private static ArrayList<ParkingRecord> AllRecords = new ArrayList<>();
    private static int capacity = 5;
    private static double feePerHour = 5.0;
    private static int nextSlot = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
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

    
    public static void Addvehicle(Scanner sc) {
        if (CurrentParked.size() >= capacity) {
            System.out.println("Parking is full.");
            return;
        }

        System.out.print("Enter your plate number: ");
        String plateNo = sc.nextLine().toUpperCase();

        if (CurrentParked.containsKey(plateNo)) {
            System.out.println("vehicle is already parked.");
            return;
        }

        System.out.print("Enter what type of vehicle (Car/Motorcycle): ");
        String vehicle = sc.nextLine();

        Date now = new Date();
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
        String date = dateFmt.format(now);
        String timeIn = timeFmt.format(now);
        int slot = nextSlot++;

        ParkingRecord record = new ParkingRecord(date, timeIn, plateNo, vehicle, slot, now);
        CurrentParked.put(plateNo, record);
        AllRecords.add(record);

        System.out.println(plateNo + " (" + vehicle + ") parked in slot " + slot + ".");
    }

    
    public static void RemoveVehicle(Scanner sc) {
        System.out.print("Enter plate number: ");
        String plateNo = sc.nextLine().toUpperCase();

        ParkingRecord record = CurrentParked.get(plateNo);
        if (record == null) {
            System.out.println("Vehicle can't be found.");
            return;
        }

        Date exitDate = new Date();
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm:ss");
        long timeDiffMs = exitDate.getTime() - record.Entry_date.getTime();
        double hours = timeDiffMs / (1000.0 * 60 * 60);
        double fee = Math.round(hours * feePerHour * 100) / 100.0;
        String timeOut = timeFmt.format(exitDate);

        record.updateOnExit(timeOut, hours, fee);
        CurrentParked.remove(plateNo);

        System.out.println(plateNo + " left. Hours: " + String.format("%.2f", hours) + ", Fee: $" + fee + ".");
    }

    
    public static void PrintparkingRecords() {
        if (AllRecords.isEmpty()) {
            System.out.println("No records yet.");
            return;
        }

        System.out.println("\n--- Parking Records ---");
        System.out.println("Date       | Time In  | Plate No | Vehicle   | Slot | Time Out      | Hours  | Fee");
        System.out.println("--------------------------------------------------------------------------------");

        for (ParkingRecord record : AllRecords) {
            System.out.println(record.toString());
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total records: " + AllRecords.size());
    }

    
    public static void SaveTofile() {
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
