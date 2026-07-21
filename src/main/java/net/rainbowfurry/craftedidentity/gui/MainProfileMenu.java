package net.rainbowfurry.craftedidentity.gui;

import net.rainbowfurry.craftedidentity.CraftedIdentity;
import net.rainbowfurry.craftedidentity.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainProfileMenu {
    private final CraftedIdentity plugin;

    public MainProfileMenu(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        
        String title = plugin.getConfig().getString("main-menu.title", "§8» §bDein Profil");
        int size = plugin.getConfig().getInt("main-menu.size", 54);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(title));
        
        // Füller Items
        ItemStack filler = createFillerItem("main-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Hauptitems
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("main-menu.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;
                
                int slot = itemSection.getInt("slot");
                String value = getProfileValue(profile, key);
                
                ItemStack item = plugin.createConfigItem(itemSection, value);
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
    
    private String getProfileValue(PlayerProfile profile, String key) {
        return switch (key) {
            case "realname" -> profile.getRealName().isEmpty() ? "§cNicht festgelegt" : profile.getRealName();
            case "age" -> profile.getAge() == 0 ? "§cNicht festgelegt" : String.valueOf(profile.getAge());
            case "gender" -> profile.getGender().isEmpty() ? "§cNicht festgelegt" : profile.getGender();
            case "sexuality" -> profile.getSexuality().isEmpty() ? "§cNicht festgelegt" : profile.getSexuality();
            case "description" -> profile.getDescription().isEmpty() ? "§cNicht festgelegt" : profile.getDescription();
            case "country" -> profile.getCountry().isEmpty() ? "§cNicht festgelegt" : profile.getCountry();
            case "languages" -> profile.getLanguages().isEmpty() ? "§cNicht festgelegt" : String.join(", ", profile.getLanguages());
            default -> "§cN/A";
        };
    }
}
