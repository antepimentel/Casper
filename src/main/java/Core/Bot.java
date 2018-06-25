package Core;

import Core.EventHandlers.AutoAssignmentEventHandler;
import Core.EventHandlers.GuildUpdateEventHandler;
import Commands.CommandHandler;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
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
    public static JDA jda;

    public static void main(String[] args) {
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);

        // Initialize
        loadProperties();
        jdaBuilder.setToken(props.getProperty(PropertyKeys.BOT_TOKEN_KEY));
        jdaBuilder.setAudioEnabled(false);
        CommandHandler ch = new CommandHandler();
        ch.init();

        // Load Saved Data, Load data from DB
        // TODO
        MainSQLHandler.init();

        // Add listeners
        jdaBuilder.addEventListener(new Bot());
        jdaBuilder.addEventListener(ch);
        jdaBuilder.addEventListener(new GuildUpdateEventHandler());
        jdaBuilder.addEventListener(new AutoAssignmentEventHandler());
        jdaBuilder.addEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                if(event instanceof ReadyEvent){
                    jda = event.getJDA();
                    AutoAssignmentEventHandler.load(jda);
                    SELF_USER_ID = event.getJDA().getSelfUser().getId();
                }
            }
        });

        try{
            jdaBuilder.buildAsync();
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
