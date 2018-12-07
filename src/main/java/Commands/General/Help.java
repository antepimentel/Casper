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
import java.lang.reflect.Modifier;
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
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    public void run(Message msg) {
        String response = "";

        //Existing commands:
        HashMap<String, AbstractCommand> commands = CommandHandler.getCommands();
        Iterator commandsIter = commands.entrySet().iterator();
        HashMap<CommandCategory, ArrayList<AbstractCommand>> sortedCommands = new HashMap<>();

        while(commandsIter.hasNext()) {
            Map.Entry pair = (Map.Entry)commandsIter.next();
            AbstractCommand command = (AbstractCommand)pair.getValue();

            if(sortedCommands.containsKey(command.getCategory())) sortedCommands.get(command.getCategory()).add(command);
            else sortedCommands.put(command.getCategory(), new ArrayList<AbstractCommand>());
        }

        Iterator sortedCommandsIter = sortedCommands.entrySet().iterator();

        while(sortedCommandsIter.hasNext()) {
            Map.Entry pair = (Map.Entry)sortedCommandsIter.next();
            Field[] fields = CommandCategory.class.getFields();
            CommandCategory category = null;
            String categoryName = "";
            ArrayList<AbstractCommand> commandArrayList = (ArrayList<AbstractCommand>) pair.getValue();
            for(Field f : fields) {
                if(Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    try {
                        CommandCategory value = (CommandCategory) f.get(null);
                        if(value.equals( pair.getKey())) {
                            category = value;
                            categoryName = f.getName();
                        }
                    } catch (IllegalAccessException e){
                        System.out.println(e.getMessage() + "\n" + e.getStackTrace());
                    }
                }
            }

            response += "**["+categoryName+" Commands]**\n*"+category.getDescription()+"*\n";

            for(AbstractCommand command : commandArrayList) {
                response += Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + command.getCommand()
                        + "\n\tInfo: " + command.getDescription()
                        + ".\n\tUsage: "
                        + command.getUsage(command.getCommand(), command.getInputs()) + "\n";
            }

            response += "\n";
        }

        //Custom commands:
        response += "**[Custom commands]**\n*Custom commands added by a server's moderator.*\n";
        HashMap<String, String> customCommands = CommandHandler.getCustomCommands();
        Iterator customCommandsIter = customCommands.entrySet().iterator();
        while(customCommandsIter.hasNext()) {
            Map.Entry pair = (Map.Entry)customCommandsIter.next();
            response += Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + (String)pair.getKey()
                     + "\n\tUsage: ```"
                     + Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + pair.getKey() + "```\n";
        }

        PrivateChannel dmChannel = msg.getMember().getUser().openPrivateChannel().complete();
        dmChannel.sendMessage(response).queue();
    }
}
