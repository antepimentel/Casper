package Commands.General;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

public class Ping extends AbstractCommand {

    private static String command = "ping";
    private static String desc = "Ping the bot";
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

    @Override
    public void run(Message msg) {
        msg.getChannel().sendMessage("This works?").queue();
        msg.getChannel().sendMessage(msg.getMember().getUser().getId()).queue();

//        msg.getGuild().getId();
//        msg.getGuild().getName();
        MainSQLHandler.addServer(msg.getGuild().getId(), msg.getGuild().getName());
    }
}
