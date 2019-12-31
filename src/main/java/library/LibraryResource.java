package library;

import library.application.documents.BookDocument;
import library.domain.entities.Book;
import library.domain.values.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class LibraryResource {
    public static final String PATH_TO_BOOKS_FILE = System.getProperty("user.dir") + "/src/main/resources/library/";
    private InMemoryCustomerRepository customerRepository;

    public LibraryResource() {
        this.customerRepository = new InMemoryCustomerRepository();
    }

    public List<String[]> getBooks() throws IOException {
        final List<String[]> books = new ArrayList<>();
        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("books.csv"),
                        StandardCharsets.UTF_8
                )
        );
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            final String[] book = line.split(";");
            books.add(book);
        }
        return books;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public List<String> calculateFee(String customerName, List<String> rentalRequests) throws IOException {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("No customer name specified!");
        }

        if (rentalRequests == null
                || rentalRequests.size() == 0
                || rentalRequests.stream().anyMatch(request -> request.split(" ").length != 2)
                || rentalRequests.stream().map(request -> request.split(" ")).anyMatch(request -> !(isInteger(request[0]) && isInteger(request[1])))) {
            throw new IllegalArgumentException("Invalid requests");
        }

        Customer customer = customerRepository.findByUsername(customerName)
                .orElseThrow(() -> new IllegalArgumentException("Could not find user with name " + customerName));


        List<String> bookRows = Files.readAllLines(Paths.get(PATH_TO_BOOKS_FILE + "books.csv"), StandardCharsets.UTF_8);
        List<String[]> books = bookRows.stream()
                .map(bookRow -> bookRow.split(";"))
                .collect(toList());

        List<Book> books2 = bookRows.stream()
                .map(bookRow -> bookRow.split(";"))
                .map(bookCells -> new Book(Id.of(bookCells[0]), new Title(bookCells[1]), new Authors(Stream.of(bookCells[2].split(",")).map(Author::new).collect(toList()))))
                .collect(toList());

        double totalAmount = 0;

        String result = "Rental Record for " + customer.getName() + "\n";

        for (int i = 0; i < rentalRequests.size(); i++) {
            // Rental Document: BookId + DaysRented
            String request = rentalRequests.get(i);
            final String[] rental = request.split(" ");
            int bookId = Integer.parseInt(rental[0]);
            int daysRented = Integer.parseInt(rental[1]);
            RentalDocument rentalDocument = new RentalDocument(new BookDocument(new Id(bookId)), new DaysRented(daysRented), null);

            // rental: Book + daysRented
            final String[] book = books.get(rentalDocument.getBook().getId().asInteger());
            final Book book2 = books2.get(rentalDocument.getBook().getId().asInteger());

            double thisAmount = rentalDocument.getDaysRented().asDouble() * 0.25;
            Amount amount = new Amount(thisAmount);
            totalAmount += thisAmount;


            // Presenter logic: Rental Document: BookDocument + daysRented + amount
            Title title = book2.getTitle();
            Authors authors = book2.getAuthors();

            final BookDocument bookDocument = new BookDocument(book2.getId(), book2.getAuthors(), book2.getTitle());

            RentalDocument rentalDocument1 = new RentalDocument(bookDocument, rentalDocument.getDaysRented(), amount);

            result += "\t'" + rentalDocument1.getBook().getTitle() + "' by '" + rentalDocument1.getBook().getAuthors() + "' for " + rentalDocument1.getDaysRented().asDouble() + " days: \t" + rentalDocument1.getAmount().asDouble() + " $\n";
        }
        result += "You owe " + totalAmount + " $\n";
        return List.of(result);
    }
}

