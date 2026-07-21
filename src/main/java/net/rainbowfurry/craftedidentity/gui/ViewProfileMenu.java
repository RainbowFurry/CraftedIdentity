package net.rainbowfurry.craftedidentity.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rainbowfurry.craftedidentity.CraftedIdentity;
import net.rainbowfurry.craftedidentity.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ViewProfileMenu {
    private final CraftedIdentity plugin;

    public ViewProfileMenu(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target) {
        PlayerProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());
        
        String title = plugin.getConfig().getString("view-profile-menu.title", "<gradient:00FFFF,FFFFFF>Profil von %player%</gradient>");
        title = title.replace("%player%", target.getName());
        String processedTitle = plugin.processGradients(title);
        int size = plugin.getConfig().getInt("view-profile-menu.size", 54);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(processedTitle));
        
        // Füller Items
        ItemStack filler = createFillerItem("view-profile-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Spieler Kopf
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(plugin.getLegacySerializer().deserialize(plugin.processGradients("<gradient:FFAA00,FF5500>" + target.getName() + "</gradient>")));
        head.setItemMeta(meta);
        inv.setItem(4, head);
        
        // Profil Info Items
        inv.setItem(10, createInfoItem(profile.getRealName().isEmpty() ? "§cNicht festgelegt" : profile.getRealName(), "<gradient:FFFF00,FFAA00>Echter Name</gradient>"));
        
        // Alter + Geburtsdatum
        List<String> ageLore = new java.util.ArrayList<>();
        String ageValue = profile.getAge() == 0 ? "§cNicht festgelegt" : String.valueOf(profile.getAge());
        String birthDateValue = profile.getBirthDate().isEmpty() ? "§cNicht festgelegt" : profile.getBirthDate();
        ageLore.add("§7Alter: " + ageValue);
        ageLore.add("§7Geburtsdatum: " + birthDateValue);
        ItemStack ageItem = new ItemStack(Material.CLOCK);
        var ageMeta = ageItem.getItemMeta();
        ageMeta.displayName(plugin.getLegacySerializer().deserialize(plugin.processGradients("<gradient:FFAA00,FFFF00>Alter & Geburtsdatum</gradient>")));
        List<Component> ageLoreComp = new java.util.ArrayList<>();
        for (String l : ageLore) {
            ageLoreComp.add(plugin.getLegacySerializer().deserialize(l));
        }
        ageMeta.lore(ageLoreComp);
        ageItem.setItemMeta(ageMeta);
        inv.setItem(12, ageItem);
        
        // Geschlecht (mit custom material)
        String gender = profile.getGender();
        if (!gender.isEmpty()) {
            ConfigurationSection genderOptions = plugin.getConfig().getConfigurationSection("gender-menu.options");
            if (genderOptions != null && genderOptions.contains(gender)) {
                ConfigurationSection genderSection = genderOptions.getConfigurationSection(gender);
                String materialName = genderSection.getString("material", "WHITE_WOOL");
                Material genderMaterial = Material.matchMaterial(materialName);
                if (genderMaterial == null) genderMaterial = Material.WHITE_WOOL;
                ItemStack genderItem = new ItemStack(genderMaterial);
                var genderMeta = genderItem.getItemMeta();
                String genderName = genderSection.getString("name", "<gradient:FF00FF,800080>Geschlecht</gradient>");
                genderMeta.displayName(plugin.getLegacySerializer().deserialize(plugin.processGradients(genderName)));
                genderMeta.lore(List.of(plugin.getLegacySerializer().deserialize("§7" + gender)));
                genderItem.setItemMeta(genderMeta);
                inv.setItem(14, genderItem);
            } else {
                inv.setItem(14, createInfoItem(gender, "<gradient:FF00FF,800080>Geschlecht</gradient>"));
            }
        } else {
            inv.setItem(14, createInfoItem("§cNicht festgelegt", "<gradient:FF00FF,800080>Geschlecht</gradient>"));
        }
        
        // Sexualität (mit custom head)
        String sexuality = profile.getSexuality();
        if (!sexuality.isEmpty()) {
            ConfigurationSection sexualityOptions = plugin.getConfig().getConfigurationSection("sexuality-menu.options");
            if (sexualityOptions != null && sexualityOptions.contains(sexuality)) {
                ConfigurationSection sexualitySection = sexualityOptions.getConfigurationSection(sexuality);
                String headOwner = sexualitySection.getString("head", "MHF_Steve");
                ItemStack sexualityItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta sexualityMeta = (SkullMeta) sexualityItem.getItemMeta();
                sexualityMeta.setOwner(headOwner);
                String sexualityName = sexualitySection.getString("name", "<gradient:FF0000,FFFF00>Sexualität</gradient>");
                sexualityMeta.displayName(plugin.getLegacySerializer().deserialize(plugin.processGradients(sexualityName)));
                sexualityMeta.lore(List.of(plugin.getLegacySerializer().deserialize("§7" + sexuality)));
                sexualityItem.setItemMeta(sexualityMeta);
                inv.setItem(16, sexualityItem);
            } else {
                inv.setItem(16, createInfoItem(sexuality, "<gradient:FF0000,FFFF00>Sexualität</gradient>"));
            }
        } else {
            inv.setItem(16, createInfoItem("§cNicht festgelegt", "<gradient:FF0000,FFFF00>Sexualität</gradient>"));
        }
        
        inv.setItem(28, createInfoItem(profile.getDescription().isEmpty() ? "§cNicht festgelegt" : profile.getDescription(), "<gradient:00FF00,00AA00>Beschreibung</gradient>"));
        inv.setItem(30, createInfoItem(profile.getPronouns().isEmpty() ? "§cNicht festgelegt" : profile.getPronouns(), "<gradient:FF69B4,FF1493>Pronomen</gradient>"));
        inv.setItem(32, createInfoItem(profile.getCountry().isEmpty() ? "§cNicht festgelegt" : profile.getCountry(), "<gradient:00AA00,00FF00>Land</gradient>"));
        inv.setItem(34, createInfoItem(profile.getLanguages().isEmpty() ? "§cNicht festgelegt" : String.join(", ", profile.getLanguages()), "<gradient:FFFF00,FF5555>Sprachen</gradient>"));
        inv.setItem(38, createInfoItem(profile.getFormattedPlaytime(), "<gradient:FFD700,FFA500>Spielzeit</gradient>"));
        inv.setItem(40, createInfoItem(profile.getStatus().isEmpty() ? "§cKein Status gesetzt" : profile.getStatus(), "<gradient:00CED1,20B2AA>Status</gradient>"));
        
        // Socials
        List<String> socialLore = new java.util.ArrayList<>();
        for (String key : plugin.getSocialMenuSlots().values()) {
            String value = profile.getSocial(key);
            if (!value.isEmpty()) {
                socialLore.add("§b" + key + ": §a" + value);
            }
        }
        if (socialLore.isEmpty()) {
            socialLore.add("§cKeine Socials angegeben");
        }
        
        ItemStack socialItem = new ItemStack(Material.TOTEM_OF_UNDYING);
        var socialMeta = socialItem.getItemMeta();
        socialMeta.displayName(plugin.getLegacySerializer().deserialize(plugin.processGradients("<gradient:0000FF,00FFFF>Socials</gradient>")));
        List<Component> socialLoreComp = new java.util.ArrayList<>();
        for (String l : socialLore) {
            socialLoreComp.add(plugin.getLegacySerializer().deserialize(l));
        }
        socialMeta.lore(socialLoreComp);
        socialItem.setItemMeta(socialMeta);
        inv.setItem(30, socialItem);
        
        viewer.openInventory(inv);
    }
    
    private ItemStack createInfoItem(String value, String name) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        var meta = item.getItemMeta();
        meta.displayName(plugin.getLegacySerializer().deserialize(name));
        meta.lore(List.of(plugin.getLegacySerializer().deserialize("§7" + value)));
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createFillerItem(String menuPath) {
        ConfigurationSection fillerSection = plugin.getConfig().getConfigurationSection(menuPath + ".filler-item");
        if (fillerSection == null) {
            return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        }
        return plugin.createConfigItem(fillerSection, "");
    }
}
