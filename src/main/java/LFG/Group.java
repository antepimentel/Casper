package LFG;

import Exceptions.GroupIsEmptyException;
import Exceptions.MemberNotFoundException;
import Exceptions.NoAvailableSpotsException;
import net.dv8tion.jda.core.entities.Member;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * LFG Group data object
 */
public class Group {

    private static final int MAX_GROUP_SIZE = 6;
    private static final int MAX_SUBS = 2;

    private String serverID;
    private int ID;
    private String name;
    private Date dateCreated;
    private Date date;
    private String time;
    private String timezone;

    public static DateFormat df = new SimpleDateFormat("MM/dd hh:mmaa zzz yyyy");

    private ArrayList<Member> players = new ArrayList<Member>();
    private ArrayList<Member> subs = new ArrayList<Member>();

    public Group(String serverID, int id, String name, String date, String time, String timezone, Member m) throws ParseException {
        ID = id;
        this.name = name;
        this.serverID = serverID;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        this.df.setTimeZone(TimeZone.getTimeZone(timezone));
        this.date = df.parse(date +" "+ time +" "+ timezone + " " + year);
        this.dateCreated = new Date();
        //this.time = time;
        //this.timezone = timezone;
        players.add(m);
    }

    public Group(String serverID, int id, String name, Date date) throws ParseException {
        ID = id;
        this.name = name;
        this.serverID = serverID;
        this.date = date;
        this.dateCreated = new Date();
        //this.time = time;
        //this.timezone = timezone;
    }

    public void join(Member m) throws NoAvailableSpotsException {
        if(players.size() < MAX_GROUP_SIZE){
            players.add(m);
        } else {
            throw new NoAvailableSpotsException(m, ID);
        }
    }

    public void joinAsSub(Member m) throws NoAvailableSpotsException{
        if(subs.size() < MAX_SUBS){
            subs.add(m);
        } else {
            throw new NoAvailableSpotsException(m, ID);
        }
    }

    public void removePlayer(Member m) throws MemberNotFoundException, GroupIsEmptyException {
        if(players.contains(m)){
            players.remove(m);
        } else if(subs.contains(m)){
            subs.remove(m);
        } else {
            throw new MemberNotFoundException(ID, m);
        }

        if(players.isEmpty() && subs.isEmpty()){
            throw new GroupIsEmptyException(ID);
        }
    }

    public String toString(){
        String result = "**"+ ID + " - " + name + "**\n"
                + "Joined: **" + players.size() + "** : Subs: **" + subs.size() + "**\n"
                + "Date: **" + df.format(date) + "**";

        return result;
    }

    public String toStringFull(){
        String line = "=================================\n";
        String temp = line
                + ID + " : " + name
                + "\n" + line
                + df.format(date)
                + "\n" + line + "Players:\n";

        for(int i = 0; i < players.size(); i++){
            temp = temp + (i+1) + ": " + players.get(i).getEffectiveName() + "\n";
        }

        if(subs.size() > 0){
            temp = temp + "Substitutes:\n";
            for(int i = 0; i < subs.size(); i++){
                temp = temp + (i+1) + ": " + subs.get(i).getEffectiveName() + "\n";
            }
        }

        return "```"+temp+"```";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date, String time, String timezone) throws ParseException {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        this.df.setTimeZone(TimeZone.getTimeZone(timezone));
        this.date = df.parse(date + " " + time + " " + timezone + " " + year);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public ArrayList<Member> getSubs() {
        return subs;
    }

    public ArrayList<Member> getPlayers() {
        return players;
    }

    public String getServerID(){
        return serverID;
    }

    public String getPlayerIDString(){
        String result = "";
        for(int i = 0; i < players.size(); i++){
            result = result + players.get(i).getUser().getId();
            if(i+1 != players.size()){
                result = result + "#";
            }
        }
        return result;
    }

    public String getSubIDString(){
        String result = "";
        for(int i = 0; i < subs.size(); i++){
            result = result + subs.get(i).getUser().getId();
            if(i+1 != subs.size()){
                result = result + "#";
            }
        }
        return result;
    }
}
