package ch.hatbe.minecraft;

import ch.hatbe.minecraft.listeners.OnPlayerInteract;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Doorbell extends JavaPlugin {

    TcpServer tcpServer;

    @Override
    public void onEnable() {
        System.out.println("PLUGIN ENABLED");

        this.tcpServer = new TcpServer();

        Bukkit.getPluginManager().registerEvents(new OnPlayerInteract(tcpServer), this);
    }

    @Override
    public void onDisable() {
        // TODO: disable tcp server
        // Plugin shutdown logic
    }
}
