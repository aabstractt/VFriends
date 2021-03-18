package net.vicnix.friends;

import net.md_5.bungee.api.plugin.Plugin;
import net.vicnix.friends.command.FriendMessageCommand;
import net.vicnix.friends.command.FriendReplyCommand;
import net.vicnix.friends.command.FriendsCommand;
import net.vicnix.friends.listener.PlayerDisconnectListener;
import net.vicnix.friends.listener.PostLoginListener;
import net.vicnix.friends.provider.IProvider;
import net.vicnix.friends.provider.MongoDBProvider;
import net.vicnix.friends.session.Session;
import net.vicnix.friends.translation.Translation;

public class VicnixFriends extends Plugin {

    private static VicnixFriends instance;

    private IProvider provider;

    public static VicnixFriends getInstance() {
        return instance;
    }

    public IProvider getProvider() {
        return provider;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.provider = new MongoDBProvider();

        this.provider.init();

        Translation.getInstance().init();

        this.getProxy().getPluginManager().registerCommand(this, new FriendsCommand());
        this.getProxy().getPluginManager().registerCommand(this, new FriendMessageCommand());
        this.getProxy().getPluginManager().registerCommand(this, new FriendReplyCommand());

        this.getProxy().getPluginManager().registerListener(this, new PostLoginListener());
        this.getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());
    }

    public Integer getMaxFriendsSlots(Session session) {
        return 21;
    }
}