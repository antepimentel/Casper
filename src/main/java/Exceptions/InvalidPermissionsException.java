package Exceptions;

import net.dv8tion.jda.core.entities.Member;

public class InvalidPermissionsException extends CustomAbstractException {

    private Member member;
    private String message = "";

    public InvalidPermissionsException(Member m){
        super();
        this.member = m;
        this.message = "Invalid permissions";
    }

    public Member getMember() {
        return member;
    }

    public String getMessage(){
        return message; }
}
