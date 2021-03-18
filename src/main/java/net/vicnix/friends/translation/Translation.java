package net.vicnix.friends.translation;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.session.SessionPermission;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Translation {

    private static final Translation instance = new Translation();

    private final Map<String, String> translations = new HashMap<>();
    private final Map<String, SessionPermission> sessionPermissionMap = new HashMap<>();

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

            config = (Configuration) ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(VicnixFriends.getInstance().getDataFolder().getPath(), "config.yml")).get("permissions-session-size");

            for (String permission : config.getKeys()) {
                this.sessionPermissionMap.put(permission, new SessionPermission(permission, config.getString(permission + ".prefix"), config.getInt(permission + ".size")));
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

    public String translateServerPrefix(ServerInfo serverInfo) {
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(VicnixFriends.getInstance().getDataFolder().getPath(), "config.yml"));

            Configuration newConfig = (Configuration) config.get("servers-prefix");

            for (String prefix : newConfig.getKeys()) {
                if (serverInfo.getName().startsWith(prefix)) {
                    return newConfig.getString(prefix);
                }
            }

            return config.getString("server-unknown");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    public SessionPermission getSessionPermission(ProxiedPlayer player) {
        SessionPermission betterSessionPermission = this.sessionPermissionMap.get("default-party");

        if (player == null) {
            ProxyServer.getInstance().getLogger().info("Player not found");

            return betterSessionPermission;
        }

        for (SessionPermission sessionPermission : this.sessionPermissionMap.values()) {
            if (betterSessionPermission == null) {
                betterSessionPermission = sessionPermission;
            }

            if (!player.hasPermission(sessionPermission.getName())) continue;

            if (sessionPermission.getSize() >= betterSessionPermission.getSize()) {
                betterSessionPermission = sessionPermission;
            }
        }

        return betterSessionPermission;
    }

    public String translatePrefix(Session session) {
        SessionPermission sessionPermission = this.getSessionPermission(session.getInstance());

        return ChatColor.translateAlternateColorCodes('&', sessionPermission.getPrefix().replace("{name}", session.getName()));
    }

    private void saveDefaultConfig() {
        if (!VicnixFriends.getInstance().getDataFolder().exists()) {
            VicnixFriends.getInstance().getDataFolder().mkdir();
        }

        for (String fileName : new String[] {"translations", "config"}) {
            File file = new File(VicnixFriends.getInstance().getDataFolder().getPath(), fileName + ".yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();

                    try (InputStream is = VicnixFriends.getInstance().getResourceAsStream(fileName + ".yml");
                         OutputStream os = new FileOutputStream(file)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}