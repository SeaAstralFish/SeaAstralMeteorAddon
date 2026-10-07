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
import java.util.List;
import meteordevelopment.meteorclient.settings.IntSetting;

public class TpMace extends Module {
    private PlayerEntity zhongzhuan;
    private boolean isArmor = false;
    private double lastAngle = 0;
    private double angularSpeed = 0;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("Range")
        .description("Range")
        .defaultValue(500.0d)
        .range(20.0d, 40000000.0d)
        .build()
    );
    private final Setting<Boolean> LBLTMode = sgGeneral.add(new BoolSetting.Builder()
        .name("LBLTMode")
        .description("LBLTMode")
        .defaultValue(false)
        .build()
    );
    //    private final Setting<Double> lbltModeTicks = sgGeneral.add(new DoubleSetting.Builder()
//        .name("LBLTModeTicks")
//        .description("Ticks")
//        .defaultValue(8.0d)
//        .range(1.0d, 20.0d)
//        .build()
//    );
    private final Setting<Integer> fallfor = sgGeneral.add(new IntSetting.Builder()
        .name("fallfor")
        .description("fallllllllllll")
        .defaultValue(3)
        .range(1,10)
        .build()
    );
    private final Setting<Double> onefall = sgGeneral.add(new DoubleSetting.Builder()
        .name("Fall")
        .description("SBFall")
        .defaultValue(20.0d)
        .range(1.0d,320.0d)
        .build()
    );
    private final Setting<Double> fallpp = sgGeneral.add(new DoubleSetting.Builder()
        .name("Fall++")
        .description("Fall++ccc")
        .defaultValue(10.0d)
        .range(1.0d, 320.0d)
        .build()
    );
    private final Setting<Boolean> predict = sgGeneral.add(new BoolSetting.Builder()
        .name("predict")
        .description("predictON/OFF")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> predictcir = sgGeneral.add(new BoolSetting.Builder()
        .name("predictcir")
        .description("predictcirON/OFF")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> pt = sgGeneral.add(new IntSetting.Builder()
        .name("Tick")
        .description("Tickpredict")
        .defaultValue(4)
        .range(1, 800)
        .build()
    );
    private final Setting<Integer> circleTicks = sgGeneral.add(new IntSetting.Builder()
        .name("CircleTick")
        .description("CircleTickOFFON")
        .defaultValue(2)
        .range(1, 10)
        .build()
    );

    @Override
    public void onActivate() {
        lastAngle = 0;
        angularSpeed = 0;
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

    public TpMace() {
        super(SeaAstral.CATEGORY, "tp-mace", "TpKill");
    }

    private void PlayerS() {
        PlayerEntity ATKplayer = null;
        double mD = Double.MAX_VALUE;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead()) continue;
            if (Friends.get().isFriend(player)) continue;
            if (player.getName().getString().equals("originalname")) continue;
            double dist = mc.player.distanceTo(player);

            if (dist <= range.get() && dist < mD) {
                mD = dist;
                ATKplayer = player;
                if (ATKplayer != zhongzhuan) {
                    lastAngle = 0;
                    angularSpeed = 0;
                }
                zhongzhuan = ATKplayer;
            }
        }
    }

    private void DataPacket() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (zhongzhuan == null) return;
        Vec3d currentPos = mc.player.getPos();
        Vec3d targetPos = new Vec3d(zhongzhuan.getX(), zhongzhuan.getY() + 1.5, zhongzhuan.getZ());
        double distance = currentPos.distanceTo(targetPos);
        double stepSize = 5.0;
        int steps = (int) Math.ceil(distance / stepSize);
        if (steps <= 1) {
            mc.player.setPosition(targetPos.x, targetPos.y, targetPos.z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(targetPos.x, targetPos.y, targetPos.z, mc.player.isOnGround(), mc.player.horizontalCollision)
            );
            return;
        }
        double dx = (targetPos.x - currentPos.x) / steps;
        double dy = (targetPos.y - currentPos.y) / steps;
        double dz = (targetPos.z - currentPos.z) / steps;
        for (int i = 1; i <= steps; i++) {
            double x = currentPos.x + dx * i;
            double y = currentPos.y + dy * i;
            double z = currentPos.z + dz * i;
            mc.player.setPosition(x, y, z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, mc.player.isOnGround(), mc.player.horizontalCollision)
            );
        }
    }

    private void hasArmor(PlayerEntity target) {
        if (target == null) return;
        isArmor = false;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = target.getInventory().getArmorStack(i);
            if (!stack.isEmpty()) {
                isArmor = true;
            }
        }
        return;
    }

    private void DataPacketBA(Vec3d targetPos) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (targetPos == null) return;

        Vec3d currentPos = mc.player.getPos();
        double distance = currentPos.distanceTo(targetPos);
        double stepSize = 5.0;
        int steps = (int) Math.ceil(distance / stepSize);

        if (steps <= 1) {
            mc.player.setPosition(targetPos.x, targetPos.y, targetPos.z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(
                    targetPos.x, targetPos.y, targetPos.z,
                    mc.player.isOnGround(), mc.player.horizontalCollision
                )
            );
            return;
        }

        double dx = (targetPos.x - currentPos.x) / steps;
        double dy = (targetPos.y - currentPos.y) / steps;
        double dz = (targetPos.z - currentPos.z) / steps;

        for (int i = 1; i <= steps; i++) {
            double x = currentPos.x + dx * i;
            double y = currentPos.y + dy * i;
            double z = currentPos.z + dz * i;
            mc.player.setPosition(x, y, z);
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(
                    x, y, z,
                    mc.player.isOnGround(), mc.player.horizontalCollision
                )
            );
        }
    }

    private void breachAttack(PlayerEntity target) {
        if (target == null) return;
        Vec3d playerPos = mc.player.getPos();
        for (int i = 0; i < 4; i++) {
            mc.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.OnGroundOnly(false, mc.player.horizontalCollision)
            );
        }
        double[] heights = {20, 60, 120};
        for (double h : heights) {
            Vec3d upPos = new Vec3d(target.getX(), target.getY() + h, target.getZ());
            DataPacketBA(upPos);
            DataPacketBA(new Vec3d(target.getX(), target.getY(), target.getZ()));
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            DataPacketBA(playerPos);
        }
        mc.player.setVelocity(mc.player.getVelocity().x, 0.1, mc.player.getVelocity().z);
        mc.player.fallDistance = 0;
    }

    private Vec3d predictMace(PlayerEntity target) {
        if (target == null) return null;
        Vec3d cur = target.getPos();
        Vec3d lastPos = new Vec3d(target.prevX, target.prevY, target.prevZ);
        Vec3d velocity = cur.subtract(lastPos);
        double resultMul = 3.0;
        Vec3d scaledVel = velocity.multiply(resultMul);
        Vec3d posVec = cur;
        int ticks = pt.get();
        for (int i = 0; i < ticks; i++) {
            Vec3d adjusted = Entity.adjustMovementForCollisions(target, scaledVel, target.getBoundingBox(), target.getWorld(), List.of());
            posVec = posVec.add(adjusted);
            if (adjusted.lengthSquared() == 0.0) {
                break;
            }
        }
        return posVec;
    }

    private Vec3d predictCircle(PlayerEntity target) {
        if (target == null) return null;
        Vec3d cur = target.getPos();
        Vec3d lastPos = new Vec3d(target.prevX, target.prevY, target.prevZ);
        Vec3d velocity = cur.subtract(lastPos);
        double speed = velocity.length();
        if (speed < 0.05) return cur;
        double angle = Math.atan2(velocity.z, velocity.x);
        double delta = angle - lastAngle;
        if (delta > Math.PI) delta -= 2 * Math.PI;
        if (delta < -Math.PI) delta += 2 * Math.PI;
        angularSpeed = delta;
        lastAngle = angle;
        int ticks = circleTicks.get();
        double futureAngle = angle + angularSpeed * ticks;
        double distance = speed * ticks;
        double dx = Math.cos(futureAngle) * distance;
        double dz = Math.sin(futureAngle) * distance;
        return new Vec3d(cur.x + dx, cur.y, cur.z + dz);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive() || mc.player == null || mc.getNetworkHandler() == null) return;
        Vec3d xyznow = mc.player.getPos();
        PlayerS();
        if (zhongzhuan == null) return;
        hasArmor(zhongzhuan);
        FindItemResult mace = InvUtils.find(Items.MACE);
        if (!mace.found()) return;
        InvUtils.swap(mace.slot(), true);
        if (LBLTMode.get()) {
            DataPacket();
            Vec3d originalPosLB = mc.player.getPos();
            if (isArmor == false) {
                for (int i = 0; i < 3; i++) {
                    double currentFall = 20 + i * 10;

                    mc.player.setPosition(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ());
                    mc.getNetworkHandler().sendPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround(), mc.player.horizontalCollision)
                    );

                    mc.player.setPosition(originalPosLB.x, originalPosLB.y, originalPosLB.z);
                    mc.getNetworkHandler().sendPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(originalPosLB.x, originalPosLB.y, originalPosLB.z, mc.player.isOnGround(), mc.player.horizontalCollision)
                    );

                    mc.interactionManager.attackEntity(mc.player, zhongzhuan);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }

                mc.player.setPosition(xyznow.x, xyznow.y, xyznow.z);
                mc.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(xyznow.x, xyznow.y, xyznow.z, mc.player.isOnGround(), mc.player.horizontalCollision)
                );
                zhongzhuan = null;
            } else {
                breachAttack(zhongzhuan);
                zhongzhuan = null;
            }
        } else {
            if (predict.get()) {
                Vec3d targetPos = predictMace(zhongzhuan);
                if (targetPos == null) {
                    zhongzhuan = null;
                    return;
                }
                Vec3d originalPos = mc.player.getPos();
                mc.player.setPosition(targetPos.x, targetPos.y + 1.2, targetPos.z);

                for (int i = 0; i < fallfor.get(); i++) {
                    double currentFall = onefall.get() + i * fallpp.get();
                    sendMove(new Vec3d(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ()));
                    sendMove(targetPos);
                    mc.player.setPosition(targetPos);
                    mc.interactionManager.attackEntity(mc.player, zhongzhuan);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                mc.player.setPosition(xyznow.x, xyznow.y, xyznow.z);
                zhongzhuan = null;
            } else {
                if (predictcir.get()) {
                    Vec3d targetPos = predictCircle(zhongzhuan);
                    if (targetPos == null) {
                        zhongzhuan = null;
                        return;
                    }

                    Vec3d originalPos = mc.player.getPos();
                    mc.player.setPosition(targetPos.x, targetPos.y + 1.2, targetPos.z);

                    for (int i = 0; i < (int) fallfor.get(); i++) {
                        double currentFall = onefall.get() + i * fallpp.get();
                        sendMove(new Vec3d(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ()));
                        sendMove(targetPos);
                        mc.player.setPosition(targetPos);
                        mc.interactionManager.attackEntity(mc.player, zhongzhuan);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    mc.player.setPosition(xyznow.x, xyznow.y, xyznow.z);
                    zhongzhuan = null;
                } else {
                    Vec3d enemyVelocity = zhongzhuan.getVelocity();
                    Vec3d targetPos = zhongzhuan.getPos();
                    Vec3d originalPos = mc.player.getPos();

                    mc.player.setPosition(targetPos.x, targetPos.y + 1.2, targetPos.z);

                    for (int i = 0; i < fallfor.get(); i++) {
                        double currentFall = onefall.get() + i * fallpp.get();
                        sendMove(new Vec3d(mc.player.getX(), mc.player.getY() + currentFall, mc.player.getZ()));
                        sendMove(targetPos);
                        mc.player.setPosition(targetPos);
                        mc.interactionManager.attackEntity(mc.player, zhongzhuan);
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }

                    mc.player.setPosition(xyznow.x, xyznow.y, xyznow.z);
                    zhongzhuan = null;
                }
            }

        }
    }
}
