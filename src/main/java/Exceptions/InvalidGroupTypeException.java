package Exceptions;

public class InvalidGroupTypeException extends CustomAbstractException {

    private String message;

    public InvalidGroupTypeException(String platform){
        super();
        this.message = platform + " is not a valid group platform.";
    }


    public String getMessage(){
        return message; }
}