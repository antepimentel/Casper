package Commands.Destiny;

import Commands.AbstractCommand;
import Commands.CommandCategory;
import Core.Bot;
import Core.PermissionHandler;
import Destiny.DestinyAPIWrapper;
import Destiny.DestinyProperties;
import Exceptions.*;
import JDBC.AutoAssignmentSQL;
import JDBC.EventBoardSQL;
import JDBC.GroupSQL;
import JDBC.MainSQLHandler;
import LFG.Group;
import LFG.LFGHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import org.apache.commons.collections4.map.HashedMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Core.Bot.jda;

public class Link extends AbstractCommand {
    public class reactionHandler implements net.dv8tion.jda.core.hooks.EventListener {
        public Message message;
        public String ownerId;
        public JsonObject[] accounts;
        public int selectedAccount = 0;

        public reactionHandler(Message message, String ownerId, JsonObject[] accounts, int selectedAccount) {
            this.message = message;
            this.ownerId = ownerId;
            this.accounts = accounts;
            this.selectedAccount = selectedAccount;
        }

        @Override
        public void onEvent(Event e){
            try {
                // Ignore reactions from self
                String id = "";
                if(e instanceof GenericMessageReactionEvent){
                    id = ((GenericMessageReactionEvent) e).getUser().getId();
                }

                if(!id.equals(Bot.SELF_USER_ID) && id.equals(this.ownerId)) {
                    if(e instanceof MessageReactionAddEvent){
                        onMessageReactionAddEvent((MessageReactionAddEvent) e);
                    }
                }
            } catch (Exception err){
                System.out.println(err.toString());
                err.printStackTrace();
            }
        }

        public void onMessageReactionAddEvent(MessageReactionAddEvent e){
            boolean repost = false;
            String name = message.getEmbeds().get(0).getTitle().split("Search results for ")[1].split(":")[0];
            String platform = message.getEmbeds().get(0).getTitle().split("Search results for ")[1].split(":")[1];
            String emojiName = e.getReactionEmote().getName();
            if(emojiName.equals("✅")) {
                JsonObject account = accounts[selectedAccount];

                JsonElement character = account.getAsJsonObject("characters").getAsJsonObject("data").entrySet().iterator().next().getValue();
                String membershipId = character.getAsJsonObject().getAsJsonPrimitive("membershipId").getAsString();


                int platformCode = -1;
                if(platform.equals("ps4")) {
                    platformCode = DestinyProperties.PLAT_PS4;
                } else if(platform.equals("pc")) {
                    platformCode = DestinyProperties.PLAT_PC;
                } else if(platform.equals("xbox")) {
                    platformCode = DestinyProperties.PLAT_XBOX;
                }

                if(MainSQLHandler.queryLinker(ownerId) != null) {
                    MainSQLHandler.dropLinker(ownerId);
                    message.getChannel().sendMessage("Removed old link from "+e.getMember().getUser().getName()).queue();
                }

                MainSQLHandler.addLinker(ownerId, membershipId, platformCode);



                message.getChannel().sendMessage("Linked "+e.getMember().getUser().getName() + " to " + membershipId + "(" + platform+")").queue();
                message.delete().queue();
                jda.removeEventListener(this);

            } else if(emojiName.equals("⬆") ) {
                if(selectedAccount > 0) selectedAccount--;
                repost = true;
            } else if(emojiName.equals("⬇")) {
                if(selectedAccount < accounts.length) selectedAccount++;
                repost = true;
            } else if(emojiName.equals("❎")) {
                message.delete().queue();
                jda.removeEventListener(this);
            }

            if(repost) {
                EmbedBuilder eb = createEmbedBuilder(name, platform, accounts, selectedAccount);
                message.editMessage(eb.build()).queue();
            }
        }
    }

    //private Map<String, Message> messageList = new HashedMap<>();

    private static String command = "link";
    private static String desc = "Link a destiny account to your discord account.";
    private static String[] inputs = {"Destiny Username", "platform"};

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
    public void run(Message msg) throws NoArgumentsGivenException, IOException {
        String[] args = getInputArgs(msg);
        String name = args[0];
        String platformString = ( args.length == 1) ? "" : args[1].toLowerCase();
        System.out.println("Searching for "+platformString+":"+name);

        int platform = -1;
        if(platformString.equals("ps4")) {
            platform = DestinyProperties.PLAT_PS4;
        } else if(platformString.equals("pc")) {
            platform = DestinyProperties.PLAT_PC;
        } else if(platformString.equals("xbox")) {
            platform = DestinyProperties.PLAT_XBOX;
        } else {
            msg.getChannel().sendMessage("Invalid platform: "+platformString);
            return;
        }

        if(platform == DestinyProperties.PLAT_PC) name = name.replace("#", "%23");

        JsonObject response = DestinyAPIWrapper.searchDestinyPlayer(platform, name);
        JsonArray results = response.getAsJsonArray("Response");

        JsonObject[] profiles = new JsonObject[results.size()];

        for(int i = 0; i < results.size();  i++) {
            JsonElement result = results.get(i);
            String membershipId = result.getAsJsonObject().getAsJsonPrimitive("membershipId").getAsString();
            int membershipType = result.getAsJsonObject().getAsJsonPrimitive("membershipType").getAsInt();
            JsonObject profile = DestinyAPIWrapper.getDestinyProfile(membershipType, membershipId, "200");

            profiles[i] = profile;

            JsonObject characters = profile.get("characters").getAsJsonObject().get("data").getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : characters.entrySet()) {
                JsonObject character = entry.getValue().getAsJsonObject();
            }
        }

        /*
        Debugging for search paramaters that only return 1 result

        JsonObject[] testProfiles = new JsonObject[]{profiles[0], profiles[0]};
        profiles = testProfiles;
        */

        EmbedBuilder eb = createEmbedBuilder(name, platformString, profiles, 0);
        Message m = msg.getChannel().sendMessage(eb.build()).complete();
        m.addReaction("✅").complete();
        m.addReaction("⬆").complete();
        m.addReaction("⬇").complete();
        m.addReaction("❎").complete();

        jda.addEventListener(new reactionHandler(m, msg.getMember().getUser().getId(), profiles, 0));
    }

    private EmbedBuilder createEmbedBuilder(String name, String platformString, JsonObject[] profiles, int selectedIndex) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Search results for "+name + ":"+platformString);

        embedBuilder.setDescription("I found the following accounts for " + name + ", press ✅ if the selected account is correct, or use the arrow reactions to select another one.");
        String profilesText = "";
        for(int i = 0; i < profiles.length ; i++) {
            JsonObject profile = profiles[i];
            if(i == selectedIndex) profilesText += "> ";
            profilesText += name + " : " + createCharacterString(profile) + "\n";
        }

        embedBuilder.addField("Profile(s)", profilesText, false);
        return embedBuilder;
    }

    private String createCharacterString(JsonObject profile) {
        String result = "[";

        JsonObject characters = profile.get("characters").getAsJsonObject().get("data").getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : characters.entrySet()) {
            String classType = "";
            int powerLevel = 0;

            JsonObject character = entry.getValue().getAsJsonObject();

            int classCode = character.getAsJsonPrimitive("classType").getAsInt();

            if(classCode == 0) {
                classType = "T";
            } else if(classCode == 1) {
                classType = "H";
            } else if(classCode == 2) {
                classType = "W";
            }

            powerLevel = character.getAsJsonPrimitive("light").getAsInt();
            result += " "+classType+":"+powerLevel;
        }
        result += " ]";
        return result;
    }
}
