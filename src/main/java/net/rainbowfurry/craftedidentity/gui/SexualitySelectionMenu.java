package net.rainbowfurry.craftedidentity.gui;

import net.rainbowfurry.craftedidentity.CraftedIdentity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SexualitySelectionMenu {
    private final CraftedIdentity plugin;

    public SexualitySelectionMenu(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = plugin.getConfig().getString("sexuality-menu.title", "§8» §bSexualität wählen");
        int size = plugin.getConfig().getInt("sexuality-menu.size", 27);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(title));
        
        // Füller Items
        ItemStack filler = createFillerItem("sexuality-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Sexuality Options
        ConfigurationSection optionsSection = plugin.getConfig().getConfigurationSection("sexuality-menu.options");
        if (optionsSection != null) {
            for (Map.Entry<Integer, String> entry : plugin.getSexualityMenuSlots().entrySet()) {
                int slot = entry.getKey();
                String key = entry.getValue();
                
                ConfigurationSection optionSection = optionsSection.getConfigurationSection(key);
                if (optionSection == null) continue;
                
                ItemStack item = plugin.createConfigSkullItem(optionSection, "§6" + key, "");
                inv.setItem(slot, item);
            }
        }
        
        player.openInventory(inv);
    }
    
    private ItemStack createFillerItem(String menuPath) {
        ConfigurationSection fillerSection = plugin.getConfig().getConfigurationSection(menuPath + ".filler-item");
        if (fillerSection == null) {
            return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        }
        return plugin.createConfigItem(fillerSection, "");
    }
}
