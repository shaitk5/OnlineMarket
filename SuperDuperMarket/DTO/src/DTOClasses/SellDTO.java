package DTOClasses;

public class SellDTO {
    private final double price;
    private final int timesSold;
    private final double quantitySold;
    private final ProductDTO product;

    public SellDTO(double price, int itemSold, double quantitySold, ProductDTO product) {
        this.price = price;
        this.timesSold = itemSold;
        this.quantitySold = quantitySold;
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public int getTimesSold() {
        return timesSold;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public double getQuantitySold() {
        return quantitySold;
    }
}
