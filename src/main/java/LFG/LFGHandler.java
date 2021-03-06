package LFG;

import Core.Bot;
import Core.EventHandlers.MessageReactionEventHandler;
import Core.PropertyKeys;
import Exceptions.*;
import JDBC.GroupSQL;
import JDBC.EventBoardSQL;
import JDBC.MainSQLHandler;
import net.dv8tion.jda.core.entities.*;
import org.omg.CORBA.TIMEOUT;

import javax.xml.soap.Text;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Static class for handling all things LFG
 * Model should be modified by only this class if possible
 */
public class LFGHandler {
    private final static ScheduledExecutorService lfgPingScheduler = Executors.newScheduledThreadPool(1);
    private final static ScheduledExecutorService lfgDelScheduler = Executors.newScheduledThreadPool(1);
    private static ArrayList<Group> deletionQueue = new ArrayList<Group>();
    private static ArrayList<Integer> pingedIds = new ArrayList<Integer>();
    private static Date lastCheck = new Date();

    //runnable for automatic group pings
    private  static Runnable checkGroupsForPing = new Runnable() {
        @Override
        public void run() {
            List<Guild> guilds = Bot.jda.getGuilds();
            for(Guild guild : guilds) {
                List<Group> groups = GroupSQL.getGroupsByServer(guild.getId());
                for (Group g : groups) {
                    long diff = LFGHandler.getDateDiff(new Date(), g.getDate(), TimeUnit.MINUTES);

                    if(diff >= 0 && diff < 5 && pingedIds.indexOf(g.getID()) == -1) {
                        pingPlayers(g);
                        pingedIds.add(g.getID());
                    }
                }
            }
        }
    };

    //runnable for automatic group deletion
    private static Runnable checkGroups = new Runnable() {
        @Override
        public void run() {
            List<Guild> guilds = Bot.jda.getGuilds();

            for(Guild guild : guilds) {
                for(Group g : deletionQueue) {
                    try {
                        // Putting the if statement avoids confusing output to console about boards not existing
                        if(guild.getId().equals(g.getServerID())){
                            TextChannel board = EventBoardSQL.getEventBoard(guild.getId(), g.getType());
                            Message groupMessage = board.getMessageById(g.getMsgID()).complete();
                            groupMessage.delete().queue();

                            System.out.println("DELETED: " + g.getID());
                            GroupSQL.delete(g);
                        }

                    } catch (NoBoardForPlatformException ex){
                        System.out.println(ex.getMessage());
                    }
                }

                List<Group> groups = GroupSQL.getGroupsByServer(guild.getId());
                for(Group g : groups) {
                    long diff = LFGHandler.getDateDiff(new Date(), g.getDate(), TimeUnit.MINUTES);
                    if (diff < 0) {
                        System.out.println("Adding Group "+g.getID()+" to the deletion queue.");
                        PrivateChannel dmChannel = g.getOwner().getUser().openPrivateChannel().complete();
                        dmChannel.sendMessage("Your Group "+g.getName() + " has started and so has been flagged for deletion in the next 12 hours! To keep your group around for longer, type "+Bot.props.getProperty(PropertyKeys.DELIMITER_KEY)+"keepgroup "+g.getID() + " in #rasputin-commands").queue();
                        deletionQueue.add(g);
                    }
                }
            }

            lastCheck = new Date();
        }
    };

    public static void init(){
        Group.setGroupTypes();
        Group.setPlatforms();

        lfgDelScheduler.scheduleAtFixedRate(checkGroups, 0, 12, TimeUnit.HOURS);// PROD
        lfgPingScheduler.scheduleAtFixedRate(checkGroupsForPing, 0, 1, TimeUnit.MINUTES);// PROD
        //lfgScheduler.scheduleAtFixedRate(checkGroups, 0, 20, TimeUnit.SECONDS); // DEBUG

    }

