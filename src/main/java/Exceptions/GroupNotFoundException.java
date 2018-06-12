package Exceptions;

import net.dv8tion.jda.core.entities.Member;

public class GroupNotFoundException extends CustomAbstractException {

    private int ID;
    private Member m;
    private String message;

    public GroupNotFoundException(int ID){
        super();
        this.ID = ID;
        message = "ID: " + ID + " is not a valid group";
    }

    public GroupNotFoundException(Member m){
        super();
        this.m = m;
        message = m.getAsMention() + " is not is any groups";
    }

    public int getID(){
        return ID;
    }

    public String getMessage(){
        return message; }
}
