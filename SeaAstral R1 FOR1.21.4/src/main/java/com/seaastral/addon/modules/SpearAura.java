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
import net.minecraft.util.Hand;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class SpearAura extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private PlayerEntity zhongzhuan;
    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("Speed")
        .description("Speed")
        .defaultValue(50.0d)
        .range(0.1d, 100.0d)
        .build()
    );
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("Range")
        .description("Range")
        .defaultValue(500.0d)
        .range(10.0d, 1000.0d)
        .build()
    );
    //豆包模块bushi（
    public SpearAura() {
        super(SeaAstral.CATEGORY, "spear-aura", "1.21.4Spear?");
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

    private void moveForward(double speed) {
        if (mc.player == null) return;
        Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw());
        mc.player.setVelocity(
            forward.x * speed,
            mc.player.getVelocity().y,
            forward.z * speed
        );
    }

    private void lookAt(PlayerEntity target) {
        if (target == null || mc.player == null) return;
        Vec3d targetPos = target.getPos().add(0, target.getEyeHeight(mc.player.getPose()), 0);
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d diff = targetPos.subtract(playerPos);
        double dx = diff.x;
        double dy = diff.y;
        double dz = diff.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontalDistance));
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    private void setRightClickPressed(boolean pressed) {
        KeyBinding keyBinding = mc.options.useKey;
        keyBinding.setPressed(pressed);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null) return;
        PlayerS();
        if (zhongzhuan == null) return;
        mc.player.setPosition(zhongzhuan.getX(), zhongzhuan.getY(), zhongzhuan.getZ() + 5.0);
        setRightClickPressed(true);
        lookAt(zhongzhuan);
        moveForward(speed.get());
        if (zhongzhuan.isDead()) setRightClickPressed(false);
        return;
    }
}
