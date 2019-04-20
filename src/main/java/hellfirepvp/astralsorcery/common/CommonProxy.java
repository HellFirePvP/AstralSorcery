/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import com.mojang.authlib.GameProfile;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigRegistries;
import hellfirepvp.astralsorcery.common.data.config.entry.*;
import hellfirepvp.astralsorcery.common.data.config.CommonConfig;
import hellfirepvp.astralsorcery.common.data.config.entry.common.CommonGeneralConfig;
import hellfirepvp.astralsorcery.common.data.config.registry.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.event.ClientInitializedEvent;
import hellfirepvp.astralsorcery.common.registry.internal.InternalRegistryPrimer;
import hellfirepvp.astralsorcery.common.registry.internal.PrimerEventHandler;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:38
 */
public class CommonProxy {

    private static final UUID FAKEPLAYER_UUID = UUID.fromString("b0c3097f-8391-4b4b-a89a-553ef730b13a");

    private InternalRegistryPrimer registryPrimer;
    private PrimerEventHandler registryEventHandler;

    private CommonConfig commonConfig;
    private ServerConfig serverConfig;

    public void initialize() {
        this.registryPrimer = new InternalRegistryPrimer();
        this.registryEventHandler = new PrimerEventHandler(this.registryPrimer);

        this.commonConfig = new CommonConfig();
        this.serverConfig = new ServerConfig();

        this.initializeConfigurations();

        ConfigRegistries.getRegistries().buildRegistries();
        this.commonConfig.buildConfiguration();
        this.serverConfig.buildConfiguration();
    }

    public void attachLifecycle(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);

        modEventBus.addListener(this::onServerStop);
        modEventBus.addListener(this::onServerStopping);
        modEventBus.addListener(this::onServerStarting);
        modEventBus.addListener(this::onServerStarted);
    }

    public void attachEventHandlers(IEventBus eventBus) {
        eventBus.addListener(this::onClientInitialized);

        eventBus.addListener(BlockDropCaptureAssist.INSTANCE::onDrop);

        registryEventHandler.attachEventHandlers(eventBus);
    }

    protected void initializeConfigurations() {
        ConfigRegistries.getRegistries().addDataRegistry(FluidRarityRegistry.INSTANCE);

        this.serverConfig.addConfigEntry(GeneralConfig.CONFIG);
        this.serverConfig.addConfigEntry(ToolsConfig.CONFIG);
        this.serverConfig.addConfigEntry(EntityConfig.CONFIG);
        this.serverConfig.addConfigEntry(CraftingConfig.CONFIG);
        this.serverConfig.addConfigEntry(LightNetworkConfig.CONFIG);
        this.serverConfig.addConfigEntry(WorldGenConfig.CONFIG);

        this.commonConfig.addConfigEntry(CommonGeneralConfig.CONFIG);
    }

    // Utils

    public FakePlayer getASFakePlayerServer(WorldServer world) {
        return FakePlayerFactory.get(world, new GameProfile(FAKEPLAYER_UUID, "AS-FakePlayer"));
    }

    // After registry events

    private void onCommonSetup(FMLCommonSetupEvent event) {

    }

    private void onServerStarted(FMLServerStartedEvent event) {

    }

    private void onServerStarting(FMLServerStartingEvent event) {

    }

    private void onClientInitialized(ClientInitializedEvent event) {

    }

    private void onServerStopping(FMLServerStoppingEvent event) {

    }

    private void onServerStop(FMLServerStoppedEvent event) {

    }
}
