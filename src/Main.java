import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        //INSTATIATE 
        String inputPostalCode;
        Scanner scanner = new Scanner(System.in);

        //GET USER POSTAL CODE(CHECK FORMAT: LENGTH, INVALID)
        System.out.println("NB Hospital Distance Calculator\nEnter your postal code (format: A1A1A1): ");
        inputPostalCode = scanner.nextLine();

        //CALL FUNCTION TO GET COORDINATES FROM POSTAL CODE
        

        //GET INSTATIATE HOSPITAL DATA
        functions.HospitalData.loadHospitalData();
    }
}
