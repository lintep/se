package znulib.model;

// کلاس کتاب
public class Book {
    private String title;
    private String author;
    private int publicationYear;
    private String isbn;
    private Employee registeredBy;
    private boolean available;

    public Book(String title, String author, int publicationYear, String isbn, Employee registeredBy) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.registeredBy = registeredBy;
        this.available = true;
    }

    // Getter and Setter methods
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    // 1-3 مشاهده اطلاعات کتاب و وضعیت امانت کتاب (موجود هست یا خیر)
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public Employee getRegisteredBy() { return registeredBy; }
    
    @Override
    public String toString() {
        return String.format("عنوان: %s | نویسنده: %s | سال نشر: %d | وضعیت: %s | ISBN: %s", 
            title, author, publicationYear, available ? "موجود" : "امانت داده شده", isbn);
    }
}
