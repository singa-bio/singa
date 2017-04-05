package de.bioforscher.singa.core.utility;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogManager {

    public static void setDebugLevel(Level newLvl) {
        Logger anonymousLogger = java.util.logging.LogManager.getLogManager().getLogger("");
        Handler[] handlers = anonymousLogger.getHandlers();
        anonymousLogger.setLevel(newLvl);
        for (Handler h : handlers) {
            if (h instanceof FileHandler)
                h.setLevel(newLvl);
        }
    }

}
