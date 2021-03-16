package net.vicnix.friends.provider;

import net.vicnix.friends.session.Session;

import java.util.UUID;

public interface IProvider {

    void init();

    void saveSession(Session session);

    Session loadSession(String name);

    Session loadSession(UUID uuid);
}