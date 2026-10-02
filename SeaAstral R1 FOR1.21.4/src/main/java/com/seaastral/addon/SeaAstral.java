package com.seaastral.addon;
import com.seaastral.addon.modules.BizarreTp;
import com.seaastral.addon.modules.AutoEZ;
import com.seaastral.addon.modules.MonsterKill;
import com.seaastral.addon.modules.TpCrystal;
import com.seaastral.addon.modules.AntiMiss;
import com.seaastral.addon.modules.SpearAura;
import com.seaastral.addon.modules.TpMace;
import com.seaastral.addon.modules.TpGo;
import com.seaastral.addon.modules.CircleMove;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.commands.Command;
import com.seaastral.addon.commands.TpCommand;
import org.slf4j.Logger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class SeaAstral extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("SeaAstral");
    @Override
    public void onInitialize() {
        LOG.info("InitializingSeaAstral");
        Modules.get().add(new TpMace());
        Modules.get().add(new TpGo());
        Modules.get().add(new CircleMove());
        Modules.get().add(new SpearAura());
        Modules.get().add(new TpCrystal());
        Modules.get().add(new AntiMiss());
        Modules.get().add(new MonsterKill());
        Modules.get().add(new BizarreTp());
        Modules.get().add(new AutoEZ());
        Commands.add(new TpCommand());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.seaastral.addon";
    }

}
