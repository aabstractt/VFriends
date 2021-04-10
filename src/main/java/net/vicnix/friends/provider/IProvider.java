package net.vicnix.friends.provider;

import net.vicnix.friends.session.Session;
import org.bson.Document;

import java.util.UUID;

public interface IProvider {

    void init();

    void saveSession(UUID uuid, Document newDocument);

    Session loadSession(String name);

    Session loadSession(UUID uuid);

    Session loadSession(UUID uuid, String name);
}