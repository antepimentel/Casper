package Commands.LFG;

import Core.PermissionHandler;
import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import JDBC.GroupSQL;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class ChangeName extends AbstractCommand {

    private static String command = "changename";
    private static String desc = "temp";
    private static String[] inputs = {"ID", "name"};

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

    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        String name = "";
        String response = "";
        int ID = Integer.parseInt(args[0]);

        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), ID);

        // Build name
        for(int i = 1; i < args.length; i++){
            name = name + args[i] + " ";
        }

        if(PermissionHandler.isLeaderOrMod(msg.getMember(), g)){
            g.setName(name);
            GroupSQL.updateName(g);
            response = response + g.toString();
        }

        msg.getChannel().sendMessage(response).queue();
    }
}
