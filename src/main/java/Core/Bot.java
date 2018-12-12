package Core;

import Core.EventHandlers.MessageReactionEventHandler;
import Core.EventHandlers.GuildUpdateEventHandler;
import Commands.CommandHandler;
import IPC.IPCHandler;
import JDBC.MainSQLHandler;
import LFG.LFGHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Bot extends ListenerAdapter {

    public static Properties props = new Properties();
    public static String SELF_USER_ID = "";
    public static JDA jda;
    public static Version VERSION = getBuildVersion();
    public static void main(String[] args) {
        System.out.println("Bot Version: "+VERSION);
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);

        // Initialize
        loadProperties();
       jdaBuilder.setToken(props.getProperty(PropertyKeys.BOT_TOKEN_KEY));
        jdaBuilder.setAudioEnabled(false);
        CommandHandler ch = new CommandHandler();
        ch.init();

        // Add listeners
        jdaBuilder.addEventListener(new Bot());
        jdaBuilder.addEventListener(ch);
        jdaBuilder.addEventListener(new GuildUpdateEventHandler());
        jdaBuilder.addEventListener(new MessageReactionEventHandler());
        jdaBuilder.addEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                if(event instanceof ReadyEvent){
                    jda = event.getJDA();
                    jda.getPresence().setGame(Game.playing("Version: "+VERSION.toString()));
                    SELF_USER_ID = event.getJDA().getSelfUser().getId();

                    // The ordering here is important
                    MainSQLHandler.init();
                    LFGHandler.init();
                }
            }
        });

        try{
            jdaBuilder.buildAsync();
        } catch (Exception e){
            System.out.println("Core.Core Exception: " + e.getLocalizedMessage());
        }

        //IPCHandler for API
        IPCHandler.init();
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

    public static Version getBuildVersion() {
        try {
            Enumeration<URL> resources = Bot.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest m = new Manifest(resources.nextElement().openStream());
                Attributes mainAttributes = m.getMainAttributes();
                String mainClass = mainAttributes.getValue("Main-Class");
                if (mainClass != null && mainClass.equals("Core.Bot")) {
                    String buildVersion = mainAttributes.getValue("Bot-Version");
                    int major = Integer.parseInt(buildVersion.split("\\.")[0]);
                    int minor = Integer.parseInt(buildVersion.split("\\.")[1]);
                    int patch = Integer.parseInt(buildVersion.split("\\.")[2]);
                    return new Version(major, minor, patch);
                }
            }
        } catch (IOException ex) {
            System.out.println("Unable to load MANIFEST.MF!");
            return null;
        }

        return null;
    }

}
