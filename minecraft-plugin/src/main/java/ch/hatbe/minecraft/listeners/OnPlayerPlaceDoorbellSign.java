package ch.hatbe.minecraft.listeners;


import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class OnPlayerPlaceDoorbellSign implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();

        if(!lines[0].equals("[DOORBELL]")) {
            return;
        }

        Block blockBelowSign = event.getBlock().getRelative(BlockFace.DOWN);

        if(!blockBelowSign.getType().name().endsWith("_BUTTON")) {
            event.getPlayer().sendMessage("There is no doorbell (button) under the sign!");
            return;
        }

        // DISPLAY FORMAT::
        //      __________________________________
        //      |USERNAME UP TO 16 CHARS          |
        //      |HAS RUNG (DOORNAME UP TO 7 CHARS)|
        //      -----------------------------------

        // TODO: CHECK IF NAME EXISTS ON LINE 2
        String doorBellName = lines[1];

        if(doorBellName.isEmpty()) {
            event.getPlayer().sendMessage("The doorbell must have a name!");
            return;
        }

        if(doorBellName.length() > 7) {
            event.getPlayer().sendMessage("The doorbell name can have a maximum of 7 characters!");
            return;
        }

        Player player = event.getPlayer();

        event.getPlayer().sendMessage("You have successfully created a doorbell");

        // TODO: REGISTER USER
        System.out.println(doorBellName + " " + player.getName());
    }

}