    public static Group post(String serverID, String name, String date, String time, String timezone, Member poster, String platform, String year) throws ParseException, NoBoardForPlatformException, NameTooLongException {
        Group g = new Group(
                serverID,
          getFreeGroupID(serverID),
                name,
                date,
                time,
                timezone,
                poster,
                platform,
                year
        );

        String msgID = MessageReactionEventHandler.postEventGroup(g);
        g.setMsgID(msgID);
        GroupSQL.save(g);
        return g;
    }

    public static void repostAndUpdateMsgID(Group g) throws NoBoardForPlatformException {
        String msgID = MessageReactionEventHandler.postEventGroup(g);
        g.setMsgID(msgID);
        GroupSQL.updateMessageID(g);
    }

    public static void join(String serverID, int ID, Member m) throws GroupNotFoundException, NoAvailableSpotsException {
        Group g = findGroupByID(serverID, ID);
        g.join(m);
        GroupSQL.updatePlayers(g);
        if(g.getEmpty()) g.setEmpty(false);
    }

    public static void join(Group g, Member m) throws GroupNotFoundException, NoAvailableSpotsException {
        g.join(m);
        GroupSQL.updatePlayers(g);
        if(g.getEmpty()) g.setEmpty(false);

    }

    public static void joinAsSub(String serverID, int ID, Member m) throws GroupNotFoundException, NoAvailableSpotsException {
        Group g = findGroupByID(serverID, ID);
        g.joinAsSub(m);
        GroupSQL.updatePlayers(g);
    }

    public static void leave(String serverID, int ID, Member m) throws GroupNotFoundException, MemberNotFoundException {
        Group g = findGroupByID(serverID, ID);
        try{
            g.removePlayer(m);
            GroupSQL.updatePlayers(g);

            if(g.getEmpty()) g.setEmpty(false);
        } catch (GroupIsEmptyException e){
            if(!g.getEmpty())  {
                GroupSQL.updatePlayers(g);
                g.setEmpty(true);
            }
        }
    }

    public static void leave(Group g, Member m) throws GroupNotFoundException, MemberNotFoundException {
        try{
            g.removePlayer(m);
            GroupSQL.updatePlayers(g);

            if(g.getEmpty()) g.setEmpty(false);
        } catch (GroupIsEmptyException e){
            if(!g.getEmpty())  {
                GroupSQL.updatePlayers(g);
                g.setEmpty(true);
            }
        }
    }

    public static void refreshGroup(String ServerID, Group g) {
        String platform = g.getPlatform();
        try {
            TextChannel tc = EventBoardSQL.getEventBoard(ServerID, platform);
            Message message = tc.getMessageById(g.getMsgID()).complete();
            tc.getMessageById(message.getId()).complete().editMessage(g.toEmbed()).queue();

        } catch (NoBoardForPlatformException ex) {

        }

    }

    private static void sortGroupsByID(ArrayList<Group> groupsToSort){
        Collections.sort(groupsToSort, new GroupComparator());
    }

    //TODO: Only ping each user once
    public static void pingPlayers(Group g){
        String message = "Roll call for: " + g.getName();
        ArrayList<Member> toPing = g.getPlayers();
        toPing.addAll(g.getSubs());
        ArrayList<Member> pinged = new ArrayList<Member>();

        for(Member m: toPing) {
            if(pinged.indexOf(m) == -1) {
                PrivateChannel pc = m.getUser().openPrivateChannel().complete();
                pc.sendMessage(message).queue();
                pinged.add(m);
            }

        }

        g.setRollcallCount(g.getRollcallCount() + 1);
        GroupSQL.updateRollcallCount(g);
    }

    /**
     * For use when we're not sure if group ID is valid
     * @param ID
     * @return
     * @throws GroupNotFoundException
     */
    public static Group findGroupByID(String serverID, int ID) throws GroupNotFoundException {
        Group ans = null;
        ArrayList<Group> groups = GroupSQL.getGroupsByServer(serverID);
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).getID() == ID && groups.get(i).getServerID().equals(serverID)){
                ans = groups.get(i);
            }
        }
        if(ans == null){
            throw new GroupNotFoundException(ID);
        } else {
            return ans;
        }
    }

