package net.nonswag.tnl.waypoints.commands;

import net.nonswag.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.PlayerSubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Delete extends PlayerSubCommand {

    Delete() {
        super("delete");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        Waypoint waypoint = Waypoint.getWaypoint(player.getUniqueId(), args[1]);
        if (waypoint == null) throw new InvalidUseException(this);
        player.messenger().sendMessage("%prefix% §aDeleted the waypoint §6" + waypoint.getName());
        waypoint.hide(player).unregister();
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length != 2) return suggestions;
        Waypoint.getWaypoints(player.getUniqueId()).forEach(waypoint -> suggestions.add(waypoint.getName()));
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        invocation.source().sendMessage("%prefix% §c/waypoint delete §8[§6Waypoint§8]");
    }
}
