package dev.failures.main.Listeners;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.PermissibleAction;
import dev.failures.main.GenBuckets;
import dev.failures.main.Storage.DataKeys;
import dev.failures.main.Utils.PDUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class BucketListener implements Listener {
    private GenBuckets main;

    public BucketListener(GenBuckets main) {
        this.main = main;
    }

    @EventHandler
    private void bucketPlace(PlayerBucketEmptyEvent e) {
        PDUtil bucketType = new PDUtil(DataKeys.BUCKET_BLOCK);
        Player p = e.getPlayer();
        if(p.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        ItemStack itemHand = p.getInventory().getItemInMainHand();

        if(!bucketType.itemDataContainsKey(itemHand)) return;
        e.setCancelled(true);
        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
        String direction = getBlockFace(p).toString();
        double x = 0;
        double y = 0;
        double z = 0;
        boolean notUD = true;
        if(direction.equals("SOUTH")) {
            z = 1;
        } else if(direction.equals("NORTH")) {
            z = -1;
        } else if(direction.equals("WEST")) {
            x = -1;
        } else if(direction.equals("EAST")) {
            x = 1;
        } else if(direction.equals("UP")) {
            y = 1;
            notUD = false;
        } else {
            y = -1;
            notUD = false;
        }

        double finalZ = z;
        double finalY = y;
        double finalX = x;
        boolean finalNotUD = notUD;
        final int[] counter = {0};
        int maxHeight = p.getWorld().getMaxHeight();

        Location pos = e.getBlockClicked().getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                Location newPos = pos.add(finalX, finalY, finalZ);
                if(!FactionsBlockListener.playerCanBuildDestroyBlock(p,newPos, PermissibleAction.BUILD,true)) {
                    cancel();
                    return;
                }

                if(counter[0] == main.getConfig().getInt("max-blocks") && finalNotUD) {
                    cancel();
                    return;
                }

                if(newPos.getBlock().getType() != Material.AIR || newPos.getY() == maxHeight) {
                    cancel();
                    return;
                }
                newPos.getBlock().setType(Material.valueOf(bucketType.getItemDataString(itemHand)));
                counter[0]++;
            }
        }.runTaskTimer(main,0,20/main.getConfig().getInt("blocks-per-second"));
    }

    public BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }
}
