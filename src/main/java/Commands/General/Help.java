package Commands.General;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Commands.CommandHandler;
import Core.Bot;
import Core.PropertyKeys;
import Core.Utility;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Help extends AbstractCommand {

    private static String command = "help";
    private static String desc = "Send a list of commands and usages examples";
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

    public void run(Message msg) {
        String response = "";
        // Copy command handler or steal from it, print all command names and descriptions
        HashMap<String, AbstractCommand> commands = CommandHandler.getCommands();
        Iterator commandsIter = commands.entrySet().iterator();
        HashMap<Integer, ArrayList<AbstractCommand>> sortedCommands = new HashMap<>();

        while(commandsIter.hasNext()) {
            Map.Entry pair = (Map.Entry)commandsIter.next();
            AbstractCommand command = (AbstractCommand)pair.getValue();

            if(sortedCommands.containsKey(command.getCategory())) sortedCommands.get(command.getCategory()).add(command);
            else sortedCommands.put(command.getCategory(), new ArrayList<AbstractCommand>());
        }

        Iterator sortedCommandsIter = sortedCommands.entrySet().iterator();
        CommandCategory categories = new CommandCategory();

        while(sortedCommandsIter.hasNext()) {
            Map.Entry pair = (Map.Entry)sortedCommandsIter.next();
            Field[] fields = CommandCategory.class.getFields();
            String category = "";
            ArrayList<AbstractCommand> commandArrayList = (ArrayList<AbstractCommand>) pair.getValue();
            for(Field f : fields) {
                f.setAccessible(true);
                try {
                    Integer value = new Integer(f.getInt(null));
                    if(value.equals((Integer) pair.getKey())) {
                        category = f.getName();
                    }
                } catch (IllegalAccessException e){
                    System.out.println(e.getMessage() + "\n" + e.getStackTrace());
                }
            }

            response += "**["+category+" Commands]**\n";

            for(AbstractCommand command : commandArrayList) {
                response += Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + command.getCommand()
                        + "\n\tInfo: " + command.getDescription()
                        + ".\n\tUsage: "
                        + command.getUsage(command.getCommand(), command.getInputs()) + "\n";
            }

            response += "\n";
        }

        PrivateChannel dmChannel = msg.getMember().getUser().openPrivateChannel().complete();
        dmChannel.sendMessage(response).queue();
    }
}
