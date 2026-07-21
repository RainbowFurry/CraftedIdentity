package net.rainbowfurry.craftedidentity.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.rainbowfurry.craftedidentity.CraftedIdentity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {
    private final CraftedIdentity plugin;
    private final Map<UUID, PlayerProfile> profiles;
    private final Gson gson;
    private final File profilesDir;

    public ProfileManager(CraftedIdentity plugin) {
        this.plugin = plugin;
        this.profiles = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.profilesDir = new File(plugin.getDataFolder(), "profiles");
        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
        }
    }

    public PlayerProfile getProfile(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }
        PlayerProfile profile = loadProfile(uuid);
        profiles.put(uuid, profile);
        return profile;
    }

    private PlayerProfile loadProfile(UUID uuid) {
        File profileFile = new File(profilesDir, uuid.toString() + ".json");
        if (profileFile.exists()) {
            try (Reader reader = new FileReader(profileFile)) {
                return gson.fromJson(reader, PlayerProfile.class);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to load profile for " + uuid);
                e.printStackTrace();
            }
        }
        return new PlayerProfile(uuid);
    }

    public void saveProfile(UUID uuid) {
        PlayerProfile profile = profiles.get(uuid);
        if (profile == null) return;
        File profileFile = new File(profilesDir, uuid.toString() + ".json");
        try (Writer writer = new FileWriter(profileFile)) {
            gson.toJson(profile, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save profile for " + uuid);
            e.printStackTrace();
        }
    }

    public void saveAllProfiles() {
        for (UUID uuid : profiles.keySet()) {
            saveProfile(uuid);
        }
    }
}
