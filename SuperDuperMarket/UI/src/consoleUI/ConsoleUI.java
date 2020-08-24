package consoleUI;

import DTOClasses.OrderDTO;
import DTOClasses.SellDTO;
import DTOClasses.StoreDTO;
import enums.Menu;
import enums.OrderType;
import enums.ProductAction;
import exceptions.*;
import market.Coordinates;
import market.IMarketEngine;
import market.Sell;
import xml.XmlReader;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static consoleUI.IOValidations.*;

public class ConsoleUI implements Runnable {
    IMarketEngine market = null;

    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.run();
    }

    @Override
    public void run() {
        System.out.println("C:\\Users\\srsrs\\Desktop\\java\\check\\ex1-error-3.2.xml");
        System.out.println("C:\\Users\\srsrs\\Desktop\\java\\check\\ex1-small.xml");
        System.out.println("C:\\Users\\srsrs\\Desktop\\java\\check\\ex1-big.xml");
        System.out.println("C:\\Users\\srsrs\\Desktop\\java\\check\\orderHistory.dat");
        System.out.println("Welcome to Super Duper store!");
        Scanner scanner = new Scanner(System.in);
        boolean finish = false, validInput = false;

        do {
            do {
                try {
                    System.out.println("Please select : ");
                    Arrays.stream(Menu.values()).forEach(System.out::println);
                    String input = scanner.nextLine();
                    finish = menuPick(Menu.values()[Integer.parseInt(input) - 1]);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input, this is not a number " + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Wrong input, please enter a number between (1 - " + Menu.values().length + ")");
                } catch (DiscardException ignored) {
                }
            } while (!validInput);
        } while (!finish);

        System.out.println("Thank you for visiting Super Duper store. BYE BYE");
    }

    public boolean menuPick(Menu option) throws DiscardException {
        if (this.market == null && !option.equals(Menu.READ_FILE) && !option.equals(Menu.EXIT)) {
            System.out.println("Invalid action, Please load a file before.");
        } else {
            switch (option) {
                case READ_FILE:
                    readFile();
                    break;
                case SHOW_STORES_DETAILS:
                    showStoresDetails();
                    break;
                case SHOW_ALL_PRODUCTS:
                    showProductsDetails();
                    break;
                case MAKE_ORDER:
                    makeOrder();
                    break;
                case ORDER_HISTORY:
                    orderHistory();
                    break;
                case UPDATE_DELETE_OR_ADD_PRODUCT:
                    updateDeleteOrAddProduct();
                    break;
                case SAVE_ORDERS:
                    saveOrders();
                    break;
                case READ_ORDER_HISTORY:
                    readOrderHistory();
                    break;
                case EXIT:
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    private void readOrderHistory() {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter full file path : ");
        boolean finish = false;

        do {
            try {
                Path path = Paths.get(in.nextLine());
                if (!path.isAbsolute()) {
                    System.out.println("The path is not Absolute");
                } else if (Files.isDirectory(path)) {
                    System.out.println("This is a directory");
                }else if (!path.toString().toLowerCase().endsWith(".dat")) {
                    System.out.println("This is not an dat file");
                } else{
                    finish = true;
                    this.market.loadOrderHistoryFromFile(path);
                    System.out.println("File loaded successfully" + System.lineSeparator());
                }
            }catch(InvalidPathException e){
                System.out.println("Invalid path " + e.getMessage());
            } catch (InvalidActionException e) {
                System.out.println("Invalid action : " + e.getMessage());
            }
        }while(!finish);
    }

    private void saveOrders() {
        if(this.market.getAllOrders() == null){
            System.out.println("There are no orders in the Market to save");
        }
        else {
            Scanner in = new Scanner(System.in);
            System.out.println("Please enter a path to directory to save the file : ");
            boolean finish = false;
            do {
                try {
                    Path path = Paths.get(in.nextLine());
                    if (!path.isAbsolute()) {
                        System.out.println("The path is not Absolute");
                    } else if (!Files.isDirectory(path)) {
                        System.out.println("This is not a directory");
                    } else {
                        finish = true;
                        this.market.saveOrderHistory(path);
                        System.out.println("File saved successfully");
                    }
                } catch (InvalidPathException e) {
                    System.out.println("Invalid path " + e.getMessage());
                }
            } while (!finish);
        }
    }

    public void readFile() {
        Scanner scanner = new Scanner(System.in);
        boolean validInput;


        try {
            System.out.println("Please enter full file path : ");
            Path path = Paths.get(scanner.nextLine());
            validInput = pathValidation(path);
            if (validInput) {
                this.market = XmlReader.readFile(path);
                System.out.println("File loaded successfully");
            }
        } catch (InvalidPathException e) {
            System.out.println("Invalid path " + e.getMessage());
        } catch (JAXBException | ValueOutOfRangeException | IOException | InputMismatchException e) {
            System.out.println(e.getMessage());
        } catch (DuplicateException e) {
            System.out.println("Duplicate found : " + e.getMessage());
        }
    }

    public void showStoresDetails() { //print all stores data
        Map idToStores = market.getAllData(true);
        Collection<StoreDTO> stores = idToStores.values();
        stores.forEach(store -> printStoreDetails(store, true));
    }

    public void showProductsDetails() {                                 //go over all stores, and collect products data to Sell object -> print all
        Map<Integer, StoreDTO> idToStores = market.getAllData(false);        //all data
        Map<Integer, Sell> idToProductToPrint = new HashMap();          //collect all product here to print them
        for (StoreDTO store : idToStores.values()) {
            for (SellDTO sell : store.getIdToSell().values()) {
                Sell sellToAdd = idToProductToPrint.getOrDefault(sell.getProduct().getSerialNumber(), null);
                if (sellToAdd != null) {
                    sellToAdd.addQuantitySold(sell.getQuantitySold());
                    sellToAdd.addPrice(sell.getPrice());
                } else {
                    idToProductToPrint.put(sell.getProduct().getSerialNumber(), new Sell(sell));
                }
            }
        }
        idToProductToPrint.values().forEach(System.out::println);
    }

    public void makeOrder() throws DiscardException {
        Map<Integer, StoreDTO> idToStores = this.market.getAllData(false);
        Map<Integer, Double> idToQuantity;
        Set<OrderDTO> DynamicOrder;
        OrderType orderType = chooseOrderType();
        boolean finish = false;
        StoreDTO storePick = null;

        do {
            try {
                if (orderType.equals(OrderType.CHOOSE_STORE)) {
                    storePick = userStorePick(idToStores);
                }
                Date date = readDate();
                Coordinates coordinates = readCoordinates(this.market);
                if (storePick != null) {
                    idToQuantity = pickProductsFromStore(idToStores, storePick.getSerialNumber());
                    if (idToQuantity.size() == 0) {
                        System.out.println("Order is empty");
                    }
                    showOrder(this.market, idToQuantity, storePick, coordinates, date);
                } else {
                    idToQuantity = dynamicPickProductsFromStore(this.market.getAllProducts());
                    DynamicOrder = this.market.buildDynamicOrder(idToQuantity, date, coordinates);
                    showDynamicOrder(this.market ,idToStores, idToQuantity, DynamicOrder, coordinates);
                }
                finish = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong input, please enter a number between (1 - " + Menu.values().length + ")");
            } catch (InvalidActionException ignore) { }
        }while(!finish);
    }

    public void orderHistory() {
        Set<OrderDTO> orders = this.market.getAllOrders();
        if(orders == null){
            System.out.println("There are no orders in the Market");
        }
        else{
            orders.forEach(System.out::println);
        }
    }

    public void updateDeleteOrAddProduct() {
        Map allData = this.market.getAllData(false);
        boolean finish = false;
        Scanner in = new Scanner(System.in);
        StoreDTO storePick = userStorePick(allData);

        do {
            try {
                Arrays.stream(ProductAction.values()).forEach(System.out::println);
                String input = in.nextLine();
                switch (ProductAction.values()[Integer.parseInt(input) - 1]) {
                    case DELETE_PRODUCT:
                        deleteProduct(this.market, storePick);
                        finish = true;
                        break;
                    case ADD_PRODUCT:
                        addProduct(this.market, allData, storePick);
                        finish = true;
                        break;
                    case UPDATE_PRODUCT_PRICE:
                        updateProductPrice(this.market, storePick);
                        finish = true;
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input, this is not a number " + e.getMessage() + "\nPlease select again : ");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong input, please enter a number between (1 - " + ProductAction.values().length + ")");
            }
        }while(!finish);
    }

}
