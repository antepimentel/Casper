package LFG;

import Core.Bot;
import Exceptions.GroupIsEmptyException;
import Exceptions.MemberNotFoundException;
import Exceptions.NoAvailableSpotsException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
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

    public static ArrayList<Platform> PLATFORMS = new ArrayList<Platform>();

    private static final int MAX_GROUP_SIZE = 6;
    private static final int MAX_SUBS = 2;

    private String serverID;
    private int ID;
    private String name;
    private Date dateCreated;
    private Date date;
    private String time;
    private String timezone;
    private String platform;
    private String msgID;
    private Member owner;
    private boolean empty;

    public static DateFormat df = new SimpleDateFormat("MM/dd hh:mmaa zzz yyyy");

    private ArrayList<Member> players = new ArrayList<Member>();
    private ArrayList<Member> subs = new ArrayList<Member>();

    /**
     *
     *
     * @param serverID
     * @param id
     * @param name
     * @param date
     * @param time
     * @param timezone
     * @param m
     * @throws ParseException
     */
    public Group(String serverID, int id, String name, String date, String time, String timezone, Member m, String platform) throws ParseException {
        ID = id;
        this.name = name;
        this.serverID = serverID;
        int year = Calendar.getInstance().get(Calendar.YEAR);
        this.df.setTimeZone(TimeZone.getTimeZone(timezone));
        this.date = df.parse(date +" "+ time +" "+ timezone + " " + year);
        this.dateCreated = new Date();
        this.owner = m;
        //this.time = time;
        this.timezone = timezone;
        players.add(m);
        this.empty = false;
        this.platform = platform;
    }

    /**
     * This is used when grabbing groups from the database
     *
     * @param serverID
     * @param id
     * @param name
     * @param date
     * @throws ParseException
     */
    public Group(String serverID, int id, String name, Date date, String platform, Member owner, String msgID) throws ParseException {
        ID = id;
        this.name = name;
        this.serverID = serverID;
        this.date = date;
        this.dateCreated = new Date();
        //this.time = time;
        //this.timezone = timezone;
        this.platform = platform;
        this.msgID = msgID;
        this.owner = owner;
    }

    public void join(Member m) throws NoAvailableSpotsException {
        if(players.size() < MAX_GROUP_SIZE){
            players.add(m);
        } else {
            joinAsSub(m);
        }
    }

    public void joinAsSub(Member m) throws NoAvailableSpotsException{
        if(subs.size() < MAX_SUBS){
            subs.add(m);
        } else {
            throw new NoAvailableSpotsException(m, ID);
        }
    }

    public String getPlayersAsMention(){
        String response = "";
        for(int i = 0; i < getPlayers().size(); i++){
            response = response + getPlayers().get(i).getAsMention();
        }

        for(int i = 0; i < getSubs().size(); i++){
            response = response + getSubs().get(i).getAsMention();
        }
        return response + " Roll call for group: " + getID();
    }

    public void removePlayer(Member m) throws MemberNotFoundException, GroupIsEmptyException {
        if(subs.contains(m)){
            subs.remove(m);
        } else if(players.contains(m)){
            players.remove(m);
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

    public MessageEmbed toEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(name);
        eb.addField("ID", Integer.toString(ID), false);
        eb.addField("Start Date & Time", df.format(date), false);

        Platform platform = null;
        for(Platform p : PLATFORMS) {
            if(p.getName().equals(this.platform)) {
                platform = p;
            }
        }

        String playerText = (empty) ?
                "This group is empty, use the " + Bot.jda.getEmotesByName("plus", false).get(0).getAsMention() + " reaction to join it." :
                "" ;
        for(int i = 0; i < players.size(); i++){
            playerText = playerText + (i+1) + ". " + players.get(i).getEffectiveName() + "\n";
        }

        eb.addField("Players", playerText, false);

        if(subs.size() != 0) {
            String subText = "";
            if(subs.size() > 0){
                for(int i = 0; i < subs.size(); i++){
                    subText = subText + (players.size() + i +1) + ". " + subs.get(i).getEffectiveName() + "\n";
                }
            }

            eb.addField("Subs", subText, false);
        }
        String footerText = "Group Creator: "+owner.getEffectiveName() + ".";
        if(players.size() == MAX_GROUP_SIZE && subs.size() == MAX_SUBS) {
            footerText += "\n   (This group is full)";
        }

        eb.setFooter(footerText, owner.getUser().getAvatarUrl());
        eb.setAuthor("⠀", null, platform.getEmbedIconUrl()); // uses U+2800 for name.
        eb.setColor(platform.getEmbedColor());

        return eb.build();
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

    public String getType() {
        return platform;
    }

    public String getOwnerID() { return owner.getUser().getId(); }

    public void setOwner(Member m) { this.owner = m; }

    public boolean getEmpty() { return empty; }
    public void setEmpty(boolean empty ) { this.empty = empty; }

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

    public static void setPlatforms(){
        PLATFORMS.add(new Platform("ps4", 0x00AE86, "https://i0.wp.com/freepngimages.com/wp-content/uploads/2014/05/playstation_logo_2.png?w=220"));
        PLATFORMS.add(new Platform("pc", 0x00AE86, "https://png2.kisspng.com/sh/1558f63709413cf22c2b32bf9a0b8679/L0KzQYm3VcE3N5h3iZH0aYP2gLBuTfJifKVxfZ93ZYSwh7F5jPQud5cyj9N7Y4LkdsW0jCZmeqhmjNVxLXPyfcH8lPVzNZpoRadqZnOzRLa6gvNkOJQ5RqM9NEO2RoWAUcUzPmU7TakBM0e6Q4K1kP5o/kisspng-battle-net-world-of-warcraft-overwatch-computer-ic-5afc04e3bcc0c4.1443364715264657637731.png"));
        PLATFORMS.add(new Platform("xbox", 0x00AE86, "http://icons.iconarchive.com/icons/dakirby309/simply-styled/256/Xbox-icon.png"));
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }
}
