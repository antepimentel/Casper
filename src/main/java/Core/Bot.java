package Core;

import Core.EventHandlers.AutoAssignmentEventHandler;
import Core.EventHandlers.GuildUpdateEventHandler;
import Commands.CommandHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Bot extends ListenerAdapter {

    public static Properties props = new Properties();
    public static String SELF_USER_ID = "";

    public static void main(String[] args) {
        JDABuilder jda = new JDABuilder(AccountType.BOT);

        // Initialize
        loadProperties();
        jda.setToken(props.getProperty(PropertyKeys.BOT_TOKEN_KEY));
        jda.setAudioEnabled(false);
        CommandHandler ch = new CommandHandler();
        ch.init();

        // Load Saved Data
        // TODO
        //SavedDataHandler.init();

        // Add listeners
        jda.addEventListener(new Bot());
        jda.addEventListener(ch);
        jda.addEventListener(new GuildUpdateEventHandler());
        jda.addEventListener(new AutoAssignmentEventHandler());
        jda.addEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                if(event instanceof ReadyEvent){
                    AutoAssignmentEventHandler.load(event.getJDA());
                    SELF_USER_ID = event.getJDA().getSelfUser().getId();
                }
            }
        });

        try{
            jda.buildAsync();
        } catch (Exception e){
            System.out.println("Core.Core Exception: " + e.getLocalizedMessage());
        }


    }

    private static void loadProperties(){
        try {

            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));

            String propLoc = "bot.properties";
            System.out.println("Checking for properties file in: " + propLoc);
            props.load(new FileInputStream(propLoc));

        } catch (IOException e) {
            System.out.println("Properties file not found");
            e.printStackTrace();
        }
    }

}
