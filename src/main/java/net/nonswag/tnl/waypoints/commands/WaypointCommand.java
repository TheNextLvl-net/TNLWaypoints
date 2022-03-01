package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.listener.api.command.brigadier.BrigadierCommand;

import javax.annotation.Nonnull;

public class WaypointCommand extends BrigadierCommand {

    public WaypointCommand() {
        super("waypoint", "tnl.waypoint");
        addSubCommand(new Teleport());
        addSubCommand(new Create());
        addSubCommand(new Delete());
        addSubCommand(new List());
    }

    @Override
    public boolean canUse(@Nonnull CommandSource source) {
        return source.isPlayer();
    }
}
