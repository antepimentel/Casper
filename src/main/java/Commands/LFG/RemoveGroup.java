package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class RemoveGroup extends AbstractCommand {

    private static String command = "removegroup";
    private static String desc = "temp";
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
    public int getCategory() {
        return CommandCategory.GENERAL;
    }

    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), Integer.parseInt(args[0]));

        if(PermissionHandler.isLeaderOrMod(msg.getMember(), g)){

            GroupSQL.delete(g);

            msg.getChannel().sendMessage("Removed group: " + g.getID()).queue();
            //LFGHandler.getGroups().remove(g);
        }
    }
}
