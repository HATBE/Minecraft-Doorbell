package ch.hatbe.minecraft;

import ch.hatbe.minecraft.client.HardwareClient;
import ch.hatbe.minecraft.commands.ClientRegisterCommand;
import ch.hatbe.minecraft.listeners.OnPlayerClickOnDoorbellButton;
import ch.hatbe.minecraft.listeners.OnPlayerPlaceDoorbellSign;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class DoorbellPlugin extends JavaPlugin {
    private TcpServer tcpServer;

    //private List<Doorbell> registeredDoorbells = new ArrayList<>();
    private List<HardwareClient> registeredHardwareClients = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        //this.registerClients();

        this.tcpServer = new TcpServer(1337).start();

        this.registerEvents();
        this.registerCommands();
    }

    // TODO: DEBUG ONLY, CHANGE TO FS LATER
    private void registerClients() {
        this.registeredHardwareClients.add(new HardwareClient("d910b0d2-0763-4aaf-8181-f1dde4d10fd4", "Qn$18v,8rXmt"));
    }

    private void registerCommands() {
        this.getCommand("clientregister").setExecutor(new ClientRegisterCommand());
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

    public List<HardwareClient> getRegisteredHardwareClients() {
        return registeredHardwareClients;
    }
}
