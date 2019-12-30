package library;

import java.util.Map;
import java.util.Optional;

public class InMemoryCustomerRepository {
    private final Map<String, Customer> customers;

    public InMemoryCustomerRepository() {
        customers = Map.of("AnyUser", new Customer("anyUser"));
    }

    public Optional<Customer> findByUsername(String username) {
        if (customers.containsKey(username)) {
            return Optional.of(customers.get(username));
        }
        return Optional.empty();
    }
}
