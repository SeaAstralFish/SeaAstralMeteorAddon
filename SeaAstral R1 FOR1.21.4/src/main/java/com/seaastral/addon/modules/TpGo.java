package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
public class TpGo extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> y = sgGeneral.add(new DoubleSetting.Builder()
        .name("Y")
        .description("Y")
        .defaultValue(20000000.0d)
        .range(-100.0d, 50000000.0d)
        .build()
    );
    public TpGo() {
        super(SeaAstral.CATEGORY, "tpgo", "Tp");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null) return;
        Vec3d pos = mc.player.getPos();
        mc.player.setPosition(pos.x, y.get(), pos.z);
        toggle();
    }
}
