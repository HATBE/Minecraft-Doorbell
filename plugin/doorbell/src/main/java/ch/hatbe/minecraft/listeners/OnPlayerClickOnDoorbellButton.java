package ch.hatbe.minecraft.listeners;

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

        String username = lines[1];

        System.out.println(username);

        this.tcpServer.getClients().forEach(client -> {
            client.getToClientWriter().println("RING");
        });
    }
}
