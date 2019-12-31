package library.domain.values;

public class Id {
    private int id;

    public Id(int id) {
        this.id = id;
    }

    public static Id of(String idString) {
        return new Id(Integer.parseInt(idString));
    }

    public int asInteger() {
        return id;
    }
}
