package Commands.Destiny;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import Core.Linker;
import Core.PropertyKeys;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;
import sun.applet.Main;

public class RemoveLink extends AbstractCommand {

    private static String command = "unlink";
    private static String desc = "Unlink a destiny account from your discord account";
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
        Linker linker = MainSQLHandler.queryLinker(msg.getAuthor().getId());
        if(linker != null) {
            MainSQLHandler.dropLinker(linker.getDiscordId());
            msg.getChannel().sendMessage("Removed "+linker.getDestinyMembershipId() + ":"+linker.getPlatform()+" from "+msg.getAuthor().getName()).queue();
        } else {
            msg.getChannel().sendMessage("You are not linked to a destiny account. Use the "+ Bot.props.getProperty(PropertyKeys.DELIMITER_KEY)+"link command to link a destiny account to your discord account.").queue();
        }
    }
}