//    public static Group getGroupByID(int ID) {
//        Group ans = null;
//        for(int i = 0; i < groups.size(); i++){
//            if(groups.get(i).getID() == ID){
//                ans = groups.get(i);
//            }
//        }
//        return ans;
//    }



    public static String parseDiff(long totalMinutes){

        if(totalMinutes < 0){
            return "EXPIRED";
        }

        String result = "";

        int totalHours = (int)totalMinutes/60;
        int mins = (int)totalMinutes%60;
        int days = totalHours/24;
        int hours = totalHours%24;

        String sMins = "";
        String sDays = "";
        String sHours = "";

        if(days > 1){
            sDays = days + " Days";
        } else if(days == 1){
            sDays = days + " Day";
        }

        if(hours > 1){
            sHours = hours + " Hours";
        } else if(hours == 1){
            sHours = hours + " Hour";
        }

        if(mins > 1){
            sMins = mins + " Minutes";
        } else if(hours == 1){
            sMins = mins + " Minute";
        }

        result = sDays;

        if(!sDays.equals("") && !sHours.equals("")){
            result = result + ", " + sHours;
        } else if(!sHours.equals("")){
            result = sHours;
        }

        if(!result.equals("") && !sMins.equals("")){
            result = result + ", " + sMins;
        } else if (!sMins.equals("")){
            result = sMins;
        }

        return result;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        long result = timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
        return result;
    }

    public static ArrayList<Group> getGroupsByServer(String serverID){
//        ArrayList<Group> result = new ArrayList<Group>();
//        for(int i = 0; i < groups.size(); i++){
//            if(groups.get(i).getServerID().equals(serverID)){
//                result.add(groups.get(i));
//            }
//        }
//        return result;
        return GroupSQL.getGroupsByServer(serverID);
    }

    public static ArrayList<Group> getDeletionQueue() { return deletionQueue; }
    public static void removeFromDeletionQueue(Group g) {
        ArrayList<Group> newDeletionQueue = deletionQueue;
        for(int i = 0; i < deletionQueue.size(); i++) {
            Group dg = deletionQueue.get(i);
            if(dg.getID() == g.getID()) {
                deletionQueue.remove(i);
            }
        }
    }

    public static void removeIdFromPinged(int id) {
        if(pingedIds.indexOf(id) != -1) pingedIds.remove(id);
    }

    //Not sure this is needed but adding it just incase
    public static void addIdToPinged(int id) {
        if(pingedIds.indexOf(id) != -1) pingedIds.add(id);
    }
    public static Date getLastCheck() { return lastCheck; }

    public static ArrayList<Group> getGroupsByMember(Member m) throws GroupNotFoundException {
        ArrayList<Group> result = new ArrayList<Group>();
        ArrayList<Group> serverGroups = getGroupsByServer(m.getGuild().getId());
        for(int i = 0; i < serverGroups.size(); i++){
            if(serverGroups.get(i).getPlayers().contains(m)
                    || serverGroups.get(i).getSubs().contains(m)){
                result.add(serverGroups.get(i));
            }
        }

        if(result.size() < 1){
            throw new GroupNotFoundException(m);
        }
        return result;
    }

    private static int getFreeGroupID(String serverID){
        int ans = -1;

        ArrayList<Group> serverGroups = getGroupsByServer(serverID);
        sortGroupsByID(serverGroups);

        for(int i = 0; i < serverGroups.size(); i++){
            if(i+1 < serverGroups.get(i).getID()){
                ans = i+1;
                return ans;
            }
        }

        if(ans == -1){
            ans = serverGroups.size()+1;
        }
        return ans;
    }
}
