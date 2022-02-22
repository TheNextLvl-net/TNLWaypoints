package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.brigadier.PlayerSubCommand;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.player.manager.WorldManager;
import net.nonswag.tnl.waypoints.api.Waypoint;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Teleport extends PlayerSubCommand {

    Teleport() {
        super("teleport", null, "tp");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        Waypoint waypoint = Waypoint.get(args[1]);
        TNLPlayer player = (TNLPlayer) source.player();
        if (waypoint != null) {
            WorldManager manager = player.worldManager();
            Location location = waypoint.getLocation().clone().add(0.5, 0, 0.5);
            location.setYaw(manager.getLocation().getYaw());
            location.setPitch(manager.getLocation().getPitch());
            manager.teleport(location);
            source.sendMessage("%prefix% §aTeleported to waypoint §6" + waypoint.getName());
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
        source.sendMessage("%prefix% §c/waypoint teleport §8[§6Waypoint§8]");
    }
}
