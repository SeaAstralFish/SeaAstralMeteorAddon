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
import net.minecraft.util.math.BlockPos;

public class BizarreTp extends Module {
    public BizarreTp() {
        super(SeaAstral.CATEGORY, "bizarre-tp", "sbtp");
    }

    private boolean isblock = false;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private PlayerEntity zhongzhuan;
    private final Setting<Double> tprange = sgGeneral.add(new DoubleSetting.Builder()
        .name("TpRange")
        .description("SBTpRange")
        .defaultValue(30.0d)
        .range(2.0d, 400.0d)
        .build()
    );

    @Override
    public void onDeactivate() {
        zhongzhuan = null;
    }

    private void checkBlock(Vec3d randomPos) {
        BlockPos pos1 = BlockPos.ofFloored(randomPos.x, randomPos.y, randomPos.z);
        BlockPos pos2 = BlockPos.ofFloored(randomPos.x, randomPos.y + 1.2, randomPos.z);
        isblock = !mc.world.getBlockState(pos1).isAir()
            || !mc.world.getBlockState(pos2).isAir();
    }

    //像粑粑的tp
    private void shitTp() {
        Random random = new Random();
        Vec3d randomPos = new Vec3d(
            mc.player.getX() + (random.nextDouble() - 0.5) * tprange.get() * 2,
            mc.player.getY(),
            mc.player.getZ() + (random.nextDouble() - 0.5) * tprange.get() * 2
        );
        checkBlock(randomPos);
        if (isblock == true) {
            return;
        } else {
            mc.player.setPosition(randomPos.x, randomPos.y, randomPos.z);
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
