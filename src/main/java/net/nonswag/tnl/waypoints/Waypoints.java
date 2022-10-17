package net.nonswag.tnl.waypoints;

import net.nonswag.tnl.listener.Listener;
import net.nonswag.tnl.listener.api.plugin.PluginUpdate;
import net.nonswag.tnl.listener.api.plugin.TNLPlugin;
import net.nonswag.tnl.listener.api.settings.Settings;
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
        Listener.getOnlinePlayers().forEach(all -> Waypoint.getWaypoints(all.getUniqueId()).forEach(waypoint -> waypoint.show(all)));
        async(() -> {
            if (Settings.AUTO_UPDATER.getValue()) new PluginUpdate(this).downloadUpdate();
        });
    }
    @Override
    public void disable() {
        Listener.getOnlinePlayers().forEach(all -> Waypoint.getWaypoints(all.getUniqueId()).forEach(waypoint -> waypoint.hide(all)));
        Waypoint.exportAll();
    }

    @Nonnull
    public static Waypoints getInstance() {
        assert instance != null;
        return instance;
    }
}
