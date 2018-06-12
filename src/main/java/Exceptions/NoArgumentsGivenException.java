package Exceptions;

import Commands.AbstractCommand;

public class NoArgumentsGivenException extends CustomAbstractException {

    private AbstractCommand com;

    public NoArgumentsGivenException(AbstractCommand command){
        super();
        com = command;
    }

    public AbstractCommand getCommandName() {
        return com;
    }

    public String getMessage(){
        return "There was an error with that command or its inputs, please try" + com.getUsage(com.getCommand(), com.getInputs());
    }
}
