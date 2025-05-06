package ch.hatbe.minecraft.client;

public class HardwareClient {
    private String id;
    private String passwordHash;

    public HardwareClient(String id, String passwordHash) {
        this.id = id;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
