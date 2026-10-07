package com.seaastral.addon.commands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
public class TpKill extends Command {
    public TpKill() {
        super("tpkill", "TpKillSB.");
    }
    private PlayerEntity zhongzhuan;
    private String fkplayer = "";
    private void PlayerS() {
        zhongzhuan = null;
        if (mc.world == null || mc.player == null) return;
        if (fkplayer == null || fkplayer.isEmpty()) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead()) continue;
            if (player.getName().getString().equalsIgnoreCase(fkplayer)) {
                zhongzhuan = player;
                return;
            }
        }
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
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("player", StringArgumentType.word())
            .executes(context -> {
                fkplayer = StringArgumentType.getString(context, "player");
                PlayerS();
                if (zhongzhuan == null) {
                    error("NoPlayer" + fkplayer);
                } else {
                    FindItemResult mace = InvUtils.find(Items.MACE);
                    if (!mace.found()) {
                        error("No mace in inventory.");
                        return SINGLE_SUCCESS;
                    }
                    InvUtils.swap(mace.slot(), true);
                    Vec3d xyznow = mc.player.getPos();
                    Vec3d enemyVelocity = zhongzhuan.getVelocity();
                    Vec3d targetPos = zhongzhuan.getPos();
                    Vec3d originalPos = mc.player.getPos();

                    mc.player.setPosition(targetPos.x, targetPos.y + 1.2, targetPos.z);

                    for (int i = 0; i < 3; i++) {
                        double currentFall = 20 + i * 10;
                        sendMove(new Vec3d(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ()));
                        sendMove(targetPos);
                        mc.player.setPosition(targetPos);
                        mc.interactionManager.attackEntity(mc.player, zhongzhuan);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }

                    mc.player.setPosition(xyznow.x, xyznow.y, xyznow.z);
                    zhongzhuan = null;
                }
                return SINGLE_SUCCESS;
            })
        );
    }
}
