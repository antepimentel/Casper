package Commands.General;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import net.dv8tion.jda.core.entities.Message;

public class Help extends AbstractCommand {

    private static String command = "help";
    private static String desc = "temp";
    private static String[] inputs = {};

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

    public void run(Message msg){
        msg.getChannel().sendMessage("Success!").queue();
    }
}
