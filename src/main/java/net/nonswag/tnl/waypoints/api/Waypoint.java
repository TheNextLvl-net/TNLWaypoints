package net.nonswag.tnl.waypoints.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.nonswag.tnl.core.api.file.formats.JsonFile;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.*;

@Getter
@Setter
public class Waypoint {
    @Nonnull
    private static final HashMap<UUID, List<Waypoint>> waypoints = new HashMap<>();
    @Nonnull
    private static final JsonFile saves = new JsonFile("plugins/Waypoints", "saves.json");

    @Nonnull
    private final UUID owner;
    @Nonnull
    private final String name;
    @Nonnull
    private final Location location;
    @Nonnull
    private Color color;

    public Waypoint(@Nonnull UUID owner, @Nonnull String name, @Nonnull Location location, @Nonnull Color color) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.color = color;
    }

    @Nonnull
    public Waypoint register() {
        List<Waypoint> waypoints = getWaypoints(getOwner());
        if (!waypoints.contains(this)) {
            waypoints.add(this);
            Waypoint.waypoints.put(getOwner(), waypoints);
        }
        return this;
    }

    public void unregister() {
        List<Waypoint> waypoints = getWaypoints(getOwner());
        waypoints.remove(this);
        Waypoint.waypoints.put(getOwner(), waypoints);
    }

    @Nonnull
    public Waypoint hide(@Nonnull TNLPlayer player) {
        if (!player.getUniqueId().equals(getOwner())) return this;
        Location location = getLocation().clone().subtract(0, 1, 0);
        if (!player.worldManager().getWorld().equals(location.getWorld())) return this;
        player.worldManager().sendBlockChange(location);
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0));
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        for (BlockFace face : faces) {
            player.worldManager().sendBlockChange(location.getBlock().getRelative(face).getLocation());
        }
        return this;
    }

    @Nonnull
    public Waypoint show(@Nonnull TNLPlayer player) {
        if (!player.getUniqueId().equals(getOwner())) return this;
        Location location = getLocation().clone().subtract(0, 1, 0);
        if (!player.worldManager().getWorld().equals(location.getWorld())) return this;
        player.worldManager().sendBlockChange(location, getColor().getBlockData());
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0), Material.BEACON.createBlockData());
        BlockData iron = Material.IRON_BLOCK.createBlockData();
        player.worldManager().sendBlockChange(location.subtract(0, 1, 0), iron);
        List<BlockFace> faces = List.of(BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST);
        for (BlockFace face : faces) {
            Location loc = location.getBlock().getRelative(face).getLocation();
            player.worldManager().sendBlockChange(loc, iron);
        }
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Nonnull
    public static List<Waypoint> getWaypoints(@Nonnull UUID owner) {
        return waypoints.getOrDefault(owner, new ArrayList<>());
    }

    @Nullable
    public static Waypoint getWaypoint(@Nonnull UUID owner, @Nonnull String name) {
        List<Waypoint> waypoints = getWaypoints(owner);
        for (Waypoint waypoint : waypoints) if (waypoint.getName().equalsIgnoreCase(name)) return waypoint;
        return null;
    }

    public static void loadAll() {
        JsonObject root = saves.getJsonElement().getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            try {
                UUID owner = UUID.fromString(entry.getKey());
                JsonArray waypoints = entry.getValue().getAsJsonArray();
                for (JsonElement element : waypoints) {
                    JsonObject waypoint = element.getAsJsonObject();
                    if (!waypoint.has("name")) continue;
                    if (!waypoint.has("location")) continue;
                    if (!waypoint.has("color")) continue;
                    String name = waypoint.get("name").getAsString();
                    Location location = parseLocation(waypoint.get("location").getAsString());
                    Color color = Color.getColor(waypoint.get("color").getAsString());
                    if (location != null && color != null) new Waypoint(owner, name, location, color).register();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Nullable
    private static Location parseLocation(@Nonnull String string) {
        String[] split = string.split(", ");
        if (split.length != 4 && split.length != 6) return null;
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = 0;
        float pitch = 0;
        if (split.length == 6) {
            yaw = Float.parseFloat(split[4]);
            pitch = Float.parseFloat(split[5]);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void exportAll() {
        JsonObject root = new JsonObject();
        waypoints.forEach((owner, waypoints) -> {
            if (waypoints.isEmpty()) return;
            JsonArray array = new JsonArray();
            for (Waypoint waypoint : waypoints) {
                JsonObject object = new JsonObject();
                object.addProperty("name", waypoint.getName());
                Location location = waypoint.getLocation();
                String s = location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " +
                        location.getZ() + ", " + location.getYaw() + ", " + location.getPitch();
                object.addProperty("location", s);
                object.addProperty("color", waypoint.getColor().name());
                array.add(object);
            }
            root.add(owner.toString(), array);
        });
        saves.setJsonElement(root);
        saves.save();
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

        @Nonnull
        private final String name;
        @Nonnull
        private final Material glass;
        @Nullable
        private BlockData blockData = null;

        Color(@Nonnull Material glass) {
            this.glass = glass;
            this.name = name().toLowerCase().replace("_", "-");
        }

        @Nonnull
        public BlockData getBlockData() {
            return blockData == null ? blockData = glass.createBlockData() : blockData;
        }

        @Nullable
        public static Color getColor(@Nonnull String name) {
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
