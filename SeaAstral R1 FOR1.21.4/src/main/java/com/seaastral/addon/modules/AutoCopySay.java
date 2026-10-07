package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import meteordevelopment.meteorclient.settings.StringSetting;

public class AutoCopySay extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> copysbsay = sgGeneral.add(new StringSetting.Builder()
        .name("SB")
        .description("SBSAY")
        .defaultValue("")
        .build()
    );
    public AutoCopySay() {
        super(SeaAstral.CATEGORY, "autocpoy-sb", "autocopysb");
    }

//鹦鹉学舌
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null) return;

    }
}
