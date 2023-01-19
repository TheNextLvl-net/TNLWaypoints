package net.nonswag.tnl.waypoints.commands;

import net.nonswag.core.api.command.CommandSource;
import net.nonswag.tnl.listener.api.command.simple.SimpleCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;

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
    public boolean canUse(CommandSource source) {
        return source instanceof TNLPlayer;
    }
}
