package net.rainbowfurry.craftedidentity.commands;

import net.kyori.adventure.text.Component;
import net.rainbowfurry.craftedidentity.CraftedIdentity;
import net.rainbowfurry.craftedidentity.gui.MainProfileMenu;
import net.rainbowfurry.craftedidentity.gui.ViewProfileMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProfileCommand implements CommandExecutor {
    private final CraftedIdentity plugin;

    public ProfileCommand(CraftedIdentity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl benutzen!");
            return true;
        }

        if (args.length == 0) {
            new MainProfileMenu(plugin).open(player);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cSpieler nicht gefunden!");
                return true;
            }
            new ViewProfileMenu(plugin).open(player, target);
        }
        return true;
    }
}
