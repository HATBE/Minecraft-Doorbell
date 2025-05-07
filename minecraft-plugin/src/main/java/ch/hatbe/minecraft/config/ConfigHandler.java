package ch.hatbe.minecraft.config;

import ch.hatbe.minecraft.DoorbellPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigHandler {
    protected FileConfiguration config;

    public ConfigHandler() {
        this.config = JavaPlugin.getPlugin(DoorbellPlugin.class).getConfig();
    }

    public void addDefault(String path, Object obj) {
        this.config.addDefault(path, obj);
        this.config.options().copyDefaults(true);
        this.save();
    }

    public FileConfiguration getConfig() {
        return  this.config;
    }

    public void save() {
        JavaPlugin.getPlugin(DoorbellPlugin.class).saveConfig();
    }

    public void set(String path, Object obj) {
        this.config.set(path, obj);
    }

    public Object get(String path) {
        return this.config.get(path);
    }

    public void delete(String path) {
        this.config.set(path, null);
    }

    public String getString(String path) {
        return this.config.getString(path);
    }

    public Double getDouble(String path) {
        return this.config.getDouble(path);
    }

    public int getInt(String path) {
        return this.config.getInt(path);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }
}
