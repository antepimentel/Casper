package Commands.Admin;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;

import java.sql.SQLException;

public class Initialize extends AbstractCommand {
    private static String command = "initialize";
    private static String desc = "Initialize a server";
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
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    @Override
    public void run(Message msg) throws SQLException {
        msg.getChannel().sendMessage("Initializing "+msg.getGuild().getName()).queue();
        MainSQLHandler.addServer(msg.getGuild().getId(), msg.getGuild().getName());
        msg.getChannel().sendMessage(msg.getGuild().getName() + " initialized!").queue();
    }
}
