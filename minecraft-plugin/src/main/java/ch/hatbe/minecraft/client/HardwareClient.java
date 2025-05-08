package ch.hatbe.minecraft.client;

public class HardwareClient {
    private String username;
    private int cooldown;

    public HardwareClient(String username) {
        this.cooldown = 0;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void resetCooldown(int seconds) {
        this.cooldown = (int) (System.currentTimeMillis() / 1000) + seconds;
    }
}
