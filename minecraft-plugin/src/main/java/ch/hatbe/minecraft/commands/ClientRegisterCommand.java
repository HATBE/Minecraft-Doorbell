package ch.hatbe.minecraft.commands;

import ch.hatbe.minecraft.DoorbellPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClientRegisterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;

        if(args.length != 0) {
            player.sendMessage("Please dont give arguments");
        }

        String client = JavaPlugin.getPlugin(DoorbellPlugin.class).getHardwareClientsStorage().getString(player.getName());

        if(client != null) {
            player.sendMessage("You already have a client, if you forgot the password, f you!");
            return false;
        }

        // there is no registered client yet

        String username = player.getName();
        String password = UUID.randomUUID().toString();

        // TODO: currently just in memory, make to storage
        JavaPlugin.getPlugin(DoorbellPlugin.class).getHardwareClientsStorage().set(String.format("%s.password", username), password);
        JavaPlugin.getPlugin(DoorbellPlugin.class).getHardwareClientsStorage().save();

        TextComponent message = new TextComponent("Successfully registered hardware client with password:");

        TextComponent passwordComponent = new TextComponent(password);
        passwordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, password));
        passwordComponent.setUnderlined(true);

        message.addExtra(passwordComponent);

        player.sendMessage(message);

        return true;
    }
}
