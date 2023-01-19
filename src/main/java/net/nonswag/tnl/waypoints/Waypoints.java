package net.nonswag.tnl.waypoints;

import net.nonswag.core.api.annotation.FieldsAreNullableByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.tnl.listener.Listener;
import net.nonswag.tnl.listener.api.plugin.TNLPlugin;
import net.nonswag.tnl.waypoints.api.Waypoint;
import net.nonswag.tnl.waypoints.commands.WaypointCommand;
import net.nonswag.tnl.waypoints.listeners.VisibilityListener;

@FieldsAreNullableByDefault
@MethodsReturnNonnullByDefault
public class Waypoints extends TNLPlugin {
    private static Waypoints instance = null;

    @Override
    public void enable() {
        instance = this;
        Waypoint.loadAll();
        getCommandManager().registerCommand(new WaypointCommand());
        getEventManager().registerListener(new VisibilityListener());
        Listener.getOnlinePlayers().forEach(all -> Waypoint.getWaypoints(all.getUniqueId()).forEach(waypoint -> waypoint.show(all)));
    }

    @Override
    public void disable() {
        Listener.getOnlinePlayers().forEach(all -> Waypoint.getWaypoints(all.getUniqueId()).forEach(waypoint -> waypoint.hide(all)));
        Waypoint.exportAll();
    }

    public static Waypoints getInstance() {
        assert instance != null;
        return instance;
    }
}
