package library.domain.values;

public class Amount {
    private double amount;

    public Amount(double amount) {
        this.amount = amount;
    }

    public double asDouble() {
        return amount;
    }
}
