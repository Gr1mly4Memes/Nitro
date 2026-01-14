package gr1mly4memes.nitro;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NitroLogger extends Logger {
    public static final NitroLogger LOGGER = new NitroLogger();

    private NitroLogger() {
        super("Nitro", null);
        setParent(Bukkit.getLogger());
        setLevel(Level.ALL);
    }

    public void severe(String msg, Exception exception) {
        this.log(Level.SEVERE, msg, exception);
    }

    public void warning(String msg, Exception exception) {
        this.log(Level.WARNING, msg, exception);
    }

}