package ch.hatbe.minecraft;

import ch.hatbe.minecraft.listeners.OnPlayerInteract;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Doorbell extends JavaPlugin {
    TcpServer tcpServer;

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        this.tcpServer = new TcpServer(1337).start();

        this.registerEvents();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new OnPlayerInteract(tcpServer), this);
    }

    @Override
    public void onDisable() {
        this.tcpServer.stop();

        getLogger().info("Disabled");
    }
}
