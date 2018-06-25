package Commands.LFG;

import Commands.AbstractCommand;
import Exceptions.*;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class LeaveGroup extends AbstractCommand {

    private static String command = "leavegroup";
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
        return 0;
    }

    @Override
    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        String response = "";
        Group g = null;

        try {
            g = LFGHandler.findGroupByID(msg.getGuild().getId(), Integer.parseInt(args[0]));
            LFGHandler.leave(msg.getGuild().getId(), g.getID(), msg.getMember());
            response = msg.getMember().getAsMention() + " removed from group: " + g.getID();
        } catch (GroupIsEmptyException e) {
            e.printStackTrace();
            GroupSQL.delete(g);
            response = e.getMessage();
        }
        msg.getChannel().sendMessage(response).queue();
    }
}
