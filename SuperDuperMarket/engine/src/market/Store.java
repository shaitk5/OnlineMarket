package market;

import java.util.*;

public class Store {
    private final int serialNumber;
    private final String name;
    private final Map<Integer, Sell> idToSell = new HashMap();
    private final Set<SubOrder> orders = new HashSet();
    private double deliveryPPK;
    private Coordinates coordinates;

    public Store(String name, int id, double ppk, int x, int y) {
        this.name = name;
        this.serialNumber = id;
        this.deliveryPPK = ppk;
        this.coordinates = new Coordinates(x, y);
    }

    public Store() {
        this.serialNumber = 0;
        this.name = " ";
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Sell> getIdToSell() {
        return this.idToSell;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Set<SubOrder> getOrders() {
        return orders;
    }

    public double getDeliveryPPK() {
        return deliveryPPK;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void addProduct(int id, Sell sell) {
        idToSell.put(id, sell);
    }

    public void addOrder(SubOrder orderToAdd) {
        orders.add(orderToAdd);
    }
}

