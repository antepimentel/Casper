package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;

public class MyGroups extends AbstractCommand {

    private static String command = "mygroups";
    private static String desc = "temp";
    private static String[] inputs = {};

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public String[] getInputs() {
        return inputs;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.LFG;
    }

    @Override
    public void run(Message msg) throws CustomAbstractException {
        String response = "";

        ArrayList<Group> groups = LFGHandler.getGroupsByMember(msg.getMember());
        for(int i = 0; i < groups.size(); i++){
            response = response + groups.get(i).toString()+"\n\n";
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
