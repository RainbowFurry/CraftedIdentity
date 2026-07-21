package net.rainbowfurry.craftedidentity.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerProfile {
    private final UUID uuid;
    private String realName;
    private int age;
    private String gender;
    private String sexuality;
    private String description;
    private final Map<String, String> socials;
    private String country;
    private final List<String> languages;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.realName = "";
        this.age = 0;
        this.gender = "";
        this.sexuality = "";
        this.description = "";
        this.socials = new HashMap<>();
        this.country = "";
        this.languages = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSexuality() {
        return sexuality;
    }

    public void setSexuality(String sexuality) {
        this.sexuality = sexuality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getSocials() {
        return socials;
    }

    public void setSocial(String platform, String value) {
        this.socials.put(platform, value);
    }

    public String getSocial(String platform) {
        return this.socials.getOrDefault(platform, "");
    }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public List<String> getLanguages() { return languages; }
    public void addLanguage(String language) { this.languages.add(language); }
    public void removeLanguage(String language) { this.languages.remove(language); }
    public void clearLanguages() { this.languages.clear(); }
}
