package dev.failures.main.Commands;

import dev.failures.main.GenBuckets;
import dev.failures.main.Storage.DataKeys;
import dev.failures.main.Utils.ColorUtil;
import dev.failures.main.Utils.PDUtil;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ShopCommand implements CommandExecutor {
    private GenBuckets main;
    public ShopCommand(GenBuckets main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        FileConfiguration conf = main.getConfig();
        PDUtil bucketType = new PDUtil(DataKeys.BUCKET_BLOCK);

        Set<String> shopContents = main.getConfig().getConfigurationSection("shop-contents").getKeys(false);
        Gui bucketShop = new Gui(conf.getInt("shop-rows"), ColorUtil.colorize(conf.getString("shop-name")));
        for(String content : shopContents) {
            String path = "shop-contents." + content;

            ItemStack bucket = ItemBuilder.from(Material.valueOf(conf.getString(path+".material")))
                    .setName(ColorUtil.colorize(conf.getString(path+".name")))
                    .setLore(ColorUtil.colorize(conf.getStringList(path+".lore")))
                    .build();

            bucketType.setItemDataString(bucket,conf.getString(path+".type").toUpperCase());

            GuiItem item = new GuiItem(bucket, event -> {
                event.setCancelled(true);
                if(GenBuckets.econ.getBalance(p) < conf.getInt(path+".cost")) {
                    p.sendMessage(ColorUtil.colorize(conf.getString("money-need")));
                    return;
                }
                if(p.getInventory().firstEmpty() == -1) {
                    p.sendMessage(ColorUtil.colorize(conf.getString("inventory-full")));
                    return;
                }
                p.getInventory().addItem(bucket);
                GenBuckets.econ.bankWithdraw(p.getName(),conf.getInt(path+".cost"));
                p.sendMessage(ColorUtil.colorize(conf.getString("bucket-bought").replace("%price%",String.valueOf(conf.getInt(path+".cost")))));

            });
            bucketShop.setItem(Integer.parseInt(content), item);
        }


        bucketShop.open(p);
        return false;
    }
}
