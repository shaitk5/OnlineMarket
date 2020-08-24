package market;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Order {
    public static int serialNumberCounter = 1;
    private final int serialNumber;
    private final Set<SubOrder> subOrders = new HashSet();
    private Date date;

    Order(Date date) {
        this.serialNumber = serialNumberCounter++;
        this.date = date;
    }

    public int getNumberOfProductTypes(){
        return subOrders.stream().mapToInt(order -> order.getIdToQuantity().keySet().size()).sum();
    }

    public double getProductsPrice() {
        return subOrders.stream().mapToDouble(SubOrder::getProductsPrice).sum();
    }

    public double getDeliveryPrice() {
        return subOrders.stream().mapToDouble(SubOrder::getDeliveryPrice).sum();
    }

    public double getTotalPrice() {
        return this.getDeliveryPrice() + this.getProductsPrice();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Set<SubOrder> getSubOrders() {
        return subOrders;
    }

    public void addSubOrder(SubOrder subOrder){
        this.subOrders.add(subOrder);
    }
}


