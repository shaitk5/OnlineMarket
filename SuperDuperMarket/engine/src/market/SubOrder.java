package market;

import DTOClasses.OrderDTO;
import enums.Pricing;
import java.util.Map;

public class SubOrder {
    private final int serialNumber;
    private final Map<Integer, Double> idToQuantity;
    private final double productsPrice;
    private final double deliveryPrice;
    private final Order mainOrder;
    private final Store store;

    public SubOrder(Map<Integer, Double> idToQuantity, int serialNumber, double productsPrice, double deliveryPrice, Order mainOrder, Store store) {
        this.serialNumber = serialNumber;
        this.productsPrice = productsPrice;
        this.deliveryPrice = deliveryPrice;
        this.mainOrder = mainOrder;
        this.store = store;
        this.idToQuantity = idToQuantity;
    }

    public SubOrder(OrderDTO copy, Order mainOrder, Store store){
        this.serialNumber = copy.getSubOrderSerialNumber();
        this.productsPrice = copy.getProductsPrice();
        this.deliveryPrice = copy.getDeliveryPrice();
        this.mainOrder = mainOrder;
        this.store = store;
        this.idToQuantity = copy.getIdToQuantity();
    }

    public int getNumOfProducts(IMarketEngine market) {
        int totalProducts = 0;

        for (Integer id : getIdToQuantity().keySet()) {
            if (market.getProductPricing(id).equals(Pricing.PRODUCT)) {
                totalProducts += getIdToQuantity().get(id);
            } else {
                totalProducts += 1;
            }
        }

        return totalProducts;
    }

    public Map<Integer, Double> getIdToQuantity() {
        return idToQuantity;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public double getProductsPrice() {
        return productsPrice;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public Order getMainOrder() {
        return mainOrder;
    }

    public Store getStore() {
        return store;
    }
}
