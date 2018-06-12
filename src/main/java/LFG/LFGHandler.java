package LFG;

import Core.SavedDataHandler;
import Exceptions.GroupIsEmptyException;
import Exceptions.GroupNotFoundException;
import Exceptions.MemberNotFoundException;
import Exceptions.NoAvailableSpotsException;
import net.dv8tion.jda.core.entities.Member;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Static class for handling all things LFG
 * Model should be modified by only this class if possible
 */
public class LFGHandler {

    private static ArrayList<Group> groups = new ArrayList<Group>();

    public static Group post(String name, String date, String time, String timezone, Member poster) throws ParseException {
        Group g = new Group(
          getFreeGroupID(),
                name,
                date,
                time,
                timezone,
                poster
        );
        groups.add(g);
        sortGroupsByID();

        save();
        return g;
    }

    public static void join(int ID, Member m) throws GroupNotFoundException, NoAvailableSpotsException {
        Group g = findGroupByID(ID);
        g.join(m);
    }

    public static void joinAsSub(int ID, Member m) throws GroupNotFoundException, NoAvailableSpotsException {
        Group g = findGroupByID(ID);
        g.joinAsSub(m);
    }

    public static void leave(int ID, Member m) throws GroupNotFoundException, MemberNotFoundException, GroupIsEmptyException {
        Group g = findGroupByID(ID);
        g.removePlayer(m);
    }

    public static ArrayList<Group> getGroups() {
        return groups;
    }

    public static void setGroups(ArrayList newGroups){
        groups = newGroups;
    }

    private static void sortGroupsByID(){
        Collections.sort(groups, new GroupComparator());
    }

    /**
     * For use when we're not sure if group ID is valid
     * @param ID
     * @return
     * @throws GroupNotFoundException
     */
    public static Group findGroupByID(int ID) throws GroupNotFoundException {
        Group ans = null;
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).getID() == ID){
                ans = groups.get(i);
            }
        }
        if(ans == null){
            throw new GroupNotFoundException(ID);
        } else {
            return ans;
        }
    }

    /**
     * USE WITH CARE, MAY RETURN NULL
     * @param ID
     * @return
     */
    public static Group getGroupByID(int ID) {
        Group ans = null;
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).getID() == ID){
                ans = groups.get(i);
            }
        }
        return ans;
    }

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
        System.out.println("Diff: " + result);
        return result;
    }

    public static ArrayList<Group> getGroupsByMember(Member m) throws GroupNotFoundException {
        ArrayList<Group> result = new ArrayList<Group>();
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).getPlayers().contains(m)
                    || groups.get(i).getSubs().contains(m)){
                result.add(groups.get(i));
            }
        }

        if(result.size() < 1){
            throw new GroupNotFoundException(m);
        }
        return result;
    }

    private static int getFreeGroupID(){
        int ans = -1;
        sortGroupsByID();

        for(int i = 0; i < groups.size(); i++){
            if(i+1 < groups.get(i).getID()){
                ans = i+1;
                return ans;
            }
        }

        if(ans == -1){
            ans = groups.size()+1;
        }
        return ans;
    }

    private static void save(){
        try {
            SavedDataHandler.saveData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
