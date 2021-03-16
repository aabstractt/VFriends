package net.vicnix.friends.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.vicnix.friends.session.Session;

import java.util.ArrayList;
import java.util.List;

public abstract class FriendSubCommand {

    public abstract void execute(Session session, String[] args);

    /**
     * Gets the Annotation of a PartyAnnotationCommand class.
     *
     * @return Annotation if it exists, null if invalid
     */
    public FriendAnnotationCommand getAnnotations() {
        if (this.getClass().isAnnotationPresent(FriendAnnotationCommand.class)) {
            return this.getClass().getAnnotation(FriendAnnotationCommand.class);
        }

        return null;
    }

    public List<String> getComplete(ProxiedPlayer player, String[] args) {
        return new ArrayList<>();
    }
}