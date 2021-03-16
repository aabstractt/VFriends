package net.vicnix.friends.provider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.VicnixFriends;
import net.vicnix.friends.session.Session;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class MongoDBProvider implements IProvider {

    private MongoCollection<Document> friendsCollection;

    @Override
    public void init() {
        MongoClient mongoClient = new MongoClient();

        MongoDatabase database = mongoClient.getDatabase("VicnixFriends");

        this.friendsCollection = database.getCollection("friends");
    }

    public void saveSession(Session session) {
        ProxyServer.getInstance().getScheduler().runAsync(VicnixFriends.getInstance(), ()-> {
            Document document = this.friendsCollection.find(Filters.eq("uuid", session.getUuid().toString())).first();

            if (document == null || document.isEmpty()) {
                this.friendsCollection.insertOne(this.toDocument(session));
            } else {
                this.friendsCollection.findOneAndReplace(Filters.eq("uuid", session.getUuid().toString()), this.toDocument(session));
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Session loadSession(String name) {
        Document document = this.friendsCollection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return new Session(document.getString("name"), UUID.fromString(document.getString("uuid")), (List<String>)document.get("friends"), (List<String>)document.get("requests"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Session loadSession(UUID uuid) {
        Document document = this.friendsCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }

        return new Session(document.getString("name"), uuid, (List<String>)document.get("friends"), (List<String>)document.get("requests"));
    }

    private Document toDocument(Session session) {
        return new Document("uuid", session.getUuid().toString()).append("name", session.getName()).append("friends", session.getFriends()).append("requests", session.getRequests());
    }
}
