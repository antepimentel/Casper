package Radio;

import Exceptions.RadioAPIException;

import java.io.IOException;

public class StationManager {
    public static RadioManager manager = new RadioManager("s4927cc83c");

    public static RadioStatus getStatus() throws RadioAPIException, IOException {
        return manager.getStatus();
    }
}
