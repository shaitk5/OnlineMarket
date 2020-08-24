package DTOClasses;

import java.util.Map;
import java.util.Set;

public class StoreDTO {
    private final int coordinateX;
    private final int coordinateY;
    private final int serialNumber;
    private final double PPK;
    private final String name;
    private final Map<Integer, SellDTO> idToSell;
    private final Set<OrderDTO> orders;

    public StoreDTO(int serialNumber, double PPK, String name,int coordinateX, int coordinateY, Map<Integer, SellDTO> idToSell, Set<OrderDTO> orders){
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.serialNumber = serialNumber;
        this.PPK = PPK;
        this.name = name;
        this.idToSell = idToSell;
        this.orders = orders;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, SellDTO> getIdToSell() {
        return idToSell;
    }

    public Set<OrderDTO> getOrders() {
        return orders;
    }

    public double getPPK() {
        return PPK;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }
}
