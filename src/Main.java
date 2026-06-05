import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        //INSTATIATE 
        String userPostalCode;
        Scanner scanner = new Scanner(System.in);

        //GET USER POSTAL CODE(CHECK FORMAT: LENGTH, INVALID, UPPERCASE)
        System.out.println("NB Hospital Distance Calculator\nEnter your postal code (format: A1A1A1): ");
        userPostalCode = scanner.nextLine();
        userPostalCode = userPostalCode.toUpperCase();

        //CALL FUNCTION TO GET COORDINATES FROM POSTAL CODE
        double[] userLocation = functions.PostalConverter.getLocation(userPostalCode);
        while (userLocation == null) {
            System.out.println("Invalid postal code. Please enter a valid postal code (format: A1A1A1): ");
            userPostalCode = scanner.nextLine();
            userPostalCode = userPostalCode.toUpperCase();
            userLocation = functions.PostalConverter.getLocation(userPostalCode);
        }
        System.out.format("Your location: (%.6f, %.6f)\n", userLocation[0], userLocation[1]);

        //GET INSTATIATE HOSPITAL DATA
        functions.HospitalData.loadHospitalData();//i think we need to use both user location and hospital data at the same time 
    }
}