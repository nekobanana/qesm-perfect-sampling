package org.qesm.app.model.utils;

import java.io.IOException;
import java.util.logging.*;

public class Log {
    public static Logger logger = Logger.getLogger("MyLog");
    static FileHandler fh;

    static {
        try {
            fh = new FileHandler("log.txt");
            LogManager.getLogManager().reset();
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
