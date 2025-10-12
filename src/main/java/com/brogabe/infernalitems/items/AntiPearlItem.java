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

public class AntiPearlItem {

    public static void giveItem(Player player, InfernalItems plugin, int amount) {
        player.getInventory().addItem(getItem(plugin, amount));
    }

    public static ItemStack getItem(InfernalItems plugin, int amount) {
        FileConfiguration config = plugin.getAntiPearlCFG().getConfig();

        List<String> lore = config.getStringList("pearl-item.lore");
        String name = config.getString("pearl-item.name");

        ItemStack itemStack = new ItemCreator(Material.EYE_OF_ENDER, name, 1, 0, "", lore).getItem();
        itemStack.setAmount(amount);

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound compound = nbtItem.getOrCreateCompound("InfernalItems");

        compound.setString("type", InfernalType.ANTI_PEARL.toString());

        return nbtItem.getItem();
    }
}
