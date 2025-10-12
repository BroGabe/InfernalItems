package com.brogabe.infernalitems.items;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.enums.InfernalType;
import com.brogabe.infernalitems.utils.ItemCreator;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpeedItem {

    public static void giveItem(Player player, InfernalItems plugin, int amount) {
        player.getInventory().addItem(getItem(plugin, amount));
    }

    public static ItemStack getItem(InfernalItems plugin, int amount) {
        FileConfiguration config = plugin.getSpeedCFG().getConfig();

        List<String> lore = config.getStringList("speed-item.lore");
        String name = config.getString("speed-item.name");

        ItemStack itemStack = new ItemCreator(Material.INK_SACK, name, 1, 12, "", lore).getItem();
        itemStack.setAmount(amount);

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound compound = nbtItem.getOrCreateCompound("InfernalItems");

        compound.setString("type", InfernalType.SPEED.toString());

        return nbtItem.getItem();
    }
}
