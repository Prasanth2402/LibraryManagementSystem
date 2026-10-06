import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {
    public static final int LOAN_DAYS = 7;
    public static final double FINE_PER_DAY = 5.0;

    private ArrayList<Book> books = new ArrayList<>();
    private Map<String, Book> issuedBooks = new HashMap<>();     // book id -> issued book
    private List<IssueRequest> pendingRequests = new ArrayList<>();
    private LocalDate today = LocalDate.now();
    private double finesCollected = 0;

    public LocalDate getToday() { return today; }

    // lets us test late returns without waiting for real days to pass
    public void setToday(LocalDate date) { this.today = date; }

    public void addBook(Book book) {
        if (findBook(book.getId()) != null) {
            throw new IllegalArgumentException("Book ID " + book.getId() + " already exists.");
        }
        books.add(book);
    }

    public Book findBook(String id) {
        for (Book b : books) {
            if (b.getId().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    private Book getBookOrThrow(String id) {
        Book b = findBook(id);
        if (b == null) {
            throw new IllegalArgumentException("No book found with ID " + id);
        }
        return b;
    }

    public List<Book> searchBook(String keyword) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())
                    || b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Book> getAllBooks() { return books; }

    // Student asks for a book
    public void requestIssue(String bookId, String studentName) {
        Book b = getBookOrThrow(bookId);
        if (!b.isAvailable()) {
            throw new IllegalStateException("Book is not available right now.");
        }
        pendingRequests.add(new IssueRequest(b.getId(), studentName));
    }

    public List<IssueRequest> getPendingRequests() { return pendingRequests; }

    // Librarian approves a request
    public void approveIssue(int requestNumber) {
        if (requestNumber < 1 || requestNumber > pendingRequests.size()) {
            throw new IllegalArgumentException("Invalid request number.");
        }
        IssueRequest r = pendingRequests.get(requestNumber - 1);
        Book b = getBookOrThrow(r.getBookId());
        if (!b.isAvailable()) {
            pendingRequests.remove(requestNumber - 1);
            throw new IllegalStateException("Book was already issued to someone else. Request removed.");
        }
        b.issueTo(r.getStudentName(), today.plusDays(LOAN_DAYS));
        issuedBooks.put(b.getId(), b);
        pendingRequests.remove(requestNumber - 1);
    }

    // Fine = days late x fine per day
    public double calculateFine(String bookId) {
        Book b = issuedBooks.get(findIdOrThrow(bookId));
        long daysLate = ChronoUnit.DAYS.between(b.getDueDate(), today);
        return daysLate > 0 ? daysLate * FINE_PER_DAY : 0;
    }

    private String findIdOrThrow(String bookId) {
        Book b = getBookOrThrow(bookId);
        if (!issuedBooks.containsKey(b.getId())) {
            throw new IllegalStateException("This book is not currently issued.");
        }
        return b.getId();
    }

    // Returns the fine that was collected
    public double returnBook(String bookId) {
        double fine = calculateFine(bookId);
        Book b = getBookOrThrow(bookId);
        b.markReturned();
        issuedBooks.remove(b.getId());
        finesCollected += fine;
        return fine;
    }

    // Books that are overdue or due within the next 2 days
    public List<Book> getDueSoon() {
        List<Book> list = new ArrayList<>();
        for (Book b : issuedBooks.values()) {
            if (!b.getDueDate().isAfter(today.plusDays(2))) {
                list.add(b);
            }
        }
        return list;
    }

    public void printReport() {
        int overdue = 0;
        for (Book b : issuedBooks.values()) {
            if (b.getDueDate().isBefore(today)) {
                overdue++;
            }
        }
        System.out.println("\n----- Library Report (" + today + ") -----");
        System.out.println("Total books      : " + books.size());
        System.out.println("Available books  : " + (books.size() - issuedBooks.size()));
        System.out.println("Issued books     : " + issuedBooks.size());
        System.out.println("Overdue books    : " + overdue);
        System.out.println("Pending requests : " + pendingRequests.size());
        System.out.printf("Fines collected  : Rs.%.2f%n", finesCollected);
    }
}
