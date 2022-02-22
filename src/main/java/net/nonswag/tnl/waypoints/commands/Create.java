package net.nonswag.tnl.waypoints.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.brigadier.PlayerSubCommand;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.waypoints.api.Waypoint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
        boolean isPublic = false;
        if (args.length >= 3) isPublic = Boolean.parseBoolean(args[2]);
        Waypoint waypoint = new Waypoint(args[1], player.worldManager().getLocation());
        if (!isPublic) waypoint.setOwner(player.getUniqueId());
        if (!waypoint.isRegistered()) {
            source.sendMessage("%prefix% §aCreated new waypoint named §6" + waypoint.register().getName());
            waypoint.showAll();
        } else source.sendMessage("%prefix% §cA waypoint named §4" + waypoint.getName() + "§c does already exist");
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length != 3) return suggestions;
        suggestions.add("true");
        suggestions.add("false");
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        source.sendMessage("%prefix% §c/waypoint create §8[§6Name§8] §8(§6Public§8)");
    }
}
