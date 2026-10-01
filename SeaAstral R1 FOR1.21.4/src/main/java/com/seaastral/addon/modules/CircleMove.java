package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

public class CircleMove extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Double> Speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("Speed")
        .description("Speed.")
        .defaultValue(0.5d)
        .range(0.1d, 10.0d)
        .build()
    );
    private final Setting<Double> Radius = sgGeneral.add(new DoubleSetting.Builder()
        .name("Radius")
        .description("Radius.")
        .defaultValue(10.0d)
        .range(1.0d, 100.0d)
        .build()
    );
    private double angle = 0;
    private Vec3d centerPos = null;

    @Override
    public void onActivate() {
        if (mc.player != null) {
            centerPos = mc.player.getPos();
        }
        angle = 0;
    }
    public CircleMove() {
        super(SeaAstral.CATEGORY, "circle-move", "CircleNow!");
    }
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || centerPos == null) return;
        angle += Speed.get();

        double r = Radius.get();
        double x = centerPos.x + r * Math.cos(angle);
        double z = centerPos.z + r * Math.sin(angle);
        double y = mc.player.getY();
        Vec3d currentPos = mc.player.getPos();
        mc.player.setVelocity(x - currentPos.x, 0, z - currentPos.z);
        mc.player.setYaw((float) Math.toDegrees(angle) - 90);
        }
    }
