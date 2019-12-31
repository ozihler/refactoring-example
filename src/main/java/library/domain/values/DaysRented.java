package library.domain.values;

public class DaysRented {
    private int daysRented;

    public DaysRented(int daysRented) {
        this.daysRented = daysRented;
    }

    public double asDouble() {
        return daysRented;
    }
}
