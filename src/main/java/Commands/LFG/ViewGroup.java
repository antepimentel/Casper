package Commands.LFG;

import Commands.AbstractCommand;
import Exceptions.CustomAbstractException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

public class ViewGroup extends AbstractCommand {

    private static String command = "group";
    private static String desc = "temp";
    private static String[] inputs = {"ID"};

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
    public int getCategory() {
        return 0;
    }

    @Override
    public void run(Message msg) throws CustomAbstractException {
        String response = "";

        String[] args = getInputArgs(msg);
        Group g = LFGHandler.findGroupByID(Integer.parseInt(args[0]));
        response = g.toStringFull();

        msg.getChannel().sendMessage(response).queue();
    }
}
