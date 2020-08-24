package exceptions;

public class ValueOutOfRangeException extends Exception {
    private int small = 0;
    private int big = 0;
    String massage = "Exception";

    public ValueOutOfRangeException(String errorParameter, int small, int big){
        super();
        this.small = small;
        this.big = big;
        this.massage = errorParameter;
    }

    public ValueOutOfRangeException(String errorParameter){
        super();
        this.massage = errorParameter;
    }

    @Override
    public String getMessage() {
        if(this.small != this.big){
            return massage + ", Value is out out range : [" + small + "," + big + "]";
        }
        return massage + ", Value is out of range";
    }
}
