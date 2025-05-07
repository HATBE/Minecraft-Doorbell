package ch.hatbe.minecraft;

import ch.hatbe.minecraft.commands.ClientRegisterCommand;
import ch.hatbe.minecraft.config.CustomConfigHandler;
import ch.hatbe.minecraft.listeners.OnPlayerClickOnDoorbellButton;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public final class DoorbellPlugin extends JavaPlugin {
    private CustomConfigHandler hardwareClientsStorage;

    private TcpServer tcpServer;

    @Override
    public void onEnable() {
        getLogger().info("Enabled");

        this.hardwareClientsStorage = new CustomConfigHandler("hwclients");

        this.tcpServer = new TcpServer(1337).start();

        this.registerEvents();
        this.registerCommands();
    }

    private void registerCommands() {
        this.getCommand("clientregister").setExecutor(new ClientRegisterCommand());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new OnPlayerClickOnDoorbellButton(tcpServer), this);
    }

    @Override
    public void onDisable() {
        this.tcpServer.stop();

        getLogger().info("Disabled");
    }

    public CustomConfigHandler getHardwareClientsStorage() {
        return hardwareClientsStorage;
    }
}
