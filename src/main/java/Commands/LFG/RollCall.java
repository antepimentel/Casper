package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class RollCall extends AbstractCommand {

    private static String command = "rollcall";
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
        return CommandCategory.LFG;
    }

    public void run(Message msg) throws CustomAbstractException {
        String[] args = getInputArgs(msg);
        String response = "";

        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), Integer.parseInt(args[0]));

        for(int i = 0; i < g.getPlayers().size(); i++){
            response = response + g.getPlayers().get(i).getAsMention();
        }

        for(int i = 0; i < g.getSubs().size(); i++){
            response = response + g.getSubs().get(i).getAsMention();
        }
        response = response + " Roll call for group: " + g.getID();

        msg.getChannel().sendMessage(response).queue();
    }
}
