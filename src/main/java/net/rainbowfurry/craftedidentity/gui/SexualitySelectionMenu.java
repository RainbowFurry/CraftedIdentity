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
        String title = plugin.getConfig().getString("sexuality-menu.title", "<gradient:FF0000,FFFF00>Sexualität wählen</gradient>");
        String processedTitle = plugin.processGradients(title);
        int size = plugin.getConfig().getInt("sexuality-menu.size", 27);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(processedTitle));
        
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
                
                String name = optionSection.getString("name", key);
                ItemStack item = plugin.createConfigSkullItem(optionSection, name, "");
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
