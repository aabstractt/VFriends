package net.vicnix.friends.translation;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vicnix.friends.VicnixFriends;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translation {

    private static final Translation instance = new Translation();

    private Map<String, String> translations = new HashMap<>();

    public static Translation getInstance() {
        return instance;
    }

    public void init() {
        saveDefaultConfig();

        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(VicnixFriends.getInstance().getDataFolder().getPath(), "translations.yml"));

            for (String key : config.getKeys()) {
                this.translations.put(key, config.getString(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String translateString(String key, String... args) {
        String text = this.translations.getOrDefault(key, null);

        if (text == null) {
            return key + "<" + Arrays.toString(args) + ">";
        }

        for (int i = 0; i < args.length; i++) {
            text = text.replace("{" + i + "}", args[i]);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void saveDefaultConfig() {
        if (!VicnixFriends.getInstance().getDataFolder().exists()) {
            VicnixFriends.getInstance().getDataFolder().mkdir();
        }
        File file = new File(VicnixFriends.getInstance().getDataFolder().getPath(), "translations.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream is = VicnixFriends.getInstance().getResourceAsStream("translations.yml");
                     OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}