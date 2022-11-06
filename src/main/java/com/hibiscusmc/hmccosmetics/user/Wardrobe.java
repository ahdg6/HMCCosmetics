package com.hibiscusmc.hmccosmetics.user;

import com.hibiscusmc.hmccosmetics.HMCCosmeticsPlugin;
import com.hibiscusmc.hmccosmetics.config.WardrobeSettings;
import com.hibiscusmc.hmccosmetics.util.ServerUtils;
import com.hibiscusmc.hmccosmetics.util.packets.PacketManager;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Wardrobe {

    private int NPC_ID;
    private UUID WARDROBE_UUID;
    private int ARMORSTAND_ID;
    private GameMode originalGamemode;
    private CosmeticUser VIEWER;
    private Location viewingLocation;
    private Location npcLocation;
    private Location exitLocation;
    private Boolean active;

    public Wardrobe(CosmeticUser user) {
        NPC_ID = Entity.nextEntityId();
        ARMORSTAND_ID = Entity.nextEntityId();
        WARDROBE_UUID = UUID.randomUUID();
        VIEWER = user;
    }

    public void start() {
        Player player = VIEWER.getPlayer();
        player.sendMessage("start");
        player.sendMessage("NPC ID " + NPC_ID);
        player.sendMessage("armorstand id " + ARMORSTAND_ID);

        if (!WardrobeSettings.inDistanceOfStatic(player.getLocation())) {
            player.sendMessage("You are to far away!");
            return;
        }

        this.originalGamemode = player.getGameMode();
        if (WardrobeSettings.isReturnLastLocation()) {
            this.exitLocation = player.getLocation().clone();
        } else {
            this.exitLocation = WardrobeSettings.getLeaveLocation();
        }

        VIEWER.hidePlayer();
        List<Player> viewer = List.of(player);
        // Armorstand
        PacketManager.sendEntitySpawnPacket(WardrobeSettings.getViewerLocation(), ARMORSTAND_ID, EntityType.ARMOR_STAND, UUID.randomUUID(), viewer);
        PacketManager.sendInvisibilityPacket(ARMORSTAND_ID, viewer);
        PacketManager.sendLookPacket(ARMORSTAND_ID, WardrobeSettings.getViewerLocation(), viewer);

        // Player
        PacketManager.gamemodeChangePacket(player, 3);
        PacketManager.sendCameraPacket(ARMORSTAND_ID, viewer);

        // NPC
        PacketManager.sendFakePlayerInfoPacket(player, NPC_ID, WARDROBE_UUID, viewer);

        // NPC 2
        Bukkit.getScheduler().runTaskLater(HMCCosmeticsPlugin.getInstance(), () -> {
            PacketManager.sendFakePlayerSpawnPacket(WardrobeSettings.getWardrobeLocation(), WARDROBE_UUID, NPC_ID, viewer);
            HMCCosmeticsPlugin.getInstance().getLogger().info("Spawned Fake Player on " + WardrobeSettings.getWardrobeLocation());
        }, 4);


        // Location
        PacketManager.sendLookPacket(NPC_ID, WardrobeSettings.getWardrobeLocation(), viewer);
        PacketManager.sendRotationPacket(NPC_ID, WardrobeSettings.getWardrobeLocation(), true, viewer);

        this.active = true;
        update();

    }

    public void end() {
        this.active = false;

        Player player = VIEWER.getPlayer();
        player.sendMessage("end");
        player.sendMessage("NPC ID " + NPC_ID);
        player.sendMessage("armorstand id " + ARMORSTAND_ID);

        List<Player> viewer = List.of(player);

        // NPC
        PacketManager.sendEntityDestroyPacket(NPC_ID, viewer); // Success
        PacketManager.sendRemovePlayerPacket(player, player.getUniqueId(), viewer); // Success

        // Player
        PacketManager.sendCameraPacket(player.getEntityId(), viewer);
        PacketManager.gamemodeChangePacket(player, ServerUtils.convertGamemode(this.originalGamemode)); // Success

        // Armorstand
        PacketManager.sendEntityDestroyPacket(ARMORSTAND_ID, viewer); // Sucess

        //PacketManager.sendEntityDestroyPacket(player.getEntityId(), viewer); // Success
        player.setGameMode(this.originalGamemode);
        VIEWER.showPlayer();

        if (exitLocation == null) {
            player.teleport(player.getWorld().getSpawnLocation());
        } else {
            player.teleport(exitLocation);
        }
        if (!player.isOnline()) return;
        VIEWER.updateCosmetic();
    }

    public void update() {
        final AtomicInteger data = new AtomicInteger();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (active == false) {
                    HMCCosmeticsPlugin.getInstance().getLogger().info("Active is false");
                    this.cancel();
                    return;
                }
                HMCCosmeticsPlugin.getInstance().getLogger().info("Update ");
                List<Player> viewer = List.of(VIEWER.getPlayer());

                Location location = WardrobeSettings.getWardrobeLocation().clone();
                int yaw = data.get();
                location.setYaw(yaw);

                PacketManager.sendLookPacket(NPC_ID, location, viewer);
                VIEWER.updateCosmetic();
                int rotationSpeed = 3;
                location.setYaw(getNextYaw(yaw - 30, rotationSpeed));
                PacketManager.sendRotationPacket(NPC_ID, location, true, viewer);
                data.set(getNextYaw(yaw, rotationSpeed));
            }
        };

        runnable.runTaskTimer(HMCCosmeticsPlugin.getInstance(), 10, 0);
    }

    private static int getNextYaw(final int current, final int rotationSpeed) {
        int nextYaw = current + rotationSpeed;
        if (nextYaw > 179) {
            nextYaw = (current + rotationSpeed) - 358;
            return nextYaw;
        }
        return nextYaw;
    }

    public int getARMORSTAND_ID() {
        return ARMORSTAND_ID;
    }
}
