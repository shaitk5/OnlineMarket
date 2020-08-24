package DTOClasses;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

public class OrderDTO implements Serializable {
    private final int serialNumber;
    private final int numOfProducts;
    private final int subOrderSerialNumber;
    private final int storeSerialNumber;
    private final double productsPrice;
    private final double deliveryPrice;
    private final Map<Integer, Double> idToQuantity;
    private final String date;
    private final String storeName;

    public OrderDTO(int serialNumber, String date, String storeName, int subOrderSerialNumber,int numOfProducts, int storeSerialNumber, double productsPrice, double deliveryPrice, Map<Integer, Double> idToQuantity) {
        this.serialNumber = serialNumber;
        this.numOfProducts = numOfProducts;
        this.storeName = storeName;
        this.storeSerialNumber = storeSerialNumber;
        this.date = date;
        this.productsPrice = productsPrice;
        this.deliveryPrice = deliveryPrice;
        this.subOrderSerialNumber = subOrderSerialNumber;
        this.idToQuantity = idToQuantity;
    }

    public int getNumOfProductTypes(){
        return idToQuantity.keySet().size();
    }

    public int getNumberOfProducts(){
        return this.numOfProducts;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getDate() {
        return date;
    }

    public Map<Integer, Double> getIdToQuantity() {
        return idToQuantity;
    }

    public int getSubOrderSerialNumber() {
        return subOrderSerialNumber;
    }

    public double getProductsPrice() {
        return productsPrice;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public int getStoreSerialNumber() {
        return storeSerialNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    @Override
    public String toString() {
       DecimalFormat df = new DecimalFormat("#.##");
        String newLine = System.lineSeparator();
        return "Order serial number " + this.getSerialNumber() + "\\" + this.getSubOrderSerialNumber() + newLine
                + "Date : " + this.getDate() + newLine
                + "Store : " + this.getSerialNumber() + ". " + this.storeName + newLine
                + "Product types : " + this.getNumOfProductTypes() + newLine
                + "Total Products : " + this.getNumberOfProducts() + newLine
                + "Products price : " + this.getProductsPrice() + newLine
                + "Delivery price : " + df.format(this.getDeliveryPrice()) + newLine
                + "Total price : " + df.format((this.getProductsPrice() + this.getDeliveryPrice())) + newLine;
    }

    //    // Custom serialization logic,
//    // This will allow us to have additional serialization logic on top of the default one e.g. encrypting object before serialization
//    private void writeObject(ObjectOutputStream oos) throws IOException {
//        Class c = OrderDTO.class;
//        Field numOfProducts = null;
//        Field serialNumber = null;
//        Field subOrderSerialNumber = null;
//        Field storeSerialNumber = null;
//        Field productsPrice = null;
//        Field deliveryPrice = null;
//        Field idToQuantity = null;
//        Field date = null;
//        Field storeName = null;
//        try {
//            numOfProducts = c.getDeclaredField("numOfProducts");
//            numOfProducts.setAccessible(true);
//            serialNumber = c.getDeclaredField("serialNumber");
//            serialNumber.setAccessible(true);
//            subOrderSerialNumber = c.getDeclaredField("subOrderSerialNumber");
//            subOrderSerialNumber.setAccessible(true);
//            storeSerialNumber = c.getDeclaredField("storeSerialNumber");
//            storeSerialNumber.setAccessible(true);
//            productsPrice = c.getDeclaredField("productsPrice");
//            productsPrice.setAccessible(true);
//            deliveryPrice = c.getDeclaredField("deliveryPrice");
//            deliveryPrice.setAccessible(true);
//            idToQuantity = c.getDeclaredField("idToQuantity");
//            idToQuantity.setAccessible(true);
//            date = c.getDeclaredField("date");
//            date.setAccessible(true);
//            storeName = c.getDeclaredField("storeName");
//            storeName.setAccessible(true);
//        } catch (NoSuchFieldException ignore) { }
//        oos.defaultWriteObject();
//        makePrivate(numOfProducts, serialNumber, subOrderSerialNumber, storeSerialNumber, productsPrice, deliveryPrice, idToQuantity, date, storeName);
//    }
//
//    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
//        Class c = OrderDTO.class;
//        Field numOfProducts = null;
//        Field serialNumber = null;
//        Field subOrderSerialNumber = null;
//        Field storeSerialNumber = null;
//        Field productsPrice = null;
//        Field deliveryPrice = null;
//        Field idToQuantity = null;
//        Field date = null;
//        Field storeName = null;
//        try {
//            numOfProducts = c.getDeclaredField("numOfProducts");
//            numOfProducts.setAccessible(true);
//            serialNumber = c.getDeclaredField("serialNumber");
//            serialNumber.setAccessible(true);
//            subOrderSerialNumber = c.getDeclaredField("subOrderSerialNumber");
//            subOrderSerialNumber.setAccessible(true);
//            storeSerialNumber = c.getDeclaredField("storeSerialNumber");
//            storeSerialNumber.setAccessible(true);
//            productsPrice = c.getDeclaredField("productsPrice");
//            productsPrice.setAccessible(true);
//            deliveryPrice = c.getDeclaredField("deliveryPrice");
//            deliveryPrice.setAccessible(true);
//            idToQuantity = c.getDeclaredField("idToQuantity");
//            idToQuantity.setAccessible(true);
//            date = c.getDeclaredField("date");
//            date.setAccessible(true);
//            storeName = c.getDeclaredField("storeName");
//            storeName.setAccessible(true);
//        } catch (NoSuchFieldException ignore) { }
//        ois.defaultReadObject();
//        makePrivate(numOfProducts, serialNumber, subOrderSerialNumber, storeSerialNumber, productsPrice, deliveryPrice, idToQuantity, date, storeName);
//    }
//
//    private void makePrivate(Field numOfProducts, Field serialNumber, Field subOrderSerialNumber, Field storeSerialNumber, Field productsPrice, Field deliveryPrice, Field idToQuantity, Field date, Field storeName) {
//        numOfProducts.setAccessible(false);
//        serialNumber.setAccessible(false);
//        subOrderSerialNumber.setAccessible(false);
//        storeSerialNumber.setAccessible(false);
//        productsPrice.setAccessible(false);
//        deliveryPrice.setAccessible(false);
//        idToQuantity.setAccessible(false);
//        date.setAccessible(false);
//        storeName.setAccessible(false);
//    }

}
