package com.seaastral.addon.modules;

import java.util.Random;
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


public class BizarreTp
    extends Module {
    public BizarreTp() {
        super(SeaAstral.CATEGORY, "bizarre-tp", "sbtp");
    }
    private boolean isair = false;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private PlayerEntity zhongzhuan;
    private final Setting<Double> tprange = sgGeneral.add(new DoubleSetting.Builder()
        .name("TpRange")
        .description("SBTpRange")
        .defaultValue(30.0d)
        .range(2.0d, 400.0d)
        .build()
    );
    private final Setting<Boolean> hasplayertp = sgGeneral.add(new BoolSetting.Builder()
        .name("HasPlayerTp")
        .description("WhatTP")
        .defaultValue(false)
        .build()
    );

    private void PlayerS() {
        PlayerEntity ATKplayer = null;
        double mD = Double.MAX_VALUE;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead()) continue;
            if (Friends.get().isFriend(player)) continue;
            if (player.getName().getString().equals("originalname")) continue;
            double dist = mc.player.distanceTo(player);

            if (dist <= tprange.get() && dist < mD) {
                mD = dist;
                ATKplayer = player;
                zhongzhuan = ATKplayer;
            }
        }
    }
    private void checkBlock(Vec3d randomPos) {
        BlockPos pos1 = BlockPos.ofFloored(randomPos.x, randomPos.y, randomPos.z);
        BlockPos pos2 = BlockPos.ofFloored(randomPos.x, randomPos.y + 1.2, randomPos.z);
        isair = !mc.world.getBlockState(pos1).isAir()
            && !mc.world.getBlockState(pos2).isAir();
    }
//hasplayer是有玩家才tp不要看不懂
    private void shitTp() {
        if (hasplayertp.get()) {
            Random random = new Random();
            Vec3d randomPos = new Vec3d(
                mc.player.getX() + (random.nextDouble() - 0.5) * tprange.get() * 2,
                mc.player.getY(),
                mc.player.getZ() + (random.nextDouble() - 0.5) * tprange.get() * 2
            );
            PlayerS();
            checkBlock(randomPos);
            if (isair == false) {
                return;
            }else{
                if(zhongzhuan == null){
                    return;
                }else {
                    mc.player.setPosition(randomPos.x, randomPos.y, randomPos.z);
                }
            }
        } else {
            Random random = new Random();
            Vec3d randomPos = new Vec3d(
                mc.player.getX() + (random.nextDouble() - 0.5) * tprange.get() * 2,
                mc.player.getY(),
                mc.player.getZ() + (random.nextDouble() - 0.5) * tprange.get() * 2
            );
            checkBlock(randomPos);
            if (isair == false) {
                return;
            }else {
                mc.player.setPosition(randomPos.x, randomPos.y, randomPos.z);
            }
        }

    }
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.player == null) return;
        if (mc.getNetworkHandler() == null) return;
        shitTp();
    }
}
