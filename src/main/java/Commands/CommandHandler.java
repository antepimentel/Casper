package Commands;

import Core.Bot;
import Core.PropertyKeys;
import Core.Utility;
import Exceptions.CustomAbstractException;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.reflections.Reflections;

public class CommandHandler extends ListenerAdapter {

    private static final HashMap<String, AbstractCommand> commands = new HashMap<>();

    public void onMessageReceived(MessageReceivedEvent e){

        // Ignore Private Messages
        if(e.getMessage().getGuild() == null || e.getAuthor().getId().equals(Bot.jda.getSelfUser().getId())) {
            return;
        }

        String delim = Bot.props.getProperty(PropertyKeys.DELIMITER_KEY);
        if(e.getMessage().getContentRaw().startsWith(delim)){
            String[] args = e.getMessage().getContentRaw().substring(delim.length()).split(" ");
            String customCommandResponse = MainSQLHandler.checkCustomCommand(e.getGuild().getId(), args[0]);

            if(commands.containsKey(args[0])){
                AbstractCommand com = commands.get(args[0]);
                try {
                    boolean checkDisabled = MainSQLHandler.checkDisabledCommand(e.getGuild().getId(), com.getCommand());
                    if(!checkDisabled){

                        CommandCategory category = com.getCategory();
                        if(category.canRun(e.getMember(), e.getMessage())) {
                            com.run(e.getMessage());
                        }
                    } else {
                        System.out.println("DISABLED");
                    }
                } catch (CustomAbstractException exp){
                    exp.printStackTrace();
                    e.getMessage().getChannel().sendMessage(exp.getMessage()).queue();
                } catch (Exception exp) {
                    System.out.println(exp.getMessage());
                    exp.printStackTrace();
                    e.getMessage().getChannel().sendMessage("There was an error with that command or its inputs, please try" + com.getUsage(com.getCommand(), com.getInputs())).queue();
                }
            } else if(customCommandResponse != null){
                e.getMessage().getChannel().sendMessage(customCommandResponse).queue();
            } else {
                String response = "";

                HashMap<String, AbstractCommand> similarCommands = getSimilarCommands(args[0]);
                response = Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + args[0] + " is not a command, ";
                System.out.println(similarCommands.keySet().size());
                if(similarCommands.keySet().size() == 0) response += "and no similar commands were found.";
                else response += "did you mean: ";

                for(String name : similarCommands.keySet()) {
                    response += "\n`" + Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + name + "`";
                }

                response += "\n\nType " + Bot.props.getProperty(PropertyKeys.DELIMITER_KEY) + "help to get a list of available commands";
                e.getMessage().getChannel().sendMessage(response).queue();
            }
        }
    }

    public void init(){
        loadCommands();

    }

    private static void loadCommands(){
        Reflections reflections = new Reflections("Commands");
        Set<Class<? extends AbstractCommand>> classes = reflections.getSubTypesOf(AbstractCommand.class);
        for (Class<? extends AbstractCommand> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers())) {
                    continue;
                }
                AbstractCommand c = s.getConstructor().newInstance();
                if (!c.isEnabled()) {
                    continue;
                }
                if (!commands.containsKey(c.getCommand())) {
                    commands.put(c.getCommand(), c);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String, AbstractCommand> getCommands(){
        return commands;
    }

    public static HashMap<String, AbstractCommand> getSimilarCommands(String input) {
        HashMap<String, AbstractCommand> availableCommands = getCommands();
        HashMap<String, AbstractCommand> similarCommands = new HashMap<>();
        for(String name : availableCommands.keySet()) {
            int distance = Utility.levenshtienDistance(input, name);

            if(distance <= 2){
                similarCommands.put(name, availableCommands.get(name));
            }
        }

        return similarCommands;
    }


}
