package net.nonswag.tnl.waypoints.commands;

import net.nonswag.core.api.command.CommandSource;
import net.nonswag.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.PlayerSubCommand;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;

class Create extends PlayerSubCommand {

    Create() {
        super("create");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        TNLPlayer player = (TNLPlayer) source.player();
        if (args.length < 2) throw new InvalidUseException(this);
        Waypoint waypoint = Waypoint.getWaypoint(player.getUniqueId(), args[1]);
        if (waypoint == null) {
            BlockLocation location = new BlockLocation(player.worldManager().getLocation());
            waypoint = new Waypoint(player.getUniqueId(), args[1], location, Waypoint.Color.WHITE).register();
            source.sendMessage("%prefix% §aCreated new waypoint named §6" + waypoint.show(player));
        } else source.sendMessage("%prefix% §cA waypoint named §4" + waypoint + "§c does already exist");
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        invocation.source().sendMessage("%prefix% §c/waypoint create §8[§6Name§8]");
    }
}
