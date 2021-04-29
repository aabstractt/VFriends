package net.vicnix.friends.provider;

import net.vicnix.friends.session.SessionStorage;

import java.util.UUID;

public interface IProvider {

    void init();

    void saveSessionStorage(SessionStorage sessionStorage, Integer friendsSlots);

    SessionStorage loadSessionStorage(String name);

    SessionStorage loadSessionStorage(UUID uuid);

    SessionStorage loadSessionStorage(UUID uuid, String name);
}