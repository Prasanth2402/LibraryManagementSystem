import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        // sample books
        service.addBook(new Book("B101", "Java Programming", "Herbert Schildt"));
        service.addBook(new Book("B102", "Clean Code", "Robert Martin"));
        service.addBook(new Book("B103", "Data Structures", "Mark Weiss"));
        service.addBook(new Book("B104", "Wings of Fire", "APJ Abdul Kalam"));

        while (true) {
            printMenu();
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: addBook(); break;
                    case 2: searchBook(); break;
                    case 3: showAvailability(); break;
                    case 4: requestIssue(); break;
                    case 5: approveIssue(); break;
                    case 6: returnBook(); break;
                    case 7: fineCalculator(); break;
                    case 8: dueDateReminder(); break;
                    case 9: service.printReport(); break;
                    case 10: changeDate(); break;
                    case 0:
                        System.out.println("Goodbye!");
                        return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== Library Management System =====");
        System.out.println("Today: " + service.getToday());
        System.out.println("-- Student --");
        System.out.println("2. Search book");
        System.out.println("3. Book availability");
        System.out.println("4. Request book issue");
        System.out.println("-- Librarian --");
        System.out.println("1. Add book");
        System.out.println("5. Approve book issue");
        System.out.println("6. Return book & collect fine");
        System.out.println("7. Fine calculator");
        System.out.println("8. Due date reminder");
        System.out.println("9. Generate report");
        System.out.println("-- Other --");
        System.out.println("10. Change today's date (for testing fines)");
        System.out.println("0. Exit");
    }

    private static void addBook() {
        System.out.print("Book ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Author: ");
        String author = sc.nextLine().trim();
        service.addBook(new Book(id, title, author));
        System.out.println("Book added.");
    }

    private static void searchBook() {
        System.out.print("Enter title or author: ");
        List<Book> result = service.searchBook(sc.nextLine().trim());
        if (result.isEmpty()) {
            System.out.println("No books found.");
        }
        for (Book b : result) {
            System.out.println(b);
        }
    }

    private static void showAvailability() {
        for (Book b : service.getAllBooks()) {
            System.out.println(b);
        }
    }

    private static void requestIssue() {
        System.out.print("Your name: ");
        String name = sc.nextLine().trim();
        System.out.print("Book ID: ");
        String id = sc.nextLine().trim();
        service.requestIssue(id, name);
        System.out.println("Request sent. Waiting for librarian approval.");
    }

    private static void approveIssue() {
        List<IssueRequest> pending = service.getPendingRequests();
        if (pending.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        for (int i = 0; i < pending.size(); i++) {
            IssueRequest r = pending.get(i);
            System.out.println((i + 1) + ". " + r.getStudentName() + " wants book " + r.getBookId());
        }
        int n = readInt("Request number to approve: ");
        service.approveIssue(n);
        System.out.println("Book issued. Return within " + LibraryService.LOAN_DAYS + " days.");
    }

    private static void returnBook() {
        System.out.print("Book ID to return: ");
        double fine = service.returnBook(sc.nextLine().trim());
        System.out.println("Book returned.");
        if (fine > 0) {
            System.out.printf("Late return! Fine collected: Rs.%.2f%n", fine);
        } else {
            System.out.println("No fine.");
        }
    }

    private static void fineCalculator() {
        System.out.print("Book ID: ");
        double fine = service.calculateFine(sc.nextLine().trim());
        System.out.printf("Current fine: Rs.%.2f (Rs.%.0f per day late)%n", fine, LibraryService.FINE_PER_DAY);
    }

    private static void dueDateReminder() {
        List<Book> due = service.getDueSoon();
        if (due.isEmpty()) {
            System.out.println("No books are due soon.");
        }
        for (Book b : due) {
            String note = b.getDueDate().isBefore(service.getToday()) ? "OVERDUE" : "due soon";
            System.out.println("Reminder: " + b.getIssuedTo() + " - '" + b.getTitle()
                    + "' due on " + b.getDueDate() + " (" + note + ")");
        }
    }

    private static void changeDate() {
        System.out.print("Enter date (yyyy-mm-dd): ");
        try {
            service.setToday(LocalDate.parse(sc.nextLine().trim()));
            System.out.println("Date changed to " + service.getToday());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-mm-dd.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
