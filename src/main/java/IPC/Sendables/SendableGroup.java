package IPC.Sendables;

import LFG.GroupActivityType;
import LFG.Platform;
import net.dv8tion.jda.core.entities.Member;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SendableGroup {public static ArrayList<Platform> PLATFORMS = new ArrayList<Platform>();
    private String serverID;
    private int ID;
    private String name;
    private Date dateCreated;
    private Date date;
    private String time;
    private String timezone;
    private String platform;
    private String msgID;
    private String ownerID;
    private GroupActivityType groupActivityType;
    private int rollcallCount;
    private ArrayList<String> players = new ArrayList<String>();
    private ArrayList<String> subs = new ArrayList<String>();

    public SendableGroup(String serverID, int ID, String name, Date dateCreated,
                         Date date, String time, String timezone, String platform,
                         String msgID, String ownerID, GroupActivityType activityType,
                         int rollcallCount, ArrayList<String> players, ArrayList<String> sub)
    {
        this.serverID = serverID;
        this.ID = ID;
        this.name = name;
        this.dateCreated = dateCreated;
        this.date = date;
        this.time = time;
        this.timezone = timezone;
        this.platform = platform;
        this.msgID = msgID;
        this.ownerID = ownerID;
        this.groupActivityType = activityType;
        this.rollcallCount = rollcallCount;
        this.players = players;
        this.subs = subs;
    }
}
