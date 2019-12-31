package library.domain.entities;

import library.domain.values.Authors;
import library.domain.values.Id;
import library.domain.values.Title;

public class Book {
    private final Id id;
    private final Title title;
    private final Authors authors;

    public Book(Id id, Title title, Authors authors) {
        this.id = id;
        this.title = title;
        this.authors = authors;
    }

    public Id getId() {
        return id;
    }

    public Title getTitle() {
        return title;
    }

    public Authors getAuthors() {
        return authors;
    }


}
