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
        
        String title = plugin.getConfig().getString("view-profile-menu.title", "§8» §bProfil von %player%");
        title = title.replace("%player%", target.getName());
        int size = plugin.getConfig().getInt("view-profile-menu.size", 54);
        
        Inventory inv = Bukkit.createInventory(null, size, plugin.getLegacySerializer().deserialize(title));
        
        // Füller Items
        ItemStack filler = createFillerItem("view-profile-menu");
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }
        
        // Spieler Kopf
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(plugin.getLegacySerializer().deserialize("§6" + target.getName()));
        head.setItemMeta(meta);
        inv.setItem(4, head);
        
        // Profil Info Items
        inv.setItem(10, createInfoItem(profile.getRealName().isEmpty() ? "§cNicht festgelegt" : profile.getRealName(), "§6Echter Name"));
        inv.setItem(12, createInfoItem(profile.getAge() == 0 ? "§cNicht festgelegt" : String.valueOf(profile.getAge()), "§6Alter"));
        inv.setItem(14, createInfoItem(profile.getGender().isEmpty() ? "§cNicht festgelegt" : profile.getGender(), "§6Geschlecht"));
        inv.setItem(16, createInfoItem(profile.getSexuality().isEmpty() ? "§cNicht festgelegt" : profile.getSexuality(), "§6Sexualität"));
        inv.setItem(28, createInfoItem(profile.getDescription().isEmpty() ? "§cNicht festgelegt" : profile.getDescription(), "§6Beschreibung"));
        inv.setItem(32, createInfoItem(profile.getCountry().isEmpty() ? "§cNicht festgelegt" : profile.getCountry(), "§6Land"));
        inv.setItem(34, createInfoItem(profile.getLanguages().isEmpty() ? "§cNicht festgelegt" : String.join(", ", profile.getLanguages()), "§6Sprachen"));
        
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
        socialMeta.displayName(plugin.getLegacySerializer().deserialize("§6Socials"));
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
