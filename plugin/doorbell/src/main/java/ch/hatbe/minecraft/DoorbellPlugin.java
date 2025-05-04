package ch.hatbe.minecraft;

import ch.hatbe.minecraft.listeners.OnPlayerClickOnDoorbellButton;
import ch.hatbe.minecraft.listeners.OnPlayerPlaceDoorbellSign;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class DoorbellPlugin extends JavaPlugin {
    private TcpServer tcpServer;

    private List<Doorbell> registeredDoorbells = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        this.registerClients();
        this.tcpServer = new TcpServer(1337).start();

        this.registerEvents();
    }

    // TODO: DEBUG ONLY, CHANGE TO FS LATER
    private void registerClients() {
        //this.registeredDoorbells.add(new Doorbell("35d3c3be-bf1a-4629-834b-43cb8aeb1fcc", "Qn$18v,8rXmt", "HATBE");
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new OnPlayerClickOnDoorbellButton(tcpServer), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerPlaceDoorbellSign(), this);
    }

    @Override
    public void onDisable() {
        this.tcpServer.stop();

        getLogger().info("Disabled");
    }

    public List<Doorbell> getRegisteredDoorbells() {
        return this.registeredDoorbells;
    }
}
