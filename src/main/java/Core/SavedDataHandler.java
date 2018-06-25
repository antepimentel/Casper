package Core;

import LFG.LFGHandler;
import com.google.gson.Gson;
//import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Need to save:
 * Custom Commands
 * Active LFG Groups
 * Enabled/Disabled Commands
 *
 */

public class SavedDataHandler {

    private static String LFG_GROUPS = "lfg_groups";
    private static String DESTINY_ACCOUNTS = "destiny_accounts";

    //private static XStream xstream = new XStream();

    public static void init(){
        File dst = new File(PropertyKeys.SAVED_DATA_PATH);
        if(!dst.exists()){
            dst.mkdir();
        }

        //xstream.alias("group", Group.class);
    }

    public static void saveData() throws FileNotFoundException {
//        File dst = new File(PropertyKeys.SAVED_DATA_PATH+LFG_GROUPS);
//        PrintStream writer = new PrintStream(dst);
//
//        LFG.Group temp = LFGHandler.getGroups().get(0);
//        //String xml = xstream.toXML(temp.getName());
//
//        Gson gson = new Gson();
//        String json = gson.toJson(LFGHandler.getGroups());
//
//        writer.print(json);
//        System.out.println("Saved to: " + dst.getAbsoluteFile());
//        writer.close();
    }

    public static void loadData() throws FileNotFoundException {
        //Scanner reader = new Scanner(new File(PropertyKeys.SAVED_DATA_PATH+LFG_GROUPS));
        //ArrayList lfgGroups = (ArrayList) xstream.fromXML(new File(PropertyKeys.SAVED_DATA_PATH+LFG_GROUPS));
        //LFGHandler.setGroups(lfgGroups);
    }

    public static void backup(){

    }

    public static void restore(){

    }
}
