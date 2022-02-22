package net.nonswag.tnl.waypoints;

import net.nonswag.tnl.listener.api.plugin.PluginUpdate;
import net.nonswag.tnl.listener.api.plugin.TNLPlugin;
import net.nonswag.tnl.waypoints.api.Waypoint;
import net.nonswag.tnl.waypoints.commands.WaypointCommand;
import net.nonswag.tnl.waypoints.listeners.VisibilityListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Waypoints extends TNLPlugin {

    @Nullable
    private static Waypoints instance = null;

    @Override
    public void enable() {
        instance = this;
        Waypoint.loadAll();
        getCommandManager().registerCommand(new WaypointCommand());
        getEventManager().registerListener(new VisibilityListener());
        async(() -> getUpdater().update());
    }

    @Nonnull
    @Override
    public PluginUpdate getUpdater() {
        return updater == null ? updater = new PluginUpdate(this) : updater;
    }

    @Override
    public void disable() {
        Waypoint.exportAll();
    }

    @Nonnull
    public static Waypoints getInstance() {
        assert instance != null;
        return instance;
    }
}
