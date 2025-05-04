package ch.hatbe.minecraft;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class DoorbellHelper {

    public static Doorbell getDoorbellByLocation(Location location) {
        for(Doorbell doorbell : JavaPlugin.getPlugin(DoorbellPlugin.class).getRegisteredDoorbells()) {
            if(doorbell.getLocation().equals(location)) {
                return doorbell;
            }
        }

        return null;
    }
}
