package net.rainbowfurry.craftedidentity.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rainbowfurry.craftedidentity.CraftedIdentity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class GenderSelectionMenu {
    private final CraftedIdentity plugin;

    public GenderSelectionMenu(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = plugin.getConfig().getString("gender-menu.title", "<gradient:FF00FF,800080>Geschlecht wählen</gradient>");
        String processedTitle = plugin.processGradients(title);
        int size = plugin.getConfig().getInt("gender-menu.size", 27);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(processedTitle));
        
        // Füller Items
        ItemStack filler = createFillerItem("gender-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Gender Options
        ConfigurationSection optionsSection = plugin.getConfig().getConfigurationSection("gender-menu.options");
        if (optionsSection != null) {
            for (Map.Entry<Integer, String> entry : plugin.getGenderMenuSlots().entrySet()) {
                int slot = entry.getKey();
                String key = entry.getValue();
                
                ConfigurationSection optionSection = optionsSection.getConfigurationSection(key);
                if (optionSection == null) continue;
                
                Material material = Material.matchMaterial(optionSection.getString("material", "WHITE_WOOL"));
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                
                if (meta != null) {
                    String name = optionSection.getString("name", key);
                    String processedName = plugin.processGradients(name);
                    meta.displayName(plugin.getLegacySerializer().deserialize(processedName));
                    meta.lore(List.of(plugin.getLegacySerializer().deserialize("§7Klicke, um auszuwählen")));
                    item.setItemMeta(meta);
                }
                
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
