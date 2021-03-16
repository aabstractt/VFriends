package net.vicnix.friends.provider;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
        Document document = this.friendsCollection.find(Filters.eq("uuid", session.getUniqueId().toString())).first();

        Document newDocument = new Document("uuid", session.getUniqueId().toString())
                .append("name", session.getName())
                .append("friends", session.getFriends())
                .append("requests", session.getRequests())
                .append("sentRequests", session.getSentRequests());

        if (document == null || document.isEmpty()) {
            this.friendsCollection.insertOne(newDocument);
        } else {
            this.friendsCollection.findOneAndReplace(Filters.eq("uuid", session.getUniqueId().toString()), newDocument);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Session loadSession(String name) {
        Document document = this.friendsCollection.find(Filters.eq("name", name)).first();

        if (document == null) {
            return null;
        }

        return new Session(document.getString("name"), UUID.fromString(document.getString("uuid")), (List<String>)document.get("friends"), (List<String>)document.get("requests"), (List<String>)document.get("sentRequests"));
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

        return new Session(name, uuid, (List<String>)document.get("friends"), (List<String>)document.get("requests"), (List<String>)document.get("sentRequests"));
    }
}
