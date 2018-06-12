package Exceptions;

public class GroupIsEmptyException extends CustomAbstractException {
    private int groupID;
    private String message;

    public GroupIsEmptyException(int ID){
        super();
        this.groupID = ID;
        this.message = "Group: " + ID + " is empty, removing group";
    }

    public int getGroupID() {
        return groupID;
    }

    public String getMessage(){
        return message; }
}
