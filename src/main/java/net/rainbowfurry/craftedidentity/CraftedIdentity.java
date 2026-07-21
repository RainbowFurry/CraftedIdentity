package net.rainbowfurry.craftedidentity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rainbowfurry.craftedidentity.commands.ProfileCommand;
import net.rainbowfurry.craftedidentity.gui.InventoryClickListener;
import net.rainbowfurry.craftedidentity.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CraftedIdentity extends JavaPlugin {

    private ProfileManager profileManager;
    private final Map<Integer, String> mainMenuSlots = new HashMap<>();
    private final Map<Integer, String> socialMenuSlots = new HashMap<>();
    private final Map<Integer, String> genderMenuSlots = new HashMap<>();
    private final Map<Integer, String> sexualityMenuSlots = new HashMap<>();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        profileManager = new ProfileManager(this);
        loadSlotMappings();
        
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        
        this.getCommand("profile").setExecutor(new ProfileCommand(this));
    }

    private void loadSlotMappings() {
        // Hauptmenü Slots
        ConfigurationSection mainItems = getConfig().getConfigurationSection("main-menu.items");
        if (mainItems != null) {
            for (String key : mainItems.getKeys(false)) {
                int slot = mainItems.getInt(key + ".slot");
                mainMenuSlots.put(slot, key);
            }
        }
        
        // Socials Slots
        ConfigurationSection socialPlatforms = getConfig().getConfigurationSection("socials-menu.platforms");
        if (socialPlatforms != null) {
            for (String key : socialPlatforms.getKeys(false)) {
                int slot = socialPlatforms.getInt(key + ".slot");
                socialMenuSlots.put(slot, key);
            }
        }
        
        // Gender Slots
        ConfigurationSection genderOptions = getConfig().getConfigurationSection("gender-menu.options");
        if (genderOptions != null) {
            int slot = 10;
            for (String key : genderOptions.getKeys(false)) {
                if (slot % 9 == 8) slot++;
                if (slot % 9 == 0) slot++;
                genderMenuSlots.put(slot, key);
                slot++;
            }
        }
        
        // Sexuality Slots
        ConfigurationSection sexualityOptions = getConfig().getConfigurationSection("sexuality-menu.options");
        if (sexualityOptions != null) {
            int slot = 10;
            for (String key : sexualityOptions.getKeys(false)) {
                if (slot % 9 == 8) slot++;
                if (slot % 9 == 0) slot++;
                sexualityMenuSlots.put(slot, key);
                slot++;
            }
        }
    }

    @Override
    public void onDisable() {
        if (profileManager != null) {
            profileManager.saveAllProfiles();
        }
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
    
    public Map<Integer, String> getMainMenuSlots() { return mainMenuSlots; }
    public Map<Integer, String> getSocialMenuSlots() { return socialMenuSlots; }
    public Map<Integer, String> getGenderMenuSlots() { return genderMenuSlots; }
    public Map<Integer, String> getSexualityMenuSlots() { return sexualityMenuSlots; }
    
    public LegacyComponentSerializer getLegacySerializer() { return legacySerializer; }
    
    public ItemStack createConfigItem(ConfigurationSection section, String valuePlaceholder) {
        Material material = Material.matchMaterial(section.getString("material", "STONE"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String name = section.getString("name", "");
            if (!name.isEmpty()) {
                meta.displayName(legacySerializer.deserialize(name.replace("%value%", valuePlaceholder)));
            }
            
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                List<Component> loreComponents = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(legacySerializer.deserialize(line.replace("%value%", valuePlaceholder)));
                }
                meta.lore(loreComponents);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public ItemStack createConfigSkullItem(ConfigurationSection section, String name, String valuePlaceholder) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        
        if (meta != null) {
            String headOwner = section.getString("head", "MHF_Steve");
            meta.setOwner(headOwner);
            
            if (!name.isEmpty()) {
                meta.displayName(legacySerializer.deserialize(name.replace("%value%", valuePlaceholder)));
            }
            
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                List<Component> loreComponents = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(legacySerializer.deserialize(line.replace("%value%", valuePlaceholder)));
                }
                meta.lore(loreComponents);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}