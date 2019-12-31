package library.domain.values;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Authors {
    private List<Author> authors;

    public Authors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    @Override
    public String toString() {
        return authors.stream().map(Author::toString).collect(joining(" & "));
    }
}
