package net.vicnix.friends.session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionStorage implements Cloneable {

    private final String name;

    private final UUID uuid;

    private List<String> friends = new ArrayList<>();

    private List<String> requests = new ArrayList<>();
    private List<String> sentRequests = new ArrayList<>();

    protected Integer maxFriendsSlots = 0;

    private Boolean toggleRequests = true;
    private Boolean toggleNotifications = true;

    public SessionStorage(String name, UUID uuid) {
        this.name = name;

        this.uuid = uuid;
    }

    public SessionStorage(String name, UUID uuid, Integer maxFriendsSlots, Boolean toggleRequests, Boolean toggleNotifications, List<String> friends, List<String> requests, List<String> sentRequests) {
        this(name, uuid);

        this.maxFriendsSlots = maxFriendsSlots;

        this.toggleRequests = toggleRequests;

        this.toggleNotifications = toggleNotifications;

        this.friends = friends;

        this.requests = requests;

        this.sentRequests = sentRequests;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public List<String> getFriends() {
        return this.friends;
    }

    public void addFriend(UUID uuid) {
        if (this.isFriend(uuid)) return;

        this.friends.add(uuid.toString());
    }

    public void removeFriend(UUID uuid) {
        this.friends.remove(uuid.toString());
    }

    public void removeFriends() {
        List<String> friends = new ArrayList<>(this.friends);

        for (String uuid : friends) {
            Session session = SessionManager.getInstance().getOfflineSession(UUID.fromString(uuid));

            if (session == null) {
                continue;
            }

            session.getSessionStorage().removeFriend(this.uuid);

            SessionManager.getInstance().intentSave((SessionStorage) session.getSessionStorage().forceClone(), session.getMaxFriendsSlots());
        }

        this.friends = new ArrayList<>();
    }

    public Boolean isFriend(UUID uuid) {
        return this.friends.contains(uuid.toString());
    }

    public List<String> getRequests() {
        return this.requests;
    }

    public void addRequest(UUID uuid) {
        this.requests.add(uuid.toString());
    }

    public void removeRequest(UUID uuid) {
        this.requests.remove(uuid.toString());
    }

    public Boolean alreadyRequested(UUID uuid) {
        return this.requests.contains(uuid.toString());
    }

    public List<String> getSentRequests() {
        return sentRequests;
    }

    public void addSentRequest(UUID uuid) {
        if (this.alreadySentRequest(uuid)) return;

        this.sentRequests.add(uuid.toString());
    }

    public void removeSentRequest(UUID uuid) {
        if (!this.alreadySentRequest(uuid)) return;

        this.sentRequests.remove(uuid.toString());
    }

    public Boolean alreadySentRequest(UUID uuid) {
        return this.sentRequests.contains(uuid.toString());
    }

    public void toggleRequests(Boolean toggleRequests) {
        this.toggleRequests = toggleRequests;
    }

    public Boolean hasToggleRequests() {
        return this.toggleRequests;
    }

    public void toggleNotifications(Boolean toggleNotifications) {
        this.toggleNotifications = toggleNotifications;
    }

    public Boolean hasToggleNotifications() {
        return this.toggleNotifications;
    }

    public Object forceClone() {
        try {
            return this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
