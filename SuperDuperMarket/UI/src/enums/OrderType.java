package enums;

public enum OrderType {
    CHOOSE_STORE(1, "Choose store to buy from"),
    DYNAMIC_ORDER(2, "Let the Market make your order");

    private final int optionNumber;
    private final String name;

    OrderType(int optionNumber, String name) {
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
