package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.brigadier.SubCommand;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Delete extends SubCommand {

    Delete() {
        super("delete");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        Waypoint waypoint = Waypoint.get(args[1]);
        if (waypoint != null) {
            waypoint.unregister();
            waypoint.hideAll();
            source.sendMessage("%prefix% §aDeleted the waypoint §6" + waypoint.getName());
        } else throw new InvalidUseException(this);
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) suggestions.addAll(Waypoint.WAYPOINTS.keySet());
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        source.sendMessage("%prefix% §c/waypoint delete §8[§6Waypoint§8]");
    }
}
