package net.vicnix.friends.provider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.translation.Translation;
import org.bson.Document;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MongoDBProvider implements IProvider {

    private MongoCollection<Document> friendsCollection;

    @Override
    public void init() {
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(VicnixFriends.getInstance().getDataFolder().getPath(), "config.yml"));

            MongoClient mongoClient = new MongoClient();//new MongoClientURI(config.getString("mongouri")));

            MongoDatabase database = mongoClient.getDatabase("VicnixCore");

            this.friendsCollection = database.getCollection("friends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSession(UUID uuid, Document newDocument) {
        Document document = this.friendsCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null || document.isEmpty()) {
            this.friendsCollection.insertOne(newDocument);
        } else {
            this.friendsCollection.findOneAndReplace(Filters.eq("uuid", uuid.toString()), newDocument);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Session loadSession(String name) {
        Document document = this.friendsCollection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return new Session(
                document.getString("name"),
                UUID.fromString(document.getString("uuid")),
                (List<String>)document.get("friends"),
                (List<String>)document.get("requests"),
                (List<String>)document.get("sentRequests"),
                document.getInteger("maxFriendsSlots", Translation.getInstance().getSessionPermission().getSize()),
                document.getBoolean("toggleRequests", true),
                document.getBoolean("toggleNotifications", true)
        );
    }

    public Session loadSession(UUID uuid) {
        return this.loadSession(uuid, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Session loadSession(UUID uuid, String name) {
        Document document = this.friendsCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }

        if (name == null) {
            name = document.getString("name");
        }

        return new Session(
                name,
                uuid,
                (List<String>)document.get("friends"),
                (List<String>)document.get("requests"),
                (List<String>)document.get("sentRequests"),
                document.getInteger("maxFriendsSlots", Translation.getInstance().getSessionPermission().getSize()),
                document.getBoolean("toggleRequests", true),
                document.getBoolean("toggleNotifications", true)
        );
    }
}