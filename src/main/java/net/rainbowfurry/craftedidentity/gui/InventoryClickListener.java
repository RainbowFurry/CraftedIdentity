package net.rainbowfurry.craftedidentity.gui;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rainbowfurry.craftedidentity.CraftedIdentity;
import net.rainbowfurry.craftedidentity.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryClickListener implements Listener {
    private final CraftedIdentity plugin;
    private final Map<UUID, String> editingState;
    private final LegacyComponentSerializer legacySerializer;

    public InventoryClickListener(CraftedIdentity plugin) {
        this.plugin = plugin;
        this.editingState = new HashMap<>();
        this.legacySerializer = LegacyComponentSerializer.legacySection();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.CHEST) return;
        
        String title = legacySerializer.serialize(event.getView().title());
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        String mainTitle = plugin.getConfig().getString("main-menu.title", "§8» §bDein Profil");
        String socialTitle = plugin.getConfig().getString("socials-menu.title", "§8» §bSocials");
        String genderTitle = plugin.getConfig().getString("gender-menu.title", "§8» §bGeschlecht wählen");
        String sexualityTitle = plugin.getConfig().getString("sexuality-menu.title", "§8» §bSexualität wählen");
        
        // Hauptmenü
        if (title.equals(mainTitle)) {
            int slot = event.getSlot();
            String key = plugin.getMainMenuSlots().get(slot);
            if (key == null) return;
            
            switch (key) {
                case "realname" -> startEditing(player, "realName");
                case "age" -> startEditing(player, "age");
                case "gender" -> new GenderSelectionMenu(plugin).open(player);
                case "sexuality" -> new SexualitySelectionMenu(plugin).open(player);
                case "description" -> startEditing(player, "description");
                case "socials" -> new SocialsMenu(plugin).open(player);
                case "country" -> startEditing(player, "country");
                case "languages" -> startEditing(player, "language");
            }
            return;
        }
        
        // Socials Menü
        if (title.equals(socialTitle)) {
            int slot = event.getSlot();
            
            // Back Button
            ConfigurationSection backSection = plugin.getConfig().getConfigurationSection("socials-menu.back-item");
            if (backSection != null && slot == backSection.getInt("slot")) {
                new MainProfileMenu(plugin).open(player);
                return;
            }
            
            // Social Plattform
            String key = plugin.getSocialMenuSlots().get(slot);
            if (key != null) {
                startEditing(player, "social_" + key);
            }
            return;
        }
        
        // Geschlecht Menü
        if (title.equals(genderTitle)) {
            if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
            
            int slot = event.getSlot();
            String key = plugin.getGenderMenuSlots().get(slot);
            if (key == null) return;
            
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            profile.setGender(key);
            plugin.getProfileManager().saveProfile(player.getUniqueId());
            player.sendMessage("§aDein Geschlecht wurde auf §6" + key + " §agesetzt!");
            new MainProfileMenu(plugin).open(player);
            return;
        }
        
        // Sexualität Menü
        if (title.equals(sexualityTitle)) {
            if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
            
            int slot = event.getSlot();
            String key = plugin.getSexualityMenuSlots().get(slot);
            if (key == null) return;
            
            PlayerProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            profile.setSexuality(key);
            plugin.getProfileManager().saveProfile(player.getUniqueId());
            player.sendMessage("§aDeine Sexualität wurde auf §6" + key + " §agesetzt!");
            new MainProfileMenu(plugin).open(player);
            return;
        }
    }

    private void startEditing(Player player, String state) {
        editingState.put(player.getUniqueId(), state);
        player.closeInventory();
        player.sendMessage("§a§lGib nun den Wert ein (oder '§ccancel§a' zum Abbrechen):");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!editingState.containsKey(uuid)) return;
        
        event.setCancelled(true);
        String message = event.getMessage();
        
        if (message.equalsIgnoreCase("cancel")) {
            editingState.remove(uuid);
            player.sendMessage("§cBearbeitung abgebrochen!");
            new MainProfileMenu(plugin).open(player);
            return;
        }
        
        PlayerProfile profile = plugin.getProfileManager().getProfile(uuid);
        String state = editingState.get(uuid);
        
        switch (state) {
            case "realName" -> profile.setRealName(message);
            case "age" -> {
                try {
                    int age = Integer.parseInt(message);
                    profile.setAge(age);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cBitte gib eine gültige Zahl ein!");
                    return;
                }
            }
            case "description" -> profile.setDescription(message);
            case "country" -> profile.setCountry(message);
            case "language" -> {
                if (!message.trim().isEmpty()) {
                    profile.addLanguage(message.trim());
                    player.sendMessage("§aSprache '" + message.trim() + "' hinzugefügt!");
                }
            }
            default -> {
                if (state.startsWith("social_")) {
                    String platform = state.substring(7);
                    profile.setSocial(platform, message);
                }
            }
        }
        
        plugin.getProfileManager().saveProfile(uuid);
        if (!state.equals("language")) {
            editingState.remove(uuid);
            player.sendMessage("§aWert erfolgreich geändert!");
            new MainProfileMenu(plugin).open(player);
        } else {
            player.sendMessage("§7Gib eine weitere Sprache ein (oder 'cancel' zum Beenden)!");
        }
    }
}
