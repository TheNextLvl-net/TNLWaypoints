package net.nonswag.tnl.waypoints.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.nonswag.core.api.annotation.FieldsAreNonnullByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.core.api.file.formats.JsonFile;
import net.nonswag.tnl.holograms.api.Hologram;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@Getter
@Setter
@Accessors(chain = true)
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Waypoint {
    private static final HashMap<UUID, List<Waypoint>> waypoints = new HashMap<>();
    private static final JsonFile saves = new JsonFile("plugins/Waypoints", "saves.json");

    private final UUID owner;
    private final String name;
    private final BlockLocation location;
    private final Hologram hologram;
    private Color color;

    public Waypoint(UUID owner, String name, BlockLocation location, Color color) {
        this(owner, name, location, new Hologram().setLines("§8* §7Waypoint§8: §6%s".formatted(name)), color);
        getHologram().setLocation(getLocation().toLocation().add(0.5, 0.5, 0.5));
    }

    public Waypoint register() {
        List<Waypoint> waypoints = getWaypoints(getOwner());
        if (waypoints.contains(this)) throw new IllegalStateException("Waypoint already registered");
        waypoints.add(this);
        Waypoint.waypoints.put(getOwner(), waypoints);
        return this;
    }

    public void unregister() {
        List<Waypoint> waypoints = getWaypoints(getOwner());
        if(!waypoints.contains(this)) throw new IllegalStateException("Waypoint is not registered");
        waypoints.remove(this);
        Waypoint.waypoints.put(getOwner(), waypoints);
    }

    public Waypoint hide(TNLPlayer player) {
        if (!player.getUniqueId().equals(getOwner())) return this;
        BlockLocation location = getLocation().clone();
        if (!player.worldManager().getWorld().equals(location.getWorld())) return this;
        for (int i = 0; i < 3; i++) player.worldManager().sendBlockChange(location.subtract(0, 1, 0));
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        faces.forEach(face -> player.worldManager().sendBlockChange(location.getBlock().getRelative(face).getLocation()));
        player.hologramManager().unload(hologram);
        return this;
    }

    public Waypoint show(TNLPlayer player) {
        if (!player.getUniqueId().equals(getOwner())) return this;
        Location location = getLocation().clone().subtract(0, 1, 0);
        if (!player.worldManager().getWorld().equals(location.getWorld())) return this;
        player.worldManager().sendBlockChange(location, getColor().getBlockData());
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0), Material.BEACON.createBlockData());
        BlockData iron = Material.IRON_BLOCK.createBlockData();
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0), iron);
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        faces.forEach(face -> player.worldManager().sendBlockChange(location.getBlock().getRelative(face).getLocation(), iron));
        player.hologramManager().reload(hologram);
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static List<Waypoint> getWaypoints(UUID owner) {
        return Waypoint.waypoints.getOrDefault(owner, new ArrayList<>());
    }

    public static List<Waypoint> getWaypoints(UUID owner, Chunk chunk) {
        List<Waypoint> waypoints = new ArrayList<>(getWaypoints(owner));
        waypoints.removeIf(waypoint -> !waypoint.getLocation().getChunk().equals(chunk));
        return waypoints;
    }

    @Nullable
    public static Waypoint getWaypoint(UUID owner, String name) {
        List<Waypoint> waypoints = getWaypoints(owner);
        for (Waypoint waypoint : waypoints) if (waypoint.getName().equalsIgnoreCase(name)) return waypoint;
        return null;
    }

    public static void loadAll() {
        saves.getRoot().getAsJsonObject().entrySet().forEach(Waypoint::loadEntry);
    }

    private static void loadEntry(Map.Entry<String, JsonElement> entry) {
        UUID owner = UUID.fromString(entry.getKey());
        entry.getValue().getAsJsonArray().forEach(element -> {
            JsonObject waypoint = element.getAsJsonObject();
            if (!waypoint.has("name")) return;
            if (!waypoint.has("location")) return;
            if (!waypoint.has("color")) return;
            String name = waypoint.get("name").getAsString();
            BlockLocation location = parseLocation(waypoint.get("location").getAsString());
            Color color = Color.getColor(waypoint.get("color").getAsString());
            if (location != null && color != null) new Waypoint(owner, name, location, color).register();
        });
    }

    @Nullable
    private static BlockLocation parseLocation(String string) {
        String[] split = string.split(", ");
        if (split.length != 4) return null;
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        return new BlockLocation(world, x, y, z);
    }

    public static void exportAll() {
        JsonObject root = new JsonObject();
        waypoints.forEach((owner, waypoints) -> {
            if (waypoints.isEmpty()) return;
            JsonArray array = new JsonArray();
            waypoints.forEach(waypoint -> {
                JsonObject object = new JsonObject();
                object.addProperty("name", waypoint.getName());
                BlockLocation l = waypoint.getLocation();
                if (l.getWorld() == null) return;
                object.addProperty("location", "%s, %s, %s, %s".formatted(l.getWorld().getName(), l.getX(), l.getY(), l.getZ()));
                object.addProperty("color", waypoint.getColor().name());
                array.add(object);
            });
            root.add(owner.toString(), array);
        });
        saves.setRoot(root).save();
    }

    @Getter
    public enum Color {
        WHITE(Material.WHITE_STAINED_GLASS),
        ORANGE(Material.ORANGE_STAINED_GLASS),
        MAGENTA(Material.MAGENTA_STAINED_GLASS),
        LIGHT_BLUE(Material.LIGHT_BLUE_STAINED_GLASS),
        YELLOW(Material.YELLOW_STAINED_GLASS),
        LIME(Material.LIME_STAINED_GLASS),
        PINK(Material.PINK_STAINED_GLASS),
        GRAY(Material.GRAY_STAINED_GLASS),
        LIGHT_GRAY(Material.LIGHT_GRAY_STAINED_GLASS),
        CYAN(Material.CYAN_STAINED_GLASS),
        PURPLE(Material.PURPLE_STAINED_GLASS),
        BLUE(Material.BLUE_STAINED_GLASS),
        BROWN(Material.BROWN_STAINED_GLASS),
        GREEN(Material.GREEN_STAINED_GLASS),
        RED(Material.RED_STAINED_GLASS),
        BLACK(Material.BLACK_STAINED_GLASS);

        private final String name;
        private final Material glass;
        @Nullable
        private BlockData blockData = null;

        Color(Material glass) {
            this.glass = glass;
            this.name = name().toLowerCase().replace("_", "-");
        }

        public BlockData getBlockData() {
            return blockData == null ? blockData = glass.createBlockData() : blockData;
        }

        @Nullable
        public static Color getColor(String name) {
            for (Color color : values()) {
                if (color.name().equalsIgnoreCase(name) || color.getName().equalsIgnoreCase(name)) return color;
            }
            return null;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
