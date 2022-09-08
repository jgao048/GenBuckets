package dev.failures.main.Utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorUtil {
    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> colorize(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        for(String l : lore) {
            newLore.add(colorize(l));
        }
        return newLore;
    }
}
