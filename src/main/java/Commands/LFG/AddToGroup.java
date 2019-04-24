package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.*;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;
import java.util.List;

public class AddToGroup extends AbstractCommand {

    private static String command = "addtogroup";
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
    public CommandCategory getCategory() {
        return CommandCategory.LFG;
    }

    public void run(Message msg) throws CustomAbstractException, SQLException {
        String response = "";
        String[] args = getInputArgs(msg);
        int ID = Integer.parseInt(args[0]);

        PermissionHandler.checkModPermissions(msg.getMember());
        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);
        List<Member> mentions = msg.getMentionedMembers();
        response = "Adding to group " + g.getID() + ": ";
        for(int i = 0; i < mentions.size(); i++){
            response = response + mentions.get(i).getEffectiveName() + " ";
            g.join(mentions.get(i));
        }
        GroupSQL.updatePlayers(g);
        LFGHandler.refreshGroup(msg.getGuild().getId(), g);
        msg.getChannel().sendMessage(response).queue();
    }
}
