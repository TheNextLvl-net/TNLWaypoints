package net.nonswag.tnl.waypoints.api;

import lombok.Getter;
import net.nonswag.tnl.core.api.file.formats.spearat.TSVFile;
import net.nonswag.tnl.listener.TNLListener;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class Waypoint {

    @Nonnull
    public static final HashMap<String, Waypoint> WAYPOINTS = new HashMap<>();
    @Nonnull
    private static final TSVFile saves = new TSVFile("plugins/Waypoints", "saves.tsv");

    @Nonnull
    private final String name;
    @Nonnull
    private Location location;
    @Nullable
    private UUID owner;

    public Waypoint(@Nonnull String name, @Nonnull Location location, @Nullable UUID owner) {
        this.name = name;
        this.location = location.getBlock().getLocation();
        this.owner = owner;
    }

    public Waypoint(@Nonnull String name, @Nonnull Location location) {
        this(name, location, null);
    }

    @Nonnull
    public Waypoint setLocation(@Nonnull Location location) {
        this.location = location;
        return this;
    }

    @Nonnull
    public Waypoint setOwner(@Nullable UUID owner) {
        this.owner = owner;
        return this;
    }

    @Nonnull
    public Waypoint register() {
        if (!isRegistered()) WAYPOINTS.put(getName(), this);
        return this;
    }

    public void unregister() {
        WAYPOINTS.remove(getName());
    }

    public boolean isRegistered() {
        return WAYPOINTS.containsKey(getName());
    }

    public void hideAll() {
        for (TNLPlayer all : TNLListener.getOnlinePlayers()) hide(all);
    }

    public void hide(@Nonnull TNLPlayer player) {
        if (getOwner() != null && !player.getUniqueId().equals(getOwner())) return;
        Location location = getLocation().clone().subtract(0, 1, 0);
        if (!player.worldManager().getWorld().equals(location.getWorld())) return;
        player.worldManager().sendBlockChange(location);
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0));
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        for (BlockFace face : faces) {
            player.worldManager().sendBlockChange(location.getBlock().getRelative(face).getLocation());
        }
    }

    public void showAll() {
        for (TNLPlayer all : TNLListener.getOnlinePlayers()) show(all);
    }

    public void show(@Nonnull TNLPlayer player) {
        if (getOwner() != null && !player.getUniqueId().equals(getOwner())) return;
        Location location = getLocation().clone().subtract(0, 1, 0);
        if (!player.worldManager().getWorld().equals(location.getWorld())) return;
        player.worldManager().sendBlockChange(location, Material.BEACON.createBlockData());
        BlockData block = Material.IRON_BLOCK.createBlockData();
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0), block);
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        for (BlockFace face : faces) {
            Location loc = location.getBlock().getRelative(face).getLocation();
            player.worldManager().sendBlockChange(loc, block);
        }
    }

    @Nullable
    public static Waypoint get(@Nonnull String name) {
        return WAYPOINTS.get(name);
    }

    public static void loadAll() {
        for (List<String> entry : saves.getEntries()) {
            if (entry.size() < 2) continue;
            String name = entry.get(0);
            String[] loc = entry.get(1).split(", ");
            if (loc.length < 4) continue;
            World world = Bukkit.getWorld(loc[0]);
            int x = Integer.parseInt(loc[1]);
            int y = Integer.parseInt(loc[2]);
            int z = Integer.parseInt(loc[3]);
            Location location = new Location(world, x, y, z);
            UUID owner = null;
            if (entry.size() >= 3) {
                try {
                    owner = UUID.fromString(entry.get(2));
                } catch (IllegalArgumentException ignored) {
                }
            }
            new Waypoint(name, location, owner).register();
        }
    }

    public static void exportAll() {
        List<List<String>> entries = new ArrayList<>();
        for (Waypoint waypoint : WAYPOINTS.values()) {
            List<String> entry = new ArrayList<>();
            Location location = waypoint.getLocation();
            UUID owner = waypoint.getOwner();
            World world = location.getWorld();
            if (world == null) continue;
            entry.add(waypoint.getName());
            entry.add(world.getName() + ", " + location.getBlockX() + ", " +
                    location.getBlockY() + ", " + location.getBlockZ());
            if (owner != null) entry.add(owner.toString());
            entries.add(entry);
        }
        saves.setEntries(entries);
        saves.save();
    }
}
