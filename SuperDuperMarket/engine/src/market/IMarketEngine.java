package market;

import enums.Pricing;
import exceptions.InvalidActionException;
import javax.activity.InvalidActivityException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface IMarketEngine {
    Map getAllData(boolean includeOrders);
    Map getAllProducts();
    Set getAllOrders();
    Pricing getProductPricing(int productId);
    boolean isLocationAvailable(Coordinates checkLocations);
    void addOrder(Map<Integer, Double> idToQuantity, Date date, int storeSN, double totalPrice, double deliveryPrice);
    void addDynamicOrder() throws InvalidActionException;
    Set buildDynamicOrder(Map<Integer, Double> productIdToQuantity, Date date, Coordinates coordinates);
    void addNewProduct(int storeSerialNumber, int productSerialNumber, double price);
    void updateProductPrice(int storeSerialNumber, int productSerialNumber, double newPrice);
    void deleteProduct(int storeSerialNumber, int productSerialNumber) throws InvalidActivityException;
    void saveOrderHistory(Path path);
    void loadOrderHistoryFromFile(Path path) throws InvalidActionException;
    boolean isProductExist(int key);
}
