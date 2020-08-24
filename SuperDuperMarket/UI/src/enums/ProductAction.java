package enums;

public enum ProductAction {
        DELETE_PRODUCT(1,"Delete product"),
        ADD_PRODUCT(2,"Add product"),
        UPDATE_PRODUCT_PRICE(3,"Update product price");

        private final int optionNumber;
        private final String name;

        ProductAction(int optionNumber, String name){
            this.optionNumber = optionNumber;
            this.name = name;
        }

        @Override
        public String toString() {
            return this.optionNumber + ". " + this.name;
        }
}
