package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;

public class TpCrystal extends Module {
    private PlayerEntity zhongzhuan;
    private boolean placed = false;
    private int delayTicks = 0;
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("Range")
        .description("Range")
        .defaultValue(200.0d)
        .range(10.0d, 1000.0d)
        .build()
    );
    public TpCrystal() {
        super(SeaAstral.CATEGORY, "tp-crystal", "TpCry,Boom");
    }
    private void PlayerS() {
        PlayerEntity ATKplayer = null;
        double mD = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead()) continue;
            if (Friends.get().isFriend(player)) continue;

            double dist = mc.player.distanceTo(player);

            if (dist <= range.get() && dist < mD) {
                mD = dist;
                ATKplayer = player;
                zhongzhuan = ATKplayer;
            }
        }
    }
    private void DataPacket() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (zhongzhuan == null) return;
        Vec3d currentPos = mc.player.getPos();
        Vec3d targetPos = new Vec3d(zhongzhuan.getX(), zhongzhuan.getY() - 3, zhongzhuan.getZ());
        double distance = currentPos.distanceTo(targetPos);
        double stepSize = 5.0;
        int steps = (int) Math.ceil(distance / stepSize);
        if (steps <= 1) {
            mc.player.setPosition(targetPos.x, targetPos.y, targetPos.z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(targetPos.x, targetPos.y, targetPos.z, mc.player.isOnGround(), mc.player.horizontalCollision)
            );
            return;
        }
        double dx = (targetPos.x - currentPos.x) / steps;
        double dy = (targetPos.y - currentPos.y) / steps;
        double dz = (targetPos.z - currentPos.z) / steps;
        for (int i = 1; i <= steps; i++) {
            double x = currentPos.x + dx * i;
            double y = currentPos.y + dy * i;
            double z = currentPos.z + dz * i;
            mc.player.setPosition(x, y, z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround
                        (x, y, z, mc.player.isOnGround(), mc.player.horizontalCollision)
            );
        }
    }
    private void placeOnHead() {
        if (mc.player == null || mc.interactionManager == null) return;
        BlockPos pos = new BlockPos(
            (int) Math.floor(mc.player.getX()),
            (int) Math.floor(mc.player.getY()) + 2,
            (int) Math.floor(mc.player.getZ())
        );
        FindItemResult obsidian = InvUtils.find(Items.OBSIDIAN);
        if (!obsidian.found()) return;
        InvUtils.swap(obsidian.slot(), true);
        mc.interactionManager.interactBlock(
            mc.player,
            Hand.MAIN_HAND,
            new BlockHitResult(
                Vec3d.ofCenter(pos),
                Direction.UP,
                pos,
                false
            )
        );

        mc.player.swingHand(Hand.MAIN_HAND);
    }
    private void placeOnHeadCry() {
        if (mc.player == null || mc.interactionManager == null) return;
        BlockPos pos = new BlockPos(
            (int) Math.floor(mc.player.getX()),
            (int) Math.floor(mc.player.getY()) + 2,
            (int) Math.floor(mc.player.getZ())
        );

        FindItemResult cry = InvUtils.find(Items.END_CRYSTAL);
        if (!cry.found()) return;
        InvUtils.swap(cry.slot(), true);

        mc.interactionManager.interactBlock(
            mc.player,
            Hand.MAIN_HAND,
            new BlockHitResult(
                Vec3d.ofCenter(pos),
                Direction.UP,
                pos,
                false
            )
        );
        mc.player.swingHand(Hand.MAIN_HAND);
    }
    private void atkcry() {
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }
    }
    @Override
    public void onActivate() {
        placed = false;
        delayTicks = 0;
    }
    //豆包模块。。。
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.getNetworkHandler() == null) return;
        double originX = mc.player.getX();
        double originY = mc.player.getY();
        double originZ = mc.player.getZ();
        PlayerS();
        if (zhongzhuan == null) return;

        FindItemResult obsidian = InvUtils.find(Items.OBSIDIAN);
        if (!obsidian.found()) {
            return;
        }
        if (!placed) {
            DataPacket();
            placeOnHead();
            placeOnHeadCry();
            placed = true;
            delayTicks = 0;
            return;
        }
        delayTicks++;
        if (delayTicks < 2) return;
        atkcry();
        mc.player.setPosition(originX, originY, originZ);
        mc.getNetworkHandler().sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(
                originX, originY, originZ,
                mc.player.isOnGround(),
                mc.player.horizontalCollision
            )
        );
        zhongzhuan = null;
        placed = false;
        delayTicks = 0;
    }
}
