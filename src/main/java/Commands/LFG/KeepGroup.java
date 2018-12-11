package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import Core.PermissionHandler;
import Exceptions.GroupNotFoundException;
import Exceptions.InvalidPermissionsException;
import Exceptions.NoArgumentsGivenException;
import JDBC.GroupSQL;
import JDBC.MainSQLHandler;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class KeepGroup extends AbstractCommand {

    private static String command = "keepgroup";
    private static String desc = "Keep a group that has been marked for deletion, lasts until the groups are checked again (every 10 minutes)";
    private static String[] inputs = {"ID"};

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
    public CommandCategory getCategory() {
        return CommandCategory.LFG;
    }

    @Override
    public void run(Message msg) throws NoArgumentsGivenException, GroupNotFoundException, InvalidPermissionsException {

        String[] args = getInputArgs(msg);
       int ID = Integer.parseInt(args[0]);
        String response = "";
       Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);
        if(PermissionHandler.isLeaderOrMod(msg.getMember(), g)) {
            boolean found = false;
            for (int i = 0; i < LFGHandler.getDeletionQueue().size(); i++) {
                if (LFGHandler.getDeletionQueue().get(i).getID() == g.getID()) found = true;
            }

            if (!found) {
                response = "Group " + g.getID() + " is not marked for deletion.";
            } else {
                LFGHandler.removeFromDeletionQueue(g);
                Date now = new Date();
                long diff = LFGHandler.getDateDiff(LFGHandler.getLastCheck(), now, TimeUnit.MINUTES);
                System.out.println(diff);
                response = "Group " + g.getID() + " has been unmarked for deletion, you have " + (10 - diff) + " minutes to change the time of your group to stop it from being deleted.";
            }

            msg.getChannel().sendMessage(response).queue();
        }
    }
}
