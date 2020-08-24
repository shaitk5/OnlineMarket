package DTOClasses;

public class ProductDTO {
    private final String name;
    private final String pricing;
    private final int serialNumber;
    private final int numberOfStoresSelling;

    public ProductDTO(int serialNumber, int numberOfStoresSelling, String name, String pricing){
        this.numberOfStoresSelling = numberOfStoresSelling;
        this.serialNumber = serialNumber;
        this.name = name;
        this.pricing = pricing;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public String getPricing() {
        return pricing;
    }

    public int getNumberOfStoresSelling() {
        return numberOfStoresSelling;
    }

    @Override
    public String toString() {
        String pricing = this.getPricing().equals("Weight") ? "Price per kilogram" : "Price per product";
        return this.getSerialNumber() + ". " + this.getName() + "    -    " + pricing;
    }
}
