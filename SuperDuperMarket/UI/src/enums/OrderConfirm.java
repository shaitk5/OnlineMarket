package enums;

public enum OrderConfirm {
    CONFIRM(1, "Confirm"),
    CANCEL(2, "Cancel");

    private final int optionNumber;
    private final String name;

    OrderConfirm(int optionNumber, String name) {
        this.optionNumber = optionNumber;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.optionNumber + ". " + this.name;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getName() {
        return name;
    }
}
