package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;

public class SetGroupActivity extends AbstractCommand {

    private static String command = "setactivity";
    private static String desc = "temp";
    private static String[] inputs = {"ID", "code"};

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

    public void run(Message msg) throws CustomAbstractException, SQLException {
        String[] args = getInputArgs(msg);
        String name = "";
        String response = "";
        int ID = Integer.parseInt(args[0]);
        String code = args[1];

        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);

        if(PermissionHandler.isLeaderOrMod(msg.getMember(), g)){
            LFG.GroupActivityType type = Group.getGroupTypeByCode(code);
            if(type != null) {
                g.setGroupActivityType(type);
                LFGHandler.refreshGroup(msg.getGuild().getId(), g);
                GroupSQL.updateTypeCode(g);

                response = "Set Group " + g.getID() + "'s activity to " + type.getCode();
            } else {
                response = "Unknown group activity code: "+code;
            }
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
