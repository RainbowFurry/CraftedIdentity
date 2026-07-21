package net.rainbowfurry.craftedidentity.profile;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    private final UUID uuid;
    private String realName;
    private String birthDate; // Format: DD.MM.YYYY
    private String gender;
    private String sexuality;
    private String description;
    private Map<String, String> socials;
    private String country;
    private List<String> languages;
    private List<UUID> friends; // Stores friend UUIDs
    private String pronouns; // Pronomen (z.B. "er/ihm", "sie/ihr", "dey/denen")
    private long totalPlaytime; // Gesamtspielzeit in Millisekunden
    private long lastJoinTime; // Letzter Join-Timestamp in Millisekunden
    private String status; // Custom-Status (z.B. "Baut gerade!")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.realName = "";
        this.birthDate = "";
        this.gender = "";
        this.sexuality = "";
        this.description = "";
        this.socials = new HashMap<>();
        this.country = "";
        this.languages = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.pronouns = "";
        this.totalPlaytime = 0;
        this.lastJoinTime = 0;
        this.status = "";
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getRealName() {
        return realName != null ? realName : "";
    }

    public void setRealName(String realName) {
        this.realName = realName != null ? realName : "";
    }

    public String getBirthDate() {
        return birthDate != null ? birthDate : "";
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate != null ? birthDate : "";
    }

    public int getAge() {
        String bd = getBirthDate();
        if (bd.isEmpty()) {
            return 0;
        }
        try {
            LocalDate birth = LocalDate.parse(bd, DATE_FORMATTER);
            LocalDate now = LocalDate.now();
            return Period.between(birth, now).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getGender() {
        return gender != null ? gender : "";
    }

    public void setGender(String gender) {
        this.gender = gender != null ? gender : "";
    }

    public String getSexuality() {
        return sexuality != null ? sexuality : "";
    }

    public void setSexuality(String sexuality) {
        this.sexuality = sexuality != null ? sexuality : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public Map<String, String> getSocials() {
        if (socials == null) {
            return new HashMap<>();
        }
        return socials;
    }

    public void setSocial(String platform, String value) {
        getSocials(); // Sicherstellen, dass socials initialisiert ist
        this.socials.put(platform, value != null ? value : "");
    }

    public String getSocial(String platform) {
        return getSocials().getOrDefault(platform, "");
    }
    
    public String getCountry() { return country != null ? country : ""; }
    public void setCountry(String country) { this.country = country != null ? country : ""; }
    
    public List<String> getLanguages() { 
        if (languages == null) {
            languages = new ArrayList<>();
        }
        return languages; 
    }
    public void addLanguage(String language) { 
        getLanguages(); // Sicherstellen, dass languages initialisiert ist
        this.languages.add(language); 
    }
    public void removeLanguage(String language) { 
        getLanguages(); // Sicherstellen, dass languages initialisiert ist
        this.languages.remove(language); 
    }
    public void clearLanguages() { 
        getLanguages(); // Sicherstellen, dass languages initialisiert ist
        this.languages.clear(); 
    }
    
    public List<UUID> getFriends() { 
        if (friends == null) {
            friends = new ArrayList<>();
        }
        return friends; 
    }
    public void addFriend(UUID friendUuid) { 
        getFriends(); // Sicherstellen, dass friends initialisiert ist
        if (!this.friends.contains(friendUuid)) {
            this.friends.add(friendUuid); 
        }
    }
    public void removeFriend(UUID friendUuid) { 
        getFriends(); // Sicherstellen, dass friends initialisiert ist
        this.friends.remove(friendUuid); 
    }
    public boolean hasFriend(UUID friendUuid) {
        getFriends(); // Sicherstellen, dass friends initialisiert ist
        return this.friends.contains(friendUuid);
    }
    public void clearFriends() { 
        getFriends(); // Sicherstellen, dass friends initialisiert ist
        this.friends.clear(); 
    }
    
    public String getPronouns() { return pronouns != null ? pronouns : ""; }
    public void setPronouns(String pronouns) { this.pronouns = pronouns != null ? pronouns : ""; }
    
    public long getTotalPlaytime() { return totalPlaytime; }
    public void setTotalPlaytime(long totalPlaytime) { this.totalPlaytime = totalPlaytime; }
    public void addPlaytime(long playtime) { this.totalPlaytime += playtime; }
    
    public long getLastJoinTime() { return lastJoinTime; }
    public void setLastJoinTime(long lastJoinTime) { this.lastJoinTime = lastJoinTime; }
    
    public String getStatus() { return status != null ? status : ""; }
    public void setStatus(String status) { this.status = status != null ? status : ""; }
    
    // Hilfsmethode, um Playtime lesbar zu machen (z.B. "1 Tag, 5 Stunden, 30 Minuten")
    public String getFormattedPlaytime() {
        long totalSeconds = totalPlaytime / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" Tag").append(days == 1 ? "" : "e").append(", ");
        if (hours > 0) sb.append(hours).append(" Stunde").append(hours == 1 ? "" : "n").append(", ");
        if (minutes > 0) sb.append(minutes).append(" Minute").append(minutes == 1 ? "" : "n").append(", ");
        sb.append(seconds).append(" Sekunde").append(seconds == 1 ? "" : "n");
        
        return sb.toString();
    }
}
