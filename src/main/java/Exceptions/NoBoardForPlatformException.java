package Exceptions;

import net.dv8tion.jda.core.entities.Member;

public class NoBoardForPlatformException extends CustomAbstractException {

    private String message;

    public NoBoardForPlatformException(String platform){
        super();
        this.message = "There is no board created for " + platform + " in this server.";
    }


    public String getMessage(){
        return message; }
}
