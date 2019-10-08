package me.fsml.holodrops.util;

import me.fsml.holodrops.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Strings {
    
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public static List<String> colorList(List<String> list) { // color the glowlist
        for (int x = 0; x < list.size(); x++) {
            list.set(x, color(list.get(x)));
        }
        return list;
    }
    
    public static String stripColor(String string) {
        return ChatColor.stripColor(string);
    }
    
    public static String makeName(Item drop, int count) {
        String formatted = Main.m.settings.getFormat().toUpperCase();
        String itemName = "";
        itemName = makeItemName(drop);
        if (Main.m.settings.isBlacklisted(itemName) || isUUID(itemName)) {
            itemName = "";
        }
        formatted = rePlaceholders(formatted, itemName, count);
        
        return itemName.length() == 0 ? itemName : formatted;
        
    }
    
    public static String makeItemName(Item drop) {
        String itemName = "";
        
        ItemMeta meta = drop.getItemStack().getItemMeta();
        
        if (drop.getItemStack().getType() == Material.WRITTEN_BOOK) {
            itemName = bookTitle((BookMeta)meta);
        }
        else if (meta.hasDisplayName() || Main.m.settings.getCustomNamesOnly()) {
            itemName = meta.getDisplayName();
        } else {
            itemName = Main.m.settings.getNameFromMat(drop.getItemStack().getType().toString());
        }
        
        return itemName;
    }
    
    private static String bookTitle(BookMeta meta) {
        String title = meta.getTitle() == null ? " " : meta.getTitle();
        String itemName = ConfigReader.getString("item-names.WRITTEN_BOOK");
        return itemName.replace("%title%", title);
    }
    
    private static String rePlaceholders(String formatted, String item, int count) {
        formatted = formatted.replace("%P%", Main.m.settings.getPrefix())
                .replace("%I%", item)
                .replace("%S%", Main.m.settings.getSuffix());
        // single stacks
        // count != 0 is for item frames (never display the stack count)
        if (count != 0 && count != 1 || Main.m.settings.getSingleStack()) {
            formatted = formatted.replace("%C%", Main.m.settings.getStackFormat().replace("%amount%", "" + count));
        } else {
            // remove a the space at the end
            formatted = formatted.replace(" %C%", "");
            // in case there was no space
            formatted = formatted.replace("%C%", "");
        }
        return formatted;
    }
    
    // count is supplied to make a call to rePlaceholders
    public static void makeItemFrameName(ItemStack item, int count) {
        ItemMeta meta = item.getItemMeta();
        String formatted = Main.m.settings.getFormat().toUpperCase();
        String itemName = "";
    
        if (item.getType() == Material.WRITTEN_BOOK) {
            itemName = bookTitle((BookMeta)meta);
        } else {
            itemName = Main.m.settings.getNameFromMat(item.getType().name());
        }
        formatted = rePlaceholders(formatted, itemName, count);
        
        meta.setDisplayName(itemName.length() == 0 ? itemName : formatted);
        item.setItemMeta(meta);
        
    }
    
    public static void addWatermark(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList();
        lore.add("HoloDrops");
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    public static void removeWatermark(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (hasWatermark(item)) {
            List<String> lore = meta.getLore();
            lore.remove(lore.size() - 1);
            meta.setLore(lore);
            meta.setDisplayName("");
            item.setItemMeta(meta);
        }
    }
    
    public static boolean hasWatermark(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            // last line has watermark
            return lore.get(lore.size() - 1).equals("HoloDrops");
        }
        return false;
    }
    
    public static boolean isUUID(String name) {
        return (stripColor(name).matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
    }
    
    
}
