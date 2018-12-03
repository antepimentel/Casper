package Commands.LFG;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Exceptions.CustomAbstractException;
import LFG.Group;
import LFG.LFGHandler;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Countdown extends AbstractCommand {

    private static String command = "countdown";
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
        int id = Integer.parseInt(args[0]);

        Group g = LFGHandler.findGroupByID(msg.getGuild().getId(), id);
        long diff = LFGHandler.getDateDiff(new Date(), g.getDate(), TimeUnit.MINUTES);
        response = "Time remaining: " + LFGHandler.parseDiff(diff);

        msg.getChannel().sendMessage(response).queue();
    }
}
