package ch.hatbe.minecraft.listeners;

import ch.hatbe.minecraft.tcpserver.ClientHandler;
import ch.hatbe.minecraft.tcpserver.PackageHelper;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerClickOnDoorbellButton implements Listener {

    TcpServer tcpServer;

    public OnPlayerClickOnDoorbellButton(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block clickedBlock = event.getClickedBlock();

        if(!clickedBlock.getType().name().endsWith("_BUTTON")) {
            return;
        }

        Block blockOverButton = clickedBlock.getRelative(BlockFace.UP);

        if(!blockOverButton.getType().name().endsWith("_SIGN")) {
            return;
        }

        if(!(blockOverButton.getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) blockOverButton.getState();
        SignSide frontSide = sign.getSide(Side.FRONT);

        String[] lines = frontSide.getLines();

        if(!lines[0].equals("[DOORBELL]")) {
            return;
        }

        event.setCancelled(true);

        String username = lines[1];

        for(ClientHandler clientHandler : this.tcpServer.getClients()) {
            if(!clientHandler.getHwCLient().getUsername().equals(username)) {
                event.getPlayer().sendMessage(String.format("There was no Doorbell found for the user %s", username));
                return;
            }

            if(clientHandler.getHwCLient().getCooldown() > (System.currentTimeMillis() / 1000)) {
                event.getPlayer().sendMessage(String.format("Cooldown active. Please wait for %s seconds.", clientHandler.getHwCLient().getCooldown() - (System.currentTimeMillis() / 1000)));
                return;
            }

            clientHandler.getHwCLient().resetCooldown(20);

            clientHandler.getToClientWriter().println(PackageHelper.encodeNetworkPackage(new String[]{"RING", event.getPlayer().getName()}));
            event.getPlayer().sendMessage("Successfully rang the doorbell");
            return;
        }

        event.getPlayer().sendMessage(String.format("There was no Doorbell found for the user %s", username));
    }
}
