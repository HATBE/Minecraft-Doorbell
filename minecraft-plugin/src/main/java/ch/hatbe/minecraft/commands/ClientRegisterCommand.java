package ch.hatbe.minecraft.commands;

import ch.hatbe.minecraft.DoorbellPlugin;
import ch.hatbe.minecraft.client.HardwareClient;
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

        for(HardwareClient client : JavaPlugin.getPlugin(DoorbellPlugin.class).getRegisteredHardwareClients()) {
            System.out.println(player.getUniqueId());
            if(client.getId().equals(player.getUniqueId().toString())) {
                player.sendMessage("You already have a client, if you forgot the password, f you!");
                return false;
            }
        }

        // there is no registered client yet

        String clientId = player.getUniqueId().toString();
        String password = UUID.randomUUID().toString();

        // TODO: currently just in memory, make to storage
        JavaPlugin.getPlugin(DoorbellPlugin.class).getRegisteredHardwareClients().add(new HardwareClient(clientId, password));

        TextComponent message = new TextComponent("Successfully registered hardware client: ");

        TextComponent clientComponent = new TextComponent(clientId);
        clientComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, clientId));
        clientComponent.setUnderlined(true); // Optional styling

        TextComponent passwordLabel = new TextComponent(" with password: ");

        TextComponent passwordComponent = new TextComponent(password);
        passwordComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, password));
        passwordComponent.setUnderlined(true); // Optional styling

        message.addExtra(clientComponent);
        message.addExtra(passwordLabel);
        message.addExtra(passwordComponent);

        player.sendMessage(message);

        return true;
    }
}
