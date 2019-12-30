package library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> calculateFee(List<String> rentBooksRequests) throws IOException {
        if (rentBooksRequests == null || rentBooksRequests.size() == 0) {
            throw new IllegalArgumentException("rent books requests cannot be null!");
        }
        String customerName = rentBooksRequests.remove(0);

        // fetch customer
        Customer customer = customerRepository.findByUsername(customerName);

        // fetch books
        List<String> bookRows = Files.readAllLines(Paths.get(PATH_TO_BOOKS_FILE + "books.csv"), StandardCharsets.UTF_8);
        List<String[]> books = bookRows.stream()
                .map(bookRow -> bookRow.split(";"))
                .collect(toList());

        double totalAmount = 0;

        String result = "Rental Record for " + customer.getName() + "\n";

        for (int i = 0; i < rentBooksRequests.size(); i++) {
            final String[] rental = rentBooksRequests.get(i).split(" ");
            final String[] book = books.get(Integer.parseInt(rental[0]));
            double thisAmount = 0;

            int daysRented = Integer.parseInt(rental[1]);
            if (daysRented > 7) {
                thisAmount = thisAmount + (daysRented - (daysRented / 7.0)) * 1.5;
            } else {
                thisAmount = thisAmount + daysRented * 1.5;
            }

            // create figures for this rental
            result += "\t'" + book[1] + "' by '" + book[2] + "' for " + daysRented + " days: \t" + thisAmount + " $\n";
            totalAmount += thisAmount;
        }

        // add footer lines
        result += "You owe " + totalAmount + " $\n";

        return List.of(result);
    }
}

