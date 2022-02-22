package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.listener.api.command.brigadier.BrigadierCommand;

public class WaypointCommand extends BrigadierCommand {

    public WaypointCommand() {
        super("waypoint", "tnl.waypoint");
        addSubCommand(new Teleport());
        addSubCommand(new Create());
        addSubCommand(new Delete());
        addSubCommand(new List());
    }
}
