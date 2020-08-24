package enums;

public enum Menu {
    READ_FILE(1,"Read File"),
    SHOW_STORES_DETAILS(2,"Show stores details"),
    SHOW_ALL_PRODUCTS(3,"Show all products"),
    MAKE_ORDER(4,"Make order"),
    ORDER_HISTORY(5,"Market Order history"),
    UPDATE_DELETE_OR_ADD_PRODUCT(6,"Update, delete or add product"),
    SAVE_ORDERS(7,"Save order history to file"),
    READ_ORDER_HISTORY(8,"Read order history from file"),
    EXIT(9,"Exit");

    private final int optionNumber;
    private final String name;

    Menu(int optionNumber, String name){
        this.optionNumber = optionNumber;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getOptionNumber() {
        return this.optionNumber;
    }

    @Override
    public String toString() {
        return this.optionNumber + ". " + this.name;
    }
}
