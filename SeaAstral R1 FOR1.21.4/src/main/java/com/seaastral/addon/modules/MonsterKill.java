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
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class MonsterKill extends Module {
    private Entity target = null;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("Killrange")
        .description("KillMonsterRange")
        .defaultValue(30.0)
        .range(1.0, 20000000.0)
        .build()
    );

    public MonsterKill() {
        super(SeaAstral.CATEGORY, "monster-kill", "tpmonster+kill");
    }
    private void sendMove(Vec3d pos) {
        if (mc.getNetworkHandler() == null) return;
        PlayerMoveC2SPacket movePacket = new PlayerMoveC2SPacket.Full(
            pos.x, pos.y, pos.z,
            mc.player.getYaw(),
            mc.player.getPitch(),
            false,
            mc.player.horizontalCollision
        );
        mc.player.networkHandler.sendPacket(movePacket);
    }
    private void MonsterS() {
        Entity atkmon = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!entity.isAlive()) continue;
            if (!(entity instanceof SpiderEntity) &&
                !(entity instanceof ZombieEntity) &&
                !(entity instanceof SkeletonEntity) &&
                !(entity instanceof CreeperEntity)) {
                continue;
            }
            double dist = mc.player.distanceTo(entity);
            if (dist <= range.get() && dist < bestDist) {
                bestDist = dist;
                atkmon = entity;
            }
        }
        this.target = atkmon;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.getNetworkHandler() == null) return;
        MonsterS();
        Vec3d originalPos = mc.player.getPos();
        if (target == null) return;
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();
        FindItemResult mace = InvUtils.find(Items.MACE);
        if (!mace.found()) return;
        InvUtils.swap(mace.slot(), true);
        mc.player.setPosition(x,y + 1.2,z);
        for (int i = 0; i < 3; i++) {
            double currentFall = 20 + i * 10;
            sendMove(new Vec3d(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ()));
            sendMove(target.getPos());
            Vec3d pos = target.getPos();
            mc.player.setPosition(pos.x, pos.y, pos.z);
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        mc.player.setPosition(originalPos.x, originalPos.y, originalPos.z);
        target = null;
    }
}
