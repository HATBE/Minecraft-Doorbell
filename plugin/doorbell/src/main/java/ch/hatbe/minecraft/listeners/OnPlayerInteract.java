package ch.hatbe.minecraft.listeners;

import ch.hatbe.minecraft.Doorbell;
import ch.hatbe.minecraft.DoorbellHelper;
import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteract implements Listener {

    TcpServer tcpServer;

    public OnPlayerInteract(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block clickedBlock = event.getClickedBlock();

        if(clickedBlock.getType().name().endsWith("_BUTTON")) {

            //if(clickedBlock.getLocation().getBlockX())


            Doorbell doorbell = DoorbellHelper.getDoorbellByLocation(event.getClickedBlock().getLocation());

            if(doorbell == null) {
                return;
            }

            event.getPlayer().sendMessage("ringing doorbell");

            // TODO: FIX
            this.tcpServer.getClients().forEach(client -> {
                client.getToClientWriter().println("RING");
            });
        }
    }

}
