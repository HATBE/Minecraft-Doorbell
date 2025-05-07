package ch.hatbe.minecraft.config;

import ch.hatbe.minecraft.DoorbellPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CustomConfigHandler extends ConfigHandler {
    private File configFile;
    private String name;

    public CustomConfigHandler(String name)  {
        this.name = name;

        this.configFile = new File(JavaPlugin.getPlugin(DoorbellPlugin.class).getDataFolder(), name + ".yaml");
        this.config = YamlConfiguration.loadConfiguration(configFile);

        save();
    }

    @Override
    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            JavaPlugin.getPlugin(DoorbellPlugin.class).getLogger().warning(String.format("Failed to save config: %s", this.name));
            e.printStackTrace();
        }
    }
}
