package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.brigadier.SubCommand;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

class List extends SubCommand {

    List() {
        super("list");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        Collection<Waypoint> waypoints = Waypoint.WAYPOINTS.values();
        if (!waypoints.isEmpty()) {
            java.util.List<String> names = new ArrayList<>();
            for (Waypoint waypoint : waypoints) names.add(waypoint.getName());
            source.sendMessage("%prefix% §7Waypoints §8(§a" + names.size() + "§8): §6" + String.join("§8, §6", names));
        } else source.sendMessage("%prefix% §cThere are no waypoints");
    }
}
