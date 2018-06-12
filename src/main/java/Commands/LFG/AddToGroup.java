package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.*;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

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
    public int getCategory() {
        return CommandCategory.GENERAL;
    }

    public void run(Message msg) throws CustomAbstractException {
        String response = "";
        String[] args = getInputArgs(msg);
        int ID = Integer.parseInt(args[0]);

        PermissionHandler.checkModPermissions(msg.getMember());
        Group g = LFGHandler.findGroupByID(ID);
        List<Member> mentions = msg.getMentionedMembers();
        response = "Adding to group " + g.getID() + ": ";
        for(int i = 0; i < mentions.size(); i++){
            response = response + mentions.get(i).getEffectiveName() + " ";
            g.join(mentions.get(i));
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
