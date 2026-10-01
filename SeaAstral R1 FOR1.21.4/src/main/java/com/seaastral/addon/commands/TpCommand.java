package com.seaastral.addon.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import com.seaastral.addon.SeaAstral;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class TpCommand extends Command {

    public TpCommand() {
        super("tp", "tpxyz");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("x", DoubleArgumentType.doubleArg())
            .then(argument("y", DoubleArgumentType.doubleArg())
                .then(argument("z", DoubleArgumentType.doubleArg())
                    .executes(context -> {
                        double x = DoubleArgumentType.getDouble(context, "x");
                        double y = DoubleArgumentType.getDouble(context, "y");
                        double z = DoubleArgumentType.getDouble(context, "z");

                        Vec3d pos = new Vec3d(x, y, z);

                        if (mc.player != null) {
                            mc.player.setPosition(pos.x, pos.y, pos.z);
                        }

                        return SINGLE_SUCCESS;
                    })
                )
            )
        );
    }
}
