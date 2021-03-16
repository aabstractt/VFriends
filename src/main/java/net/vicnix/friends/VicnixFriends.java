package net.vicnix.friends;

import net.md_5.bungee.api.plugin.Plugin;
import net.vicnix.friends.command.FriendsCommand;
import net.vicnix.friends.listener.ServerConnectedListener;
import net.vicnix.friends.listener.ServerDisconnectListener;
import net.vicnix.friends.provider.IProvider;
import net.vicnix.friends.provider.MongoDBProvider;
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
        this.getProxy().getPluginManager().registerListener(this, new ServerConnectedListener());
        this.getProxy().getPluginManager().registerListener(this, new ServerDisconnectListener());
    }
}