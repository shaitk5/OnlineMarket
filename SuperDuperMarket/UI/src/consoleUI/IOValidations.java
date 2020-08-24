package consoleUI;

import DTOClasses.OrderDTO;
import DTOClasses.ProductDTO;
import DTOClasses.SellDTO;
import DTOClasses.StoreDTO;
import enums.OrderConfirm;
import enums.OrderType;
import exceptions.DiscardException;
import exceptions.InvalidActionException;
import market.Coordinates;
import market.IMarketEngine;
import javax.activity.InvalidActivityException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class IOValidations {
    // validations
    public static boolean pathValidation(Path path) {
        boolean valid = false;

        if (!path.isAbsolute()) {
            System.out.println("The path is not Absolute");
        } else if (Files.isDirectory(path)) {
            System.out.println("This is a directory");
        } else if (!path.toString().toLowerCase().endsWith(".xml")) {
            System.out.println("This is not an xml file");
        } else if (!Files.exists(path)) {
            System.out.println("File Does not exist");
        } else {
            valid = true;
        }
        return valid;
    }

    // prints - all store details
    public static void printStoreDetails(StoreDTO store, boolean includeItemsAndOrders) {
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder output = new StringBuilder();
        String newLine = System.lineSeparator();
        output.append(store.getSerialNumber() + ". " + store.getName() + newLine
                + "Delivery price (Per kilometer) : " + store.getPPK() + newLine);

        if (includeItemsAndOrders) {
            output.append("Products : " + newLine + "-------------------" + newLine);
            store.getIdToSell().values().forEach(sell -> addProductDetails(sell, output));
            output.append("Orders : " + newLine + "-------------------" + newLine);
            if (store.getOrders().size() == 0) {
                output.append("-----No orders-----" + newLine + "-------------------" + newLine);
            }
            store.getOrders().forEach(order -> addOrderDetails(order, output));
            output.append("Total delivery income : " + df.format(store.getOrders().stream()
                    .mapToDouble(OrderDTO::getDeliveryPrice).sum()) + newLine);
            System.out.print(output + newLine + "----------------------------" + newLine);
        } else {
            System.out.print(output + "----------------------------" + newLine);
        }
    }

    public static void addProductDetails(SellDTO sell, StringBuilder output) {
        DecimalFormat df = new DecimalFormat("#.##");
        String newLine = System.lineSeparator();
        String pricing = sell.getProduct().getPricing().equals("Weight") ? "Price per kilogram" : "Price per product";
        output.append(sell.getProduct().getSerialNumber() + ". " + sell.getProduct().getName() + "   -   " + pricing + " : "
                + df.format(sell.getPrice()) + newLine
                + "Total product sold : " + df.format(sell.getQuantitySold()) + newLine + newLine);
    }

    public static void addOrderDetails(OrderDTO order, StringBuilder output) {
        DecimalFormat df = new DecimalFormat("#.##");
        String newLine = System.lineSeparator();
        output.append("Date : " + order.getDate() + newLine
                + "Total products : " + order.getNumberOfProducts() + newLine
                + "Total products price : " + df.format(order.getProductsPrice()) + newLine
                + "Delivery price : " + df.format(order.getDeliveryPrice()) + newLine
                + "Total order price : " + df.format(order.getDeliveryPrice() + order.getProductsPrice()) + newLine + newLine);
    }

    // prints - all products
    public static void printProductsToOrder(StoreDTO storePick, Collection<StoreDTO> stores) { //Collection<StoreDTO> stores
        DecimalFormat df = new DecimalFormat("#.##");
        StringBuilder output = new StringBuilder();
        Map<Integer, Integer> idToPrinted = new HashMap<>();

        for (StoreDTO store : stores) {
            for (SellDTO sell : store.getIdToSell().values()) {
                ProductDTO product = sell.getProduct();
                if (idToPrinted.get(sell.getProduct().getSerialNumber()) == null) {
                    String pricing = product.getPricing().equals("Weight") ? "price per kilogram" : "price per product";
                    output.append(product.getSerialNumber() + ". " + product.getName() + "   -   ");
                    if(storePick == null){
                        output.append(pricing);
                    }
                    else if (storePick.getIdToSell().containsKey(product.getSerialNumber())) {
                        output.append(pricing + " : " + df.format(storePick.getIdToSell().get(sell.getProduct().getSerialNumber()).getPrice()));
                    } else {
                        output.append("This store does not sell this item");
                    }
                    idToPrinted.put(product.getSerialNumber(), 1);
                }
                if (output.length() != 0) {
                    System.out.println(output);
                }
                output.delete(0, output.length());
            }
        }
    }

    //read data from user
    public static Coordinates readCoordinates(IMarketEngine market) {
        Scanner in = new Scanner(System.in);
        String input;
        System.out.println("Please enter your location coordinates [x,y]: ");
        Coordinates location = null;
        boolean finish = false;

        do {
            try {
                System.out.print("x : ");
                input = in.nextLine();
                int x = Integer.parseInt(input);
                System.out.print("y : ");
                input = in.nextLine();
                int y = Integer.parseInt(input);
                location = new Coordinates(x, y);
                finish = market.isLocationAvailable(location);
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            }
            if (!finish) {
                System.out.println("Coordinates invalid, please enter numbers between 1-50");
            }
        } while (!finish);

        return location;
    }

    public static OrderType chooseOrderType() {
        Scanner in = new Scanner(System.in);
        String input;
        OrderType orderType;

        do {
            try {
                System.out.println("Please pick an option : ");
                Arrays.stream(OrderType.values()).forEach(System.out::println);
                input = in.nextLine();
                int pick = Integer.parseInt(input);
                orderType = OrderType.values()[pick -1];
                break;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number.  " + e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong input, please enter a number between (1 - " + OrderType.values().length + ")");
            }
        } while (true);

        return orderType;
    }

    public static Date readDate() {
        boolean finish = false;
        Date date = null;
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter desirable delivery time (dd/mm-hh:mm) : ");

        do {
            try {
                String input = in.nextLine();
                date = new SimpleDateFormat("dd/MM-hh:mm").parse(input);
                finish = true;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            } catch (ParseException e) {
                System.out.println("Wrong format entered! please enter again : ");
            }
        } while (!finish);

        return date;
    }

    public static Map<Integer, Double> pickProductsFromStore(Map<Integer, StoreDTO> idToStores, int storePickSN) {
        Scanner in = new Scanner(System.in);
        boolean finishPicking = false;
        int productPick;
        StoreDTO storePick = idToStores.getOrDefault(storePickSN, null);
        Map<Integer, Double> idToQuantity = new HashMap<>();

        do {
            try {
                System.out.println("----------PRODUCTS----------" + System.lineSeparator() + "----------------------------");
                printProductsToOrder(storePick, idToStores.values());
                System.out.println(System.lineSeparator() + "To add product to order, please enter his number (or press 'q' to finish) : ");
                String item = in.nextLine();
                checkQ(item);
                productPick = Integer.parseInt(item);
                if (idToStores.get(storePickSN).getIdToSell().containsKey(productPick)) {
                    pickQuantity(idToQuantity, idToStores, storePickSN, productPick);
                } else {
                    System.out.println("Store doesnt sell this item!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number : " + e.getMessage());
            } catch (DiscardException e) {
                finishPicking = true;
            }
        } while (!finishPicking);

        return idToQuantity;
    }

    public static Map<Integer, Double> dynamicPickProductsFromStore(Map<Integer, ProductDTO> idToProduct) {
        Scanner in = new Scanner(System.in);
        boolean finishPicking = false;
        int productPick;
        Map<Integer, Double> idToQuantity = new HashMap<>();

        do {
            try {
                System.out.println("----------PRODUCTS----------" + System.lineSeparator() + "----------------------------");
                idToProduct.values().forEach(System.out::println);
                System.out.println(System.lineSeparator() + "To add product to order, please enter his number (or press 'q' to finish) : ");
                String item = in.nextLine();
                checkQ(item);
                productPick = Integer.parseInt(item);
                if(!idToProduct.containsKey(productPick)){
                    System.out.println("Invalid input, item does not exist");
                }
                else {
                    dynamicProductQuantity(idToQuantity, idToProduct, productPick);
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number : " + e.getMessage());
            } catch (DiscardException e) {
                finishPicking = true;
            }
        } while (!finishPicking);

        return idToQuantity;
    }

    private static void dynamicProductQuantity(Map<Integer, Double> idToQuantity, Map<Integer, ProductDTO> idToProduct, int productPick) throws DiscardException {
        boolean sellPerProduct = idToProduct.get(productPick).getPricing().equals("Product");
        productQuantity(idToQuantity, productPick, sellPerProduct);
    }

    private static void pickQuantity(Map<Integer, Double> idToQuantity, Map<Integer, StoreDTO> allData, int storeSN, int itemSN) throws DiscardException {
        boolean sellPerProduct = allData.get(storeSN).getIdToSell().get(itemSN).getProduct().getPricing().equals("Product");
        productQuantity(idToQuantity, itemSN, sellPerProduct);
    }

    private static void productQuantity(Map<Integer, Double> idToQuantity, int itemSN, boolean sellPerProduct) throws DiscardException {
        double convertedQuantity;
        boolean finish = false;
        Scanner in = new Scanner(System.in);
        String quantity;
        System.out.println("Please enter the quantity that you would like to purchase (or press 'q' to exit): ");

        do {
            try {
                quantity = in.nextLine();
                checkQ(quantity);
                convertedQuantity = Double.parseDouble(quantity);
                finish = updateProductInOrder(idToQuantity, itemSN, convertedQuantity, sellPerProduct);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, this is not a number " + e.getMessage());
            }
        } while (!finish);
    }

    private static boolean updateProductInOrder(Map<Integer, Double> idToQuantity, int itemSN, double quantity, boolean sellPerProduct) {
        boolean finish = false;
        double value;

        if (quantity > 0) {
            if (sellPerProduct) {
                if (quantity % 1 == 0) {
                    value = idToQuantity.getOrDefault(itemSN, 0.0);
                    idToQuantity.put(itemSN, quantity + value);
                    finish = true;
                } else {
                    System.out.println("Invalid input, please enter integer");
                }
            } else {
                value = idToQuantity.getOrDefault(itemSN, 0.0);
                idToQuantity.put(itemSN, quantity + value);
                finish = true;
            }
        } else {
            System.out.println("Invalid input, please enter positive number");
        }

        return finish;
    }

    public static void checkQ(String input) throws DiscardException {
        if (input.equals("q") || input.equals("Q")) {
            throw new DiscardException();
        }
    }

    public static void showOrder(IMarketEngine market, Map<Integer, Double> idToQuantity, StoreDTO store, Coordinates buyerCoordinates, Date date) throws DiscardException, InvalidActionException {
        DecimalFormat df = new DecimalFormat("#.##");
        double totalOrderPrice = 0, distance, deliveryPrice;
        String newLine = System.lineSeparator(), pricing;
        Map<Integer, SellDTO> idToSell = store.getIdToSell();
        System.out.println("Order summary : " + newLine + "--------------------");

        for (Integer key : idToQuantity.keySet()) {
            ProductDTO product = idToSell.get(key).getProduct();
            pricing = product.getPricing().equals("Weight") ? "Price per kilogram" : "Price per product";
            System.out.println(product.getSerialNumber() + ". " + product.getName() + "   -   " + pricing + " : "
                    + df.format(idToSell.get(key).getPrice()) + newLine
                    + "Quantity : " + df.format(idToQuantity.get(key)) + newLine
                    + "Total product price : " + df.format((idToSell.get(key).getPrice() * idToQuantity.get(key))) + newLine);
            totalOrderPrice += idToSell.get(key).getPrice() * idToQuantity.get(key);
        }

        distance = Coordinates.getDistance(store.getCoordinateX(), store.getCoordinateY(), buyerCoordinates.getX(), buyerCoordinates.getY());
        deliveryPrice = distance * store.getPPK();
        totalOrderPrice += deliveryPrice;

        System.out.println("Delivery price per kilometer : " + store.getPPK() + newLine
                + "Distance from store : " + df.format(distance) + " KM" + newLine
                + "Delivery price : " + df.format(deliveryPrice) + newLine
                + "Total order price : " + df.format(totalOrderPrice) + newLine
        );
        confirmOrder(market, idToQuantity, date, store.getSerialNumber(), totalOrderPrice, deliveryPrice, false);
    }

    public static void showDynamicOrder(IMarketEngine market, Map<Integer, StoreDTO> idToStores, Map<Integer, Double> idToQuantity, Set<OrderDTO> dynamicOrder, Coordinates buyerCoordinates) throws DiscardException, InvalidActionException {
        DecimalFormat df = new DecimalFormat("#.##");
        double totalOrderPrice = 0, distance, currentOrderPrice, totalDeliveryPrice = 0;
        String newLine = System.lineSeparator(), pricing;
        ProductDTO product;
        System.out.println("Order summary : " + newLine + "--------------------");

        for (OrderDTO order : dynamicOrder) {
            StoreDTO store = idToStores.get(order.getStoreSerialNumber());
            currentOrderPrice = order.getProductsPrice() + order.getDeliveryPrice();
            distance = Coordinates.getDistance(store.getCoordinateX(), store.getCoordinateY(), buyerCoordinates.getX(), buyerCoordinates.getY());
            System.out.println("Order " + order.getSerialNumber() + "\\" + order.getSubOrderSerialNumber() +  "    -    from store : " + store.getName());
            System.out.println("Product : " + newLine + "----------------------------------");
            for (Integer productId : order.getIdToQuantity().keySet()) {
                product = store.getIdToSell().get(productId).getProduct();
                pricing = product.getPricing().equals("Weight") ? "Price per kilogram : " : "Price per product : ";
                System.out.println(product.getSerialNumber() + ". " + product.getName() + "   -   " + pricing
                        + df.format(store.getIdToSell().get(productId).getPrice()) + newLine
                        + "Quantity : " + df.format(idToQuantity.get(productId)) + newLine
                        + "Total product price : " + df.format((store.getIdToSell().get(productId).getPrice() * idToQuantity.get(productId))) + newLine);
            }
            System.out.println("Delivery price per kilometer : " + store.getPPK() + newLine
                    + "Distance from store : " + df.format(distance) + " KM" + newLine
                    + "Delivery price : " + df.format(order.getDeliveryPrice()) + newLine
                    + "Total order price from this store : " + df.format(currentOrderPrice) + newLine);
            totalDeliveryPrice += order.getDeliveryPrice();
            totalOrderPrice += order.getProductsPrice() + order.getDeliveryPrice();
        }

        System.out.println("----------------------------------" + newLine + "Total order price from all stores : " + df.format(totalOrderPrice) + newLine);
        confirmOrder(market, idToQuantity, null, -1, totalOrderPrice, totalDeliveryPrice, true);
    }

    public static void confirmOrder(IMarketEngine market, Map<Integer, Double> idToQuantity, Date date, int storeSN, double totalPrice, double deliveryPrice, boolean dynamicOrder) throws DiscardException, InvalidActionException {
        Scanner in = new Scanner(System.in);
        int option;
        String input;
        boolean finish = false;
        System.out.println("Please confirm : ");
        Arrays.stream(OrderConfirm.values()).forEach(System.out::println);
        do {
            try {
                input = in.nextLine();
                option = Integer.parseInt(input);
                switch (OrderConfirm.values()[option - 1]) {
                    case CONFIRM:
                        if (dynamicOrder) {
                            market.addDynamicOrder();
                        } else {
                            market.addOrder(idToQuantity, date, storeSN, totalPrice, deliveryPrice);
                        }
                        finish = true;
                        break;
                    case CANCEL:
                        throw new DiscardException();
                    default:
                        System.out.println("Invalid input, please enter a valid option");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong input, please enter a number between (1 - " + OrderConfirm.values().length + ")");
            }
        } while (!finish);
    }

    public static StoreDTO userStorePick(Map<Integer, StoreDTO> idToStores) {
        Scanner in = new Scanner(System.in);
        String input;
        StoreDTO storePick;
        System.out.println("Please choose store by it's number : ");
        idToStores.values().forEach(store -> printStoreDetails(store, false));

        do {
            try {
                input = in.nextLine();
                storePick = idToStores.get(Integer.parseInt(input));
                if (storePick != null) {
                    return storePick;
                } else {
                    System.out.println("Store number doesnt exist! please choose again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number.  " + e.getMessage() + System.lineSeparator() + "please choose again");
            }
        } while (true);

    }

    //6 - update add or remove
    public static int userProductPick(IMarketEngine market, Map<Integer, StoreDTO> idToStores, int storeSerialNumber) {
        boolean finish = false;
        int productPick = -1;
        Scanner in = new Scanner(System.in);
        System.out.println("Please choose product by it's number (or press 'q' to exit) : ");
        printProductsToOrder(idToStores.get(storeSerialNumber), idToStores.values());
        String input;

        do {
            try {
                input = in.nextLine();
                checkQ(input);
                productPick = Integer.parseInt(input);
                if (market.isProductExist(productPick)) {
                    if (!idToStores.get(storeSerialNumber).getIdToSell().containsKey(Integer.parseInt(input))) {
                        finish = true;
                    } else {
                        System.out.println("Store already sell this product!");
                    }
                } else {
                    System.out.println("Product doesn't exist, please pick again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number.  " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            } catch (DiscardException e) {
                productPick = -1;
                finish = true;
            }
        } while (!finish);

        return productPick;
    }

    public static void deleteProduct(IMarketEngine market, StoreDTO storePick) {
        Scanner in = new Scanner(System.in);
        boolean finish = false;
        int productSerialNumber;
        Set<StoreDTO> store = new HashSet<>();
        store.add(storePick);

        do {
            try {
                System.out.println("Please pick product by his number : ");
                printProductsToOrder(storePick, store);
                String input = in.nextLine();
                productSerialNumber = Integer.parseInt(input);
                if (storePick.getIdToSell().containsKey(productSerialNumber)) {
                    String productName = storePick.getIdToSell().get(productSerialNumber).getProduct().getName();
                    market.deleteProduct(storePick.getSerialNumber(), productSerialNumber);
                    finish = true;
                    System.out.println("Product " + productName + " was deleted from store");
                } else {
                    System.out.println("Product doesn't exist, please pick again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number.  " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            } catch (InvalidActivityException e) {
                System.out.println(e.getMessage());
                finish = true;
            }
        } while (!finish);
    }

    public static void addProduct(IMarketEngine market, Map<Integer, StoreDTO> idToStores, StoreDTO storePick) {
        Scanner in = new Scanner(System.in);
        boolean finish = false;
        String input;
        int productPick = userProductPick(market, idToStores, storePick.getSerialNumber());
        double price;

        if (productPick != -1) {
            do {
                try {
                    System.out.println("Please enter the product price : ");
                    input = in.nextLine();
                    price = Double.parseDouble(input);
                    if (price <= 0) {
                        System.out.println("Price must be positive! ");
                    } else {
                        market.addNewProduct(storePick.getSerialNumber(), productPick, price);
                        //System.out.println(); product was added
                        finish = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input, this is not a number " + e.getMessage() + "\nPlease select again : ");
                }
            } while (!finish);
        }
    }

    public static void updateProductPrice(IMarketEngine market, StoreDTO storePick) {
        Scanner in = new Scanner(System.in);
        boolean finish = false;
        String input;
        double newPrice ;
        int productSerialNumber;
        Set<StoreDTO> store = new HashSet<>();
        store.add(storePick);

        do {
            try {
                System.out.println("Please pick product by his number : ");
                printProductsToOrder(storePick, store);
                input = in.nextLine();
                productSerialNumber = Integer.parseInt(input);
                if (storePick.getIdToSell().containsKey(productSerialNumber)) {
                    do {
                        System.out.println("Please enter the new price for this product : ");
                        input = in.nextLine();
                        newPrice = Double.parseDouble(input);
                        if (newPrice <= 0) {
                            System.out.println("Price must be positive.");
                        } else {
                            market.updateProductPrice(storePick.getSerialNumber(), productSerialNumber, newPrice);
                            System.out.println("Price of "+ storePick.getIdToSell().get(productSerialNumber).getProduct().getName() + " change to : " + newPrice);
                            finish = true;
                        }
                    } while (!finish);
                } else {
                    System.out.println("Product doesn't exist, please pick again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number.  " + e.getMessage() + System.lineSeparator() + "Please select again : ");
            }
        } while (!finish);
    }
}