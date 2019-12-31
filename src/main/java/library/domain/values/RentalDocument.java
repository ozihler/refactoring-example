package library.domain.values;

import library.application.documents.BookDocument;

public class RentalDocument {
    private final BookDocument book;
    private final DaysRented daysRented;
    private Amount amount;

    public RentalDocument(BookDocument book, DaysRented daysRented, Amount amount) {
        this.book = book;
        this.daysRented = daysRented;
        this.amount = amount;
    }

    public BookDocument getBook() {
        return book;
    }

    public DaysRented getDaysRented() {
        return daysRented;
    }

    public Amount getAmount() {
        return amount;
    }
}
