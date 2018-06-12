package Exceptions;

public abstract class CustomAbstractException extends Exception {

    public CustomAbstractException(){
        super();
    }

    public abstract String getMessage();
}
