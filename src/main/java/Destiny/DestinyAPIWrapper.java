package Destiny;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DestinyAPIWrapper {

    private static HashMap<String, Integer> platform = new HashMap<String, Integer>();
    private static JsonParser parser = new JsonParser();



    public static ArrayList<Milestone> getPublicMilestones() throws IOException{
        JsonObject resp = sendRequest("/Destiny2/Milestones/");
        ArrayList<Milestone> milestones = new ArrayList<Milestone>();

        for(Iterator key=resp.keySet().iterator(); key.hasNext();){

            String hash = (String)key.next();
            System.out.println("Milestone Hash : " + hash);

            Milestone ms = new Milestone(resp, hash);
            System.out.println(ms.print());

            if(ms.getName() != null)
                milestones.add(ms);

        }
        return milestones;
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    private static JsonObject sendRequest(String url) throws IOException{
        URL obj;
        obj = new URL(DestinyProperties.API_PATH + url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        // Set header
        con.setRequestProperty("X-API-KEY", DestinyProperties.API_KEY);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to Bungie.Net : "+url);
        System.out.println("Response Code : "+responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        String response = "";

        while((inputLine =in.readLine())!=null) {
            response += inputLine;
        }

        in.close();

        JsonObject json = (JsonObject) parser.parse(response);
        System.out.println("Full Response : " + json);

        return json.getAsJsonObject("Response");
    }

    /**
     *
     * @param type
     * @param hashID
     * @return
     * @throws IOException
     */
    public static JsonObject getDestinyEntityDefinition(String type, String hashID) throws IOException{
        JsonObject resp = sendRequest("/Destiny2/Manifest/" + type + "/" + hashID + "/");

        return resp;
    }

    public static void getClanInfo() throws IOException {
        String req = "/Destiny2/Stats/Leaderboards/Clans/1905682/";

        JsonObject resp = sendRequest(req);
        System.out.println(resp.toString());
    }

    public static void main(String[] args) throws IOException {
        //getPublicMilestones();
        getClanInfo();
    }
}
