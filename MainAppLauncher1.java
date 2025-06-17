import java.util.Scanner;
public class MainAppLauncher1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Project Launcher ===");
        System.out.println("1. SmartCalculator4");
        System.out.println("2. FileDBExecutor1");
        System.out.println("3. DatabaseMenuApp");
        System.out.print("Choose an option (1-3): ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                SmartCalculator4.main(new String[]{});
                break;
            case 2:
                FileDBExecutor1.main(new String[]{});
                break;
            case 3:
                DatabaseMenuApp.main(new String[]{});
                break;
            default:
			
                System.out.println("Invalid choice.");
        }
        scanner.close();
    }
}