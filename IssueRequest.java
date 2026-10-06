public class IssueRequest {
    private String bookId;
    private String studentName;

    public IssueRequest(String bookId, String studentName) {
        this.bookId = bookId;
        this.studentName = studentName;
    }

    public String getBookId() { return bookId; }
    public String getStudentName() { return studentName; }
}
