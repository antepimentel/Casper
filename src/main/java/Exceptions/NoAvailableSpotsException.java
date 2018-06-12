package Exceptions;

import net.dv8tion.jda.core.entities.Member;

public class NoAvailableSpotsException extends CustomAbstractException {

    private Member member;
    private int groupID;
    private String message;

    public NoAvailableSpotsException(Member m, int ID){
        super();
        this.groupID = ID;
        this.member = m;
        this.message = member.getAsMention() + " no space available in group: " + groupID;
    }

    public int getGroupID() {
        return groupID;
    }

    public Member getMember() {
        return member;
    }

    public String getMessage(){
        return message; }
}
