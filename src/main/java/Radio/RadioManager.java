package Radio;

import Exceptions.RadioAPIException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RadioManager {
    private final String PUBLIC_API_URL = "https://public.radio.co/stations/";
    private String stationId;

    private ArrayList<RunnableListener> listeners = new ArrayList<>();
    private ArrayList<Pair<Runnable, ScheduledExecutorService>> scheduledExecutorServices = new ArrayList<>();

    public static interface RunnableListener {
        void runnableFired(Object data, Exception ex) throws Exception;
    }

    public final Runnable SONG_CHANGED_RUNNABLE = new Runnable() {
        RadioStatus lastStatus = new RadioStatus();

        @Override
        public void run() {
            Exception err = null;
            RadioStatus statusToSend = new RadioStatus();
            try {
                statusToSend = getStatus();
            } catch (Exception ex) {
                err = ex;
            }

            if(!lastStatus.current_track.title.equals(statusToSend.current_track.title)) {
                fireRunnable(statusToSend, err);
                lastStatus = statusToSend;
            }
        }
    };

    public final Runnable STATUS_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            RadioStatus status = new RadioStatus();
            try {
                status = getStatus();
            } catch (Exception ex) {

                fireRunnable(status, ex);
            }

            fireRunnable(status, null);
        }
    };

    /**
     * Initialize the RadioManager
     * @param stationId ID of the radio.co station.
     * @throws MalformedURLException, IOException
     */
    public RadioManager(String stationId) {
        this.stationId = stationId;
    }
    //fire a runnable
    private void fireRunnable(Object data, Exception ex) {
        for(RunnableListener rl : listeners) {
            try {
                rl.runnableFired(data, ex);
            } catch (Exception exp) {
                System.err.println(exp);
            }
        }
    }

    /**
     * Start a runnable
     * @param r the runnable to start
     * @param period the delay
     * @param periodTimeUnit unit of time for the delay
     * @param listener RunnableListener to fire when the runnable runs.
     */
    public void startRunnable(Runnable r, int period, TimeUnit periodTimeUnit, RunnableListener listener) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(r, 0, period, periodTimeUnit);
        scheduledExecutorServices.add(new Pair<Runnable, ScheduledExecutorService>(r, scheduler));

        addListener(listener);
    }

    private void addListener(RunnableListener toAdd) {
        listeners.add(toAdd);
    }

    //API Methods
    public RadioStatus getStatus() throws RadioAPIException {
        RadioStatus result = new RadioStatus();
        try {
            URL url = new URL(PUBLIC_API_URL + stationId + "/status");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String response = "";

            while((inputLine = in.readLine())!=null) {
                response += inputLine;
            }

            in.close();
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(response, RadioStatus.class);

        } catch (Exception ex) {
            throw new RadioAPIException("Error fetching status", ex);
        }

        return result;
    }


    //Getters & Setters
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }



}
