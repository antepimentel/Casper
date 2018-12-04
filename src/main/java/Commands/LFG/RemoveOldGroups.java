package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.InvalidPermissionsException;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RemoveOldGroups extends AbstractCommand {

    private static String command = "removeoldgroups";
    private static String desc = "Remove groups that have passed there start date";
    private static String[] inputs = {};

    @Override
    public String[] getInputs() {
        return inputs;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public int getCategory() {
        return CommandCategory.LFG;
    }

    public void run(Message msg) throws InvalidPermissionsException {

        PermissionHandler.checkModPermissions(msg.getMember());

        String response = "";

        ArrayList<Group> groups = LFGHandler.getGroupsByServer(msg.getGuild().getId());
        ArrayList<Group> toRemove = new ArrayList<Group>();

        for(int i = 0; i < groups.size(); i++){
            Group g = groups.get(i);
            long diff = LFGHandler.getDateDiff(new Date(), g.getDate(), TimeUnit.MINUTES);
            if(diff <= 0){
                response = response + g.getID() + " ";
                toRemove.add(g);
            }
        }

        // For safe removal
        for(int i = 0; i < toRemove.size(); i++){
            System.out.println(toRemove.get(i).toString());
            GroupSQL.delete(toRemove.get(i));
        }

        if(response.equals("")){
            response = "No groups to remove";
        } else {
            response = "Removed groups: " + response;
        }
        msg.getChannel().sendMessage(response).queue();
    }
}
