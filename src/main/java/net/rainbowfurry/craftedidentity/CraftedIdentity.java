package net.rainbowfurry.craftedidentity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rainbowfurry.craftedidentity.commands.ProfileCommand;
import net.rainbowfurry.craftedidentity.gui.InventoryClickListener;
import net.rainbowfurry.craftedidentity.profile.PlayerProfile;
import net.rainbowfurry.craftedidentity.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CraftedIdentity extends JavaPlugin implements Listener {

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
        
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        
        this.getCommand("profile").setExecutor(new ProfileCommand(this));
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
        profile.setLastJoinTime(System.currentTimeMillis());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = profileManager.getProfile(player.getUniqueId());
        long playtime = System.currentTimeMillis() - profile.getLastJoinTime();
        profile.addPlaytime(playtime);
        profileManager.saveProfile(player.getUniqueId());
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
    
    /**
     * Processes text with <gradient:hex1,hex2>Text</gradient> tags into Minecraft colored text
     */
    public String processGradients(String text) {
        if (text == null || text.isEmpty()) return "";
        
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        int startTagIndex;
        
        while ((startTagIndex = text.indexOf("<gradient:", lastIndex)) != -1) {
            // Add text before the gradient tag
            result.append(text, lastIndex, startTagIndex);
            
            // Find the end of the gradient parameters
            int paramsEndIndex = text.indexOf('>', startTagIndex);
            if (paramsEndIndex == -1) break;
            
            // Extract parameters (hex1,hex2)
            String paramsStr = text.substring(startTagIndex + "<gradient:".length(), paramsEndIndex);
            String[] params = paramsStr.split(",");
            if (params.length < 2) {
                lastIndex = paramsEndIndex + 1;
                continue;
            }
            
            String hex1 = params[0].trim();
            String hex2 = params[1].trim();
            
            // Find the closing </gradient> tag
            int endTagIndex = text.indexOf("</gradient>", paramsEndIndex);
            if (endTagIndex == -1) break;
            
            // Extract the text to gradient
            String gradientText = text.substring(paramsEndIndex + 1, endTagIndex);
            
            // Parse hex colors to RGB
            int[] rgb1 = hexToRgb(hex1);
            int[] rgb2 = hexToRgb(hex2);
            
            // Apply gradient
            result.append(applyGradient(gradientText, rgb1, rgb2));
            
            // Move past the closing tag
            lastIndex = endTagIndex + "</gradient>".length();
        }
        
        // Add remaining text
        if (lastIndex < text.length()) {
            result.append(text.substring(lastIndex));
        }
        
        return result.toString();
    }
    
    /**
     * Converts a hex string (with or without #) to RGB array
     */
    private int[] hexToRgb(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() != 6) {
            return new int[]{85, 255, 255}; // Default cyan
        }
        try {
            return new int[]{
                Integer.parseInt(hex.substring(0, 2), 16),
                Integer.parseInt(hex.substring(2, 4), 16),
                Integer.parseInt(hex.substring(4, 6), 16)
            };
        } catch (NumberFormatException e) {
            return new int[]{85, 255, 255}; // Default cyan
        }
    }
    
    /**
     * Applies a gradient to the given text using the given RGB start/end colors
     */
    private String applyGradient(String text, int[] startRgb, int[] endRgb) {
        if (text.isEmpty()) return "";
        
        StringBuilder gradientText = new StringBuilder();
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);
            int r = (int) (startRgb[0] + (endRgb[0] - startRgb[0]) * ratio);
            int g = (int) (startRgb[1] + (endRgb[1] - startRgb[1]) * ratio);
            int b = (int) (startRgb[2] + (endRgb[2] - startRgb[2]) * ratio);
            
            String hexR = String.format("%02x", r);
            String hexG = String.format("%02x", g);
            String hexB = String.format("%02x", b);
            
            gradientText.append("§x")
                .append("§").append(hexR.charAt(0)).append("§").append(hexR.charAt(1))
                .append("§").append(hexG.charAt(0)).append("§").append(hexG.charAt(1))
                .append("§").append(hexB.charAt(0)).append("§").append(hexB.charAt(1))
                .append(text.charAt(i));
        }
        
        return gradientText.toString();
    }
    
    public ItemStack createConfigItem(ConfigurationSection section, String valuePlaceholder) {
        Material material = Material.matchMaterial(section.getString("material", "STONE"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            String name = section.getString("name", "");
            if (!name.isEmpty()) {
                String processedName = processGradients(name.replace("%value%", valuePlaceholder));
                meta.displayName(legacySerializer.deserialize(processedName));
            }
            
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                List<Component> loreComponents = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(legacySerializer.deserialize(processGradients(line.replace("%value%", valuePlaceholder))));
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
                String processedName = processGradients(name.replace("%value%", valuePlaceholder));
                meta.displayName(legacySerializer.deserialize(processedName));
            }
            
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                List<Component> loreComponents = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(legacySerializer.deserialize(processGradients(line.replace("%value%", valuePlaceholder))));
                }
                meta.lore(loreComponents);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}