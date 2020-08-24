package market;

import DTOClasses.*;
import enums.Pricing;
import exceptions.InvalidActionException;
import javax.activity.InvalidActivityException;
import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Market implements IMarketEngine {

    boolean fileLoaded = false;
    private final String FILE_NAME = "\\orderHistory.dat";
    private Path filePath;
    private Path ordersFilePath;
    private Set<OrderDTO> dynamicOrder = null;
    private Map<Integer, Store> idToStore = new HashMap();
    private Map<Integer, Product> idToProduct = new HashMap();
    private Map<Integer, Order> idToOrder = new HashMap();

    public Market() { }
    //getters setters
    public void setIdToStore(Map<Integer, Store> idToStore) {
        this.idToStore = idToStore;
    }

    public void setIdToProduct(Map<Integer, Product> idToProduct) {
        this.idToProduct = idToProduct;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean isLocationAvailable(Coordinates checkLocation) {
        Collection<Store> stores = idToStore.values();
        long count = stores.stream()
                .filter(store -> store.getCoordinates().equals(checkLocation))
                .count();
        return count == 0 && Coordinates.isValidCoordinate(checkLocation);
    }

    @Override
    public Map getAllData(boolean includeOrders) {
        Map<Integer, StoreDTO> idToStoresDTO = new HashMap();
        Map<Integer, ProductDTO> idToProductsDTO = getAllProducts();
        Map<Double, OrderDTO> idToOrderDTO  = null;
        Set<OrderDTO> storeOrders;
        if(includeOrders) {                         //make all orders
            idToOrderDTO = makeOrdersDTO();
        }

        for(Store store : idToStore.values()){
            Map<Integer, SellDTO> idToSellDTO = new HashMap();
            Map<Integer, ProductDTO> finalIdToProductsDTO = idToProductsDTO;
            store.getIdToSell().values().forEach(sell -> idToSellDTO.put(sell.getProduct().getSerialNumber(),
                    new SellDTO(sell.getPrice(), sell.getTimesSold(), sell.getQuantitySold(), finalIdToProductsDTO.get(sell.getProduct().getSerialNumber())))); // add all store sell items
            if(includeOrders) {
                storeOrders = idToOrderDTO.values().stream().filter(order -> order.getStoreSerialNumber() == store.getSerialNumber()).collect(Collectors.toSet());
            }
            else {
                storeOrders = null;
            }
            idToStoresDTO.put(store.getSerialNumber(), new StoreDTO(store.getSerialNumber(), store.getDeliveryPPK(), store.getName(),
                    store.getCoordinates().getX(), store.getCoordinates().getY(), idToSellDTO, storeOrders));  //make all stores DTO
        }

        return idToStoresDTO;
    }

    private Map<Double, OrderDTO>  makeOrdersDTO(){
        Map<Double, OrderDTO> idToOrderDTO = new HashMap();

        for (Order order : idToOrder.values()) {
            for (SubOrder subOrder : order.getSubOrders()) {
                idToOrderDTO.put(order.getSerialNumber() + (0.01 * subOrder.getSerialNumber()),
                        new OrderDTO(order.getSerialNumber(), new SimpleDateFormat("dd/MM-hh:mm").format(order.getDate()),
                                subOrder.getStore().getName() ,subOrder.getSerialNumber(), subOrder.getNumOfProducts(this),subOrder.getStore().getSerialNumber(),
                                subOrder.getProductsPrice(), subOrder.getDeliveryPrice(), subOrder.getIdToQuantity()));
            }
        }

        return idToOrderDTO;
    }

    @Override
    public Pricing getProductPricing(int productId) {
        if(idToProduct.containsKey(productId)){
            return idToProduct.get(productId).getPricing();
        }
        return null;
    }

    @Override
    public Map getAllProducts() {
        Map<Integer, ProductDTO> idToProductsDTO = new HashMap();
        idToProduct.values().forEach(product -> idToProductsDTO.put(product.getSerialNumber(),
                new ProductDTO(product.getSerialNumber(), product.getNumberOfStoresSelling(),
                        product.getName(), product.getPricing().toString())));  //make all products DTO
        return idToProductsDTO;
    }

    @Override
    public Set getAllOrders() {
        Map<Double, OrderDTO> idToOrderDTO = makeOrdersDTO();
        if(idToOrderDTO.size() == 0){
            return null;
        }
        return new HashSet<>(idToOrderDTO.values());
    }

    @Override
    public void addNewProduct(int storeSerialNumber, int productSerialNumber, double price) {
        Store storePick = idToStore.get(storeSerialNumber);
        Product productPick = idToProduct.get(productSerialNumber);
        productPick.setNumberOfStoresSelling(productPick.getNumberOfStoresSelling() + 1);
        storePick.getIdToSell().put(productPick.getSerialNumber(), new Sell(price, productPick));
    }

    @Override
    public void deleteProduct(int storeSerialNumber, int productSerialNumber) throws InvalidActivityException {
        Store storePick = idToStore.get(storeSerialNumber);
        Sell productSell = storePick.getIdToSell().get(productSerialNumber);

        if(productSell.getProduct().getNumberOfStoresSelling() > 1){
            if(storePick.getIdToSell().size() > 1){
                productSell.getProduct().setNumberOfStoresSelling(productSell.getProduct().getNumberOfStoresSelling() - 1);
                storePick.getIdToSell().remove(productSerialNumber);
            }
            else{
                throw new InvalidActivityException("the product : " + productSell.getProduct().getName() + " is the only product is the store : " + storePick.getName());
            }
        }
        else{
            throw new InvalidActivityException("Store : " + storePick.getName() + " is the only store that sell the product : " + productSell.getProduct().getName());
        }
    }

    @Override
    public void saveOrderHistory(Path path) {
        this.setOrdersFilePath(path);
        Map<Double, OrderDTO> orders = makeOrdersDTO();
        ArrayList<OrderDTO> ordersToSave = new ArrayList<>(orders.values());
        try (
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toString() + FILE_NAME))) {
            out.writeObject(ordersToSave);
            out.flush();
        } catch (IOException ignored) { }
    }

    @Override
    public void loadOrderHistoryFromFile(Path path) throws InvalidActionException {
        ArrayList<OrderDTO> orders = null;
        Store store = new Store();

        if(!fileLoaded) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toString()))) {
                orders = (ArrayList<OrderDTO>) in.readObject();
            } catch (IOException | ClassNotFoundException ignored) {
            }

            int counter = 1;
            int unknownStores = (int) orders.stream().filter(order -> !idToStore.getOrDefault(order.getStoreSerialNumber(), store).getName().equals(order.getStoreName())).count(); // check if stores exist
            if (unknownStores != 0){
                throw new InvalidActionException("The file is not suitable for the current Market");
            }
            while (orders != null && !orders.isEmpty()) {
                int finalCounter = counter;
                Set<OrderDTO> allOrders = orders.stream().filter(order -> order.getSerialNumber() == finalCounter).collect(Collectors.toSet());
                this.dynamicOrder = allOrders;
                addDynamicOrder();
                orders.removeAll(allOrders);
                counter++;
            }
            fileLoaded = true;
        }
        else{
            throw new InvalidActionException("File already loaded");
        }
    }

    @Override
    public void updateProductPrice(int storeSerialNumber, int productSerialNumber, double newPrice) {
        idToStore.get(storeSerialNumber).getIdToSell().get(productSerialNumber).setPrice(newPrice);
    }

    @Override
    public boolean isProductExist(int key) {
        return idToProduct.containsKey(key);
    }

    @Override
    public void addOrder(Map<Integer, Double> idToQuantity, Date date, int storeSN, double totalPrice, double deliveryPrice) {
        Store store = idToStore.get(storeSN);
        Order newOrder = new Order(date);
        SubOrder newSubOrder = new SubOrder(idToQuantity,1, (totalPrice - deliveryPrice), deliveryPrice, newOrder, store);
        newOrder.addSubOrder(newSubOrder);
        this.idToOrder.put(newOrder.getSerialNumber(), newOrder);
        store.addOrder(newSubOrder);

        idToQuantity.keySet().forEach(productId -> {
            store.getIdToSell().get(productId).setTimesSold( store.getIdToSell().get(productId).getTimesSold() + 1);
            store.getIdToSell().get(productId).setQuantitySold(store.getIdToSell().get(productId).getQuantitySold() + idToQuantity.get(productId));
        });
    }

    @Override
    public void addDynamicOrder() throws InvalidActionException {
        if(this.dynamicOrder == null){
            throw new InvalidActionException("No dynamic order in the system");
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM-hh:mm").parse(this.dynamicOrder.stream().iterator().next().getDate());
        }catch (ParseException ignore){}

        Order newOrder = new Order(date);
        idToOrder.put(newOrder.getSerialNumber(), newOrder);

        for(OrderDTO order : this.dynamicOrder){
            Store store = idToStore.get(order.getStoreSerialNumber());
            SubOrder sub = new SubOrder(order, newOrder, idToStore.get(order.getStoreSerialNumber()));
            Map<Integer, Double> idToQuantity = sub.getIdToQuantity();
            idToQuantity.keySet().forEach(productId -> {
                store.getIdToSell().get(productId).setQuantitySold(store.getIdToSell().get(productId).getQuantitySold() + idToQuantity.get(productId));
                store.getIdToSell().get(productId).setTimesSold(store.getIdToSell().get(productId).getTimesSold() + 1);
            });
            newOrder.addSubOrder(sub);
            idToStore.get(order.getStoreSerialNumber()).addOrder(sub);
        }

        this.dynamicOrder = null;
    }

    @Override
    public Set<OrderDTO> buildDynamicOrder(Map<Integer, Double> idToQuantity, Date date, Coordinates coordinates) {
        Map<Integer, Double> idToPrice = new HashMap<>();
        Map<Integer, Integer> idToStoreSelling = new HashMap();
        Map<Integer, Set<Integer>> storeSellingToIds = new HashMap();

        for(Store store : idToStore.values()){
            if(!store.getCoordinates().equals(coordinates)) {
                for (Integer key : idToQuantity.keySet()) {
                    if (store.getIdToSell().containsKey(key)) {
                        if (idToPrice.getOrDefault(key, 0.0) == 0.0) {
                            idToPrice.put(key, store.getIdToSell().get(key).getPrice());
                            idToStoreSelling.put(key, store.getSerialNumber());
                            if(!storeSellingToIds.containsKey(store.getSerialNumber())) {
                                storeSellingToIds.put(store.getSerialNumber(),new HashSet());
                            }
                            storeSellingToIds.get(store.getSerialNumber()).add(key);
                        }
                        if (store.getIdToSell().get(key).getPrice() < idToPrice.get(key)) {
                            idToPrice.put(key, store.getIdToSell().get(key).getPrice());
                            idToStoreSelling.put(key, store.getSerialNumber());
                            if(!storeSellingToIds.containsKey(store.getSerialNumber())) {
                                storeSellingToIds.put(store.getSerialNumber(), new HashSet());
                            }
                            storeSellingToIds.get(idToStoreSelling.get(key)).remove(key);
                            storeSellingToIds.get(store.getSerialNumber()).add(key);
                        }
                    }
                }
            }
        }

        this.dynamicOrder = buildOrderDTO(idToPrice, storeSellingToIds, idToQuantity, date, coordinates);
        return this.dynamicOrder;
    }

    private Set<OrderDTO> buildOrderDTO(Map<Integer, Double> idToPrice,  Map<Integer, Set<Integer>> storeSellingToIds , Map<Integer, Double> idToQuantity, Date date, Coordinates coordinates){
        Set<OrderDTO> orders = new HashSet();
        int orderSerialNumber = Order.serialNumberCounter;
        double deliveryPrice, productsPrice = 0;
        int counter = 1 ;
        Store storeSelling;

        for(Integer storeId : storeSellingToIds.keySet()) {
            Map<Integer, Double> idToQuantityStore = new HashMap();
            productsPrice = 0;
            storeSelling = idToStore.get(storeId);
            deliveryPrice = Coordinates.getDistance(storeSelling.getCoordinates(), coordinates) * storeSelling.getDeliveryPPK();

            for(Integer productId : storeSellingToIds.get(storeId)){
                productsPrice += idToPrice.get(productId) * idToQuantity.get(productId);
                idToQuantityStore.put(productId, idToQuantity.get(productId));
            }

            orders.add(new OrderDTO(orderSerialNumber ,new SimpleDateFormat("dd/MM-hh:mm").format(date), storeSelling.getName(), counter++ ,
                   storeSellingToIds.values().size(), storeSelling.getSerialNumber(), productsPrice, deliveryPrice, idToQuantityStore));
        }

        return orders;
    }

    public void setOrdersFilePath(Path ordersFilePath) {
        this.ordersFilePath = ordersFilePath;
    }
}

