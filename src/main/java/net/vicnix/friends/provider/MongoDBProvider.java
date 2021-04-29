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
import net.vicnix.friends.session.SessionStorage;
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

            String mongouri = config.getString("mongouri", null);

            MongoClient mongoClient;

            if (mongouri == null || mongouri.equals("")) {
                mongoClient = new MongoClient();
            } else {
                mongoClient = new MongoClient(new MongoClientURI(mongouri));
            }

            MongoDatabase database = mongoClient.getDatabase("VicnixCore");

            this.friendsCollection = database.getCollection("friends");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSessionStorage(SessionStorage sessionStorage, Integer friendsSlots) {
        Document newDocument = new Document("uuid", sessionStorage.getUniqueId().toString())
                .append("name", sessionStorage.getName())
                .append("friends", sessionStorage.getFriends())
                .append("requests", sessionStorage.getRequests())
                .append("sentRequests", sessionStorage.getSentRequests())
                .append("maxFriendsSlots", friendsSlots)
                .append("toggleRequests", sessionStorage.hasToggleRequests())
                .append("toggleNotifications", sessionStorage.hasToggleNotifications());

        Document document = this.friendsCollection.find(Filters.eq("uuid", sessionStorage.getUniqueId().toString())).first();

        if (document == null || document.isEmpty()) {
            this.friendsCollection.insertOne(newDocument);
        } else {
            this.friendsCollection.findOneAndReplace(Filters.eq("uuid", sessionStorage.getUniqueId().toString()), newDocument);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public SessionStorage loadSessionStorage(String name) {
        Document document = this.friendsCollection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return new SessionStorage(
                document.getString("name"),
                UUID.fromString(document.getString("uuid")),
                document.getInteger("maxFriendsSlots", Translation.getInstance().getSessionPermission().getSize()),
                document.getBoolean("toggleRequests", true),
                document.getBoolean("toggleNotifications", true),
                (List<String>)document.get("friends"),
                (List<String>)document.get("requests"),
                (List<String>)document.get("sentRequests")
        );
    }

    public SessionStorage loadSessionStorage(UUID uuid) {
        return this.loadSessionStorage(uuid, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SessionStorage loadSessionStorage(UUID uuid, String name) {
        Document document = this.friendsCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }

        if (name == null) {
            name = document.getString("name");
        }

        return new SessionStorage(
                name,
                uuid,
                document.getInteger("maxFriendsSlots", Translation.getInstance().getSessionPermission().getSize()),
                document.getBoolean("toggleRequests", true),
                document.getBoolean("toggleNotifications", true),
                (List<String>)document.get("friends"),
                (List<String>)document.get("requests"),
                (List<String>)document.get("sentRequests")
        );
    }
}