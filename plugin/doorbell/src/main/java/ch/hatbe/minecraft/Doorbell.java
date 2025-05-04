package ch.hatbe.minecraft;


import org.bukkit.Location;

public class Doorbell {
    private String id;
    private String password;
    private String username;
    private Location location;

    public Doorbell(String id, String password, String playerId, String username, Location location) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return username;
    }

    public Location getLocation() {
        return location;
    }
}
