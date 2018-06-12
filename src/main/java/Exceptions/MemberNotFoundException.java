package Exceptions;

import net.dv8tion.jda.core.entities.Member;

public class MemberNotFoundException extends CustomAbstractException {

    private Member member;
    private int groupID;
    private String message = "";

    public MemberNotFoundException(int id, Member m){
        super();
        this.groupID = id;
        this.member = m;
        this.message = "Player: " + member.getEffectiveName() + " is not in group: " + groupID;
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
