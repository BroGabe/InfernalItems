package com.brogabe.infernalitems.menus;

import com.brogabe.infernalitems.InfernalItems;
import com.brogabe.infernalitems.items.*;
import com.brogabe.infernalitems.utils.ColorUtil;
import com.brogabe.infernalitems.utils.ItemCreator;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsMenu {

    private final InfernalItems plugin;

    private PaginatedGui gui;

    public ItemsMenu(InfernalItems plugin) {
        this.plugin = plugin;

        buildMenu();
    }

    public void openMenu(Player player) {
        gui.open(player);
    }

    private void buildMenu() {
        gui = Gui.paginated()
                .title(Component.text(ColorUtil.color("&cInfernal Items")))
                .rows(4)
                .create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.getFiller().fillBorder(new GuiItem(getBlackGlass()));
        gui.setItem(30, new GuiItem(getPreviousPage()));
        gui.setItem(32, new GuiItem(getNextPage()));
        gui.setItem(10, new GuiItem(BlinderBall.getItem(plugin, 1)));
        gui.setItem(11, new GuiItem(PearlItem.getItem(plugin, 1)));;
        gui.setItem(12, new GuiItem(WebItem.getItem(plugin, 1)));
        gui.setItem(13, new GuiItem(BardItem.getItem(plugin, 1)));
        gui.setItem(14, new GuiItem(SwitcherBall.getItem(plugin, 1)));
        gui.setItem(15, new GuiItem(AntiPearlItem.getItem(plugin, 1)));
        gui.setItem(16, new GuiItem(SpeedItem.getItem(plugin, 1)));
        gui.setItem(19, new GuiItem(StrengthItem.getItem(plugin, 1)));
    }

    public void reload() {
        buildMenu();
        gui.update();
    }

    private ItemStack getBlackGlass() {
        return new ItemCreator(Material.STAINED_GLASS_PANE, "&7", 1, 15, "", "").getItem();
    }

    private ItemStack getPreviousPage() {
        return new ItemCreator(Material.ARROW, "&bLast Page", 1, 0, "", "&7Click to go back a page.").getItem();
    }

    private ItemStack getNextPage() {
        return new ItemCreator(Material.ARROW, "&bNext Page", 1, 0, "", "&7Click to go forward a page.").getItem();
    }
}
