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

        double totalAmount = 0;

        String result = "Rental Record for " + customer.getName() + "\n";

        for (int i = 0; i < rentalRequests.size(); i++) {
            // Rental Document: BookId + DaysRented
            final String[] rental = rentalRequests.get(i).split(" ");
            int bookId = Integer.parseInt(rental[0]);
            int daysRented = Integer.parseInt(rental[1]);

            // rental: Book + daysRented
            final String[] book = books.get(bookId);

            double thisAmount = daysRented * 0.25;
            result += "\t'" + book[1] + "' by '" + book[2] + "' for " + daysRented + " days: \t" + thisAmount + " $\n";
            totalAmount += thisAmount;
        }
        result += "You owe " + totalAmount + " $\n";
        return List.of(result);
    }
}

