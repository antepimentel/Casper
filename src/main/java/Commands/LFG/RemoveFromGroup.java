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

public class RemoveFromGroup extends AbstractCommand {

    private static String command = "removefromgroup";
    private static String desc = "Remove a player from a group";
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
        Group g = null;


        PermissionHandler.checkModPermissions(msg.getMember());
        g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);
        List<Member> mentions = msg.getMentionedMembers();
        response = "Removing from group " + g.getID() + ": ";

        for(int i = 0; i < mentions.size(); i++){
            response = response + mentions.get(i).getEffectiveName() + " ";
            g.removePlayer(mentions.get(i));

        }

        GroupSQL.updatePlayers(g);
        LFGHandler.refreshGroup(msg.getGuild().getId(), g);
        msg.getChannel().sendMessage(response).queue();
    }
}
