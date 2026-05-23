package com.rerurate.keephotbarandarmor;

import com.mojang.logging.LogUtils;
import com.rerurate.keephotbarandarmor.handler.PlayerDeathHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Keephotbarandarmor.MODID)
public class Keephotbarandarmor {
    public static final String MODID = "keephotbarandarmor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Keephotbarandarmor() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
        LOGGER.info("[keephotbarandarmor] Mod initialized - Hotbar and Armor will be preserved on death!");
    }
}
