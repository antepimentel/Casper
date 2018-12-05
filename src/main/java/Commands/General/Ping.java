package Commands.General;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
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
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public void run(Message msg) {
        msg.getChannel().sendMessage(msg.getMember().getAsMention() + ", you called?\nPing: "+ Math.floor(Bot.jda.getPing()) + "ms.\n*This bot is built and maintained by NullRoz007 and Reusableduckk, please @ one of them or an @Bot_Commander if you run into any issues.*\n\nHelp keep the lights on: http://www.ko-fi.com/A8882QT2").queue();
    }
}
