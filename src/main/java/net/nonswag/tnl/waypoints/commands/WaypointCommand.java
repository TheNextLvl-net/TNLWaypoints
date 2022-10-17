package net.nonswag.tnl.waypoints.commands;

import net.nonswag.core.api.command.CommandSource;
import net.nonswag.tnl.listener.api.command.simple.SimpleCommand;

import javax.annotation.Nonnull;

public class WaypointCommand extends SimpleCommand {

    public WaypointCommand() {
        super("waypoint", "tnl.waypoint");
        addSubCommand(new Teleport());
        addSubCommand(new Create());
        addSubCommand(new Delete());
        addSubCommand(new Color());
        addSubCommand(new List());
    }

    @Override
    public boolean canUse(@Nonnull CommandSource source) {
        return source.isPlayer();
    }
}
