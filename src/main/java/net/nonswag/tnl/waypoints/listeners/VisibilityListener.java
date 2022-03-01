package net.nonswag.tnl.waypoints.listeners;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.Waypoints;
import net.nonswag.tnl.waypoints.api.Waypoint;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;

public class VisibilityListener implements Listener {

    @EventHandler
    public void onWorldChange(@Nonnull PlayerChangedWorldEvent event) {
        Waypoints.getInstance().sync(() -> show(TNLPlayer.cast(event.getPlayer())), 20);
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        Waypoints.getInstance().sync(() -> show(TNLPlayer.cast(event.getPlayer())), 20);
    }

    private void show(@Nonnull TNLPlayer player) {
        for (Waypoint waypoint : Waypoint.getWaypoints(player.getUniqueId())) waypoint.show(player);
    }
}
