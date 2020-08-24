package market;

import DTOClasses.ProductDTO;
import enums.Pricing;

public class Product {
    private final String name;
    private final Pricing pricing;
    private final int serialNumber;
    private int numberOfStoresSelling;

    public Product(String name,int serialNumber, Pricing pricing, int numberOfStoresSelling){
        this.numberOfStoresSelling = numberOfStoresSelling;
        this.name = name;
        this.serialNumber = serialNumber;
        this.pricing = pricing;
    }

    public Product(ProductDTO product){
        this.numberOfStoresSelling = product.getNumberOfStoresSelling();
        this.name = product.getName();
        this.serialNumber = product.getSerialNumber();
        this.pricing = product.getPricing() == "Weight" ? Pricing.WEIGHT : Pricing.PRODUCT;
    }

    public String getName() {
        return name;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public int getNumberOfStoresSelling() {
        return numberOfStoresSelling;
    }

    public void setNumberOfStoresSelling(int numberOfStoresSelling) {
        this.numberOfStoresSelling = numberOfStoresSelling;
    }

}
