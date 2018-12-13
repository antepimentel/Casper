package Exceptions;

public class NameTooLongException extends CustomAbstractException {

    String message;

    public NameTooLongException(){
        this.message = "That name is too long, the limit is 150 characters";
    }
    @Override
    public String getMessage() {
        return message;
    }
}
