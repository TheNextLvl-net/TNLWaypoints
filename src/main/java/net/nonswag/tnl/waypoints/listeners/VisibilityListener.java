package net.nonswag.tnl.waypoints.listeners;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.Waypoints;
import net.nonswag.tnl.waypoints.api.Waypoint;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VisibilityListener implements Listener {

    @EventHandler
    public void onWorldChange(@Nonnull PlayerChangedWorldEvent event) {
        showDelayed(event.getPlayer(), null);
    }

    @EventHandler
    public void onChunkLoad(@Nonnull PlayerChunkLoadEvent event) {
        showDelayed(event.getPlayer(), event.getChunk());
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        showDelayed(event.getPlayer(), null);
    }

    private void showDelayed(@Nonnull Player player, @Nullable Chunk chunk) {
        showDelayed(TNLPlayer.cast(player), chunk);
    }

    private void showDelayed(@Nonnull TNLPlayer player, @Nullable Chunk chunk) {
        List<Waypoint> waypoints;
        if (chunk == null) waypoints = Waypoint.getWaypoints(player.getUniqueId());
        else waypoints = Waypoint.getWaypoints(player.getUniqueId(), chunk);
        if (waypoints.isEmpty()) return;
        Waypoints.getInstance().sync(() -> waypoints.forEach(waypoint -> waypoint.show(player)), 20);
    }
}
