package ch.hatbe.minecraft.listeners;

import ch.hatbe.minecraft.tcpserver.TcpServer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteract implements Listener {

    TcpServer tcpServer;

    public OnPlayerInteract(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();

        if(clickedBlock.getType().equals(Material.POLISHED_BLACKSTONE_BUTTON)) {
            event.getPlayer().sendMessage("Hello, you pressed the doorbell2");

            this.tcpServer.getClients().getFirst().getOutputWriter().println("RING");
            //this.tcpServer.getClients().getFirst().getOutputWriter().print("RING");

        }
    }

}
