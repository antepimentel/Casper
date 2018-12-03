package Commands;

import Core.Bot;
import Core.PropertyKeys;
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
import java.util.HashMap;
import java.util.Set;
import org.reflections.Reflections;

public class CommandHandler extends ListenerAdapter {

    private static final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static final HashMap<String, String> customCommands = new HashMap<>();

    public void onMessageReceived(MessageReceivedEvent e){

        // Ignore Private Messages
        if(e.getMessage().getGuild() == null){
            return;
        }

        String delim = Bot.props.getProperty(PropertyKeys.DELIMITER_KEY);
        if(e.getMessage().getContentRaw().startsWith(delim)){
            String[] args = e.getMessage().getContentRaw().substring(delim.length()).split(" ");
            if(commands.containsKey(args[0])){
                AbstractCommand com = commands.get(args[0]);
                try {
                    boolean checkDisabled = MainSQLHandler.checkDisabledCommand(e.getGuild().getId(), com.getCommand());
                    if(!checkDisabled){
                        com.run(e.getMessage());
                    } else {
                        System.out.println("DISABLED");
                    }
                } catch (CustomAbstractException exp){
                    exp.printStackTrace();
                    e.getMessage().getChannel().sendMessage(exp.getMessage()).queue();
                } catch (Exception exp){
                    exp.printStackTrace();
                    e.getMessage().getChannel().sendMessage("There was an error with that command or its inputs, please try" + com.getUsage(com.getCommand(), com.getInputs())).queue();
                }
            } else if(customCommands.containsKey(args[0])){
                e.getMessage().getChannel().sendMessage(customCommands.get(args[0])).queue();
            }
        }
    }

    public void init(){
        loadCommands();

        //TODO: Custom command initialization?? Cannot have static custom commands with the multi-server setup. Look into this.

        // TEMP CODE
        customCommands.put("meme1", "http://photobucket.com/gallery/user/TheOffice-isms/media/cGF0aDpUaGUgT2ZmaWNlLWlzbXMgbWVtZS1vLW1hdGljL1lPVVRVQkVNSUNIQUVMX3pwc2NwNGF5a3V0LmpwZw==/?ref=");
        // TEMP CODE
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
}
