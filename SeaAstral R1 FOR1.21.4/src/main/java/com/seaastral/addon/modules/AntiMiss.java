package com.seaastral.addon.modules;

import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AntiMiss extends Module {
    public AntiMiss() {
        super(SeaAstral.CATEGORY, "anti-miss", "AntiSBMissMe");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.world == null) return;
        fillHotbarWithTotems();
        fillOffhandWithTotem();
        switchToTotem();
    }
    private void fillHotbarWithTotems() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) {
                int totemSlot = findTotemInInventory();
                if (totemSlot != -1) {
                    InvUtils.move().from(totemSlot).to(i);
                }
            }
        }
    }
    private void fillOffhandWithTotem() {
        if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            int totemSlot = findTotemInInventory();
            if (totemSlot != -1) {
                InvUtils.move().from(totemSlot).toOffhand();
            }
        }
    }
    private void switchToTotem() {
        if (mc.player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                InvUtils.swap(i, false);
                return;
            }
        }
    }
    private int findTotemInInventory() {
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }
}
