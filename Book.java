import java.time.LocalDate;

public class Book {
    private String id;
    private String title;
    private String author;
    private boolean available = true;
    private String issuedTo;
    private LocalDate dueDate;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public String getIssuedTo() { return issuedTo; }
    public LocalDate getDueDate() { return dueDate; }

    public void issueTo(String student, LocalDate dueDate) {
        this.available = false;
        this.issuedTo = student;
        this.dueDate = dueDate;
    }

    public void markReturned() {
        this.available = true;
        this.issuedTo = null;
        this.dueDate = null;
    }

    @Override
    public String toString() {
        String status = available ? "Available" : "Issued to " + issuedTo + " (due " + dueDate + ")";
        return id + " | " + title + " | " + author + " | " + status;
    }
}
