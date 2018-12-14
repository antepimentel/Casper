package Exceptions;

import Exceptions.CustomAbstractException;

public class RadioAPIException extends CustomAbstractException {
    private String message;
    private Exception child;

    public RadioAPIException(String msg, Exception ex){
        message = msg;
        child = ex;
    }

    public Exception getChild() {
        return child;
    }

    @Override
    public String getMessage() {
        return message + "\n\tChild Error: " + getChild().getCause();
    }
}
