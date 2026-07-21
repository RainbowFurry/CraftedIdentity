package net.rainbowfurry.craftedidentity.gui;

import net.rainbowfurry.craftedidentity.CraftedIdentity;
import net.rainbowfurry.craftedidentity.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SocialsMenu {
    private final CraftedIdentity plugin;

    public SocialsMenu(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        
        String title = plugin.getConfig().getString("socials-menu.title", "<gradient:0000FF,00FFFF>Socials</gradient>");
        String processedTitle = plugin.processGradients(title);
        int size = plugin.getConfig().getInt("socials-menu.size", 54);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(processedTitle));
        
        // Füller Items
        ItemStack filler = createFillerItem("socials-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Back Item
        ConfigurationSection backSection = plugin.getConfig().getConfigurationSection("socials-menu.back-item");
        if (backSection != null) {
            int slot = backSection.getInt("slot");
            ItemStack backItem = plugin.createConfigItem(backSection, "");
            inv.setItem(slot, backItem);
        }
        
        // Social Platforms
        ConfigurationSection platformsSection = plugin.getConfig().getConfigurationSection("socials-menu.platforms");
        if (platformsSection != null) {
            for (String key : platformsSection.getKeys(false)) {
                ConfigurationSection platformSection = platformsSection.getConfigurationSection(key);
                if (platformSection == null) continue;
                
                int slot = platformSection.getInt("slot");
                String value = profile.getSocial(key);
                if (value.isEmpty()) value = "§cNicht festgelegt";
                
                ItemStack item = plugin.createConfigSkullItem(platformSection, platformSection.getString("name", key), value);
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
