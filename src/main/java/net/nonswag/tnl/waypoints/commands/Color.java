package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.brigadier.PlayerSubCommand;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Color extends PlayerSubCommand {

    protected Color() {
        super("color");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        Waypoint waypoint = Waypoint.getWaypoint(player.getUniqueId(), args[1]);
        if (waypoint == null) throw new InvalidUseException(this);
        if (args.length >= 3) {
            Waypoint.Color color = Waypoint.Color.getColor(args[2]);
            if (color != null) {
                waypoint.setColor(color);
                player.messenger().sendMessage("%prefix% §aChanged the waypoint's color to §6" + color);
            } else player.messenger().sendMessage("%prefix% §c/waypoint color " + waypoint + " §8[§6Color§8]");
        } else player.messenger().sendMessage("%prefix% §7Color §8(§a" + waypoint + "§8): §6" + waypoint.getColor());
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Waypoint waypoint : Waypoint.getWaypoints(player.getUniqueId())) suggestions.add(waypoint.getName());
        } else if (args.length == 3) {
            for (Waypoint.Color color : Waypoint.Color.values()) suggestions.add(color.getName());
        }
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        invocation.source().sendMessage("%prefix% §c/waypoint color §8[§6Waypoint§8] §8(§6Color§8)");
    }
}
