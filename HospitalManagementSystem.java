import java.util.*;

public class HospitalManagementSystem {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<String> patientNames = new ArrayList<>();
    static ArrayList<Integer> patientAges = new ArrayList<>();

    static ArrayList<String> doctorNames = new ArrayList<>();
    static ArrayList<String> doctorSpecialization = new ArrayList<>();

    static ArrayList<String> appointments = new ArrayList<>();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n=================================");
            System.out.println("   HOSPITAL MANAGEMENT SYSTEM");
            System.out.println("=================================");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Add Doctor");
            System.out.println("4. View Doctors");
            System.out.println("5. Book Appointment");
            System.out.println("6. View Appointments");
            System.out.println("7. Search Patient");
            System.out.println("8. Generate Bill");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch(choice){

                case 1:
                    addPatient();
                    break;

                case 2:
                    viewPatients();
                    break;

                case 3:
                    addDoctor();
                    break;

                case 4:
                    viewDoctors();
                    break;

                case 5:
                    bookAppointment();
                    break;

                case 6:
                    viewAppointments();
                    break;

                case 7:
                    searchPatient();
                    break;

                case 8:
                    generateBill();
                    break;

                case 9:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while(choice != 9);

    }

    static void addPatient(){

        System.out.print("Enter Patient Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();

        patientNames.add(name);
        patientAges.add(age);

        System.out.println("Patient Added Successfully.");
    }

    static void viewPatients(){

        if(patientNames.size()==0){
            System.out.println("No Patients Found.");
            return;
        }

        System.out.println("\n------ Patients ------");

        for(int i=0;i<patientNames.size();i++){

            System.out.println((i+1)+". "+patientNames.get(i)+" | Age: "+patientAges.get(i));

        }

    }

    static void addDoctor(){

        System.out.print("Enter Doctor Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Specialization: ");
        String spec = sc.nextLine();

        doctorNames.add(name);
        doctorSpecialization.add(spec);

        System.out.println("Doctor Added Successfully.");
    }

    static void viewDoctors(){

        if(doctorNames.size()==0){
            System.out.println("No Doctors Available.");
            return;
        }

        System.out.println("\n------ Doctors ------");

        for(int i=0;i<doctorNames.size();i++){

            System.out.println((i+1)+". Dr. "+doctorNames.get(i)+" - "+doctorSpecialization.get(i));

        }

    }

    static void bookAppointment(){

        if(patientNames.size()==0 || doctorNames.size()==0){

            System.out.println("Add Patients and Doctors First.");
            return;
        }

        System.out.print("Patient Name: ");
        String patient = sc.nextLine();

        System.out.print("Doctor Name: ");
        String doctor = sc.nextLine();

        System.out.print("Appointment Date: ");
        String date = sc.nextLine();

        appointments.add("Patient: "+patient+
                " | Doctor: "+doctor+
                " | Date: "+date);

        System.out.println("Appointment Booked Successfully.");
    }

    static void viewAppointments(){

        if(appointments.size()==0){

            System.out.println("No Appointments.");
            return;
        }

        System.out.println("\n------ Appointments ------");

        for(String a : appointments){

            System.out.println(a);

        }

    }

    static void searchPatient(){

        System.out.print("Enter Patient Name: ");
        String search = sc.nextLine();

        boolean found=false;

        for(int i=0;i<patientNames.size();i++){

            if(patientNames.get(i).equalsIgnoreCase(search)){

                System.out.println("Patient Found");
                System.out.println("Name : "+patientNames.get(i));
                System.out.println("Age  : "+patientAges.get(i));
                found=true;
                break;

            }

        }

        if(!found){

            System.out.println("Patient Not Found.");

        }

    }

    static void generateBill(){

        System.out.print("Enter Patient Name: ");
        String patient = sc.nextLine();

        System.out.print("Consultation Fee: ");
        double consult = sc.nextDouble();

        System.out.print("Medicine Charges: ");
        double medicine = sc.nextDouble();

        System.out.print("Room Charges: ");
        double room = sc.nextDouble();
        sc.nextLine();

        double total = consult + medicine + room;

        System.out.println("\n========== BILL ==========");
        System.out.println("Patient : "+patient);
        System.out.println("Consultation : ₹"+consult);
        System.out.println("Medicine     : ₹"+medicine);
        System.out.println("Room Charges : ₹"+room);
        System.out.println("--------------------------");
        System.out.println("Total Amount : ₹"+total);
        System.out.println("==========================");

    }
}
