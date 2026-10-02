package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class AutoEZ extends Module {
    public AutoEZ() {
        super(SeaAstral.CATEGORY, "auto-ez", "autosayez!");
    }

    private final List<PlayerEntity> ezPlayer = new ArrayList<>();
    private final Set<UUID> deadPlayers = new HashSet<>();

    private void PlayerS() {
        ezPlayer.clear();
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (Friends.get().isFriend(player)) continue;

            double dist = mc.player.distanceTo(player);
            if (dist <= 500) {
                ezPlayer.add(player);
            }
        }
    }

    @Override
    public void onActivate() {
        ezPlayer.clear();
        deadPlayers.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null) return;
        PlayerS();
        for (PlayerEntity player : ezPlayer) {
            if (player.isDead()) {
                String name = player.getName().getString();
                ChatUtils.sendPlayerMsg(name + " 被SAclient崩飞了2里地");
                deadPlayers.add(player.getUuid());
            }
            if (!player.isDead() && deadPlayers.contains(player.getUuid())) {
                deadPlayers.remove(player.getUuid());
            }
        }
    }
}
