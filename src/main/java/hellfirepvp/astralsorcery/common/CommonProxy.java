/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.BlockBreakHelper;
import hellfirepvp.astralsorcery.common.auxiliary.gateway.CelestialGatewayHandler;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkHandler;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonDataManager;
import hellfirepvp.astralsorcery.common.base.patreon.manager.PatreonManager;
import hellfirepvp.astralsorcery.common.cmd.CommandAstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.CustomAltarRecipeHandler;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigRegistries;
import hellfirepvp.astralsorcery.common.data.config.entry.*;
import hellfirepvp.astralsorcery.common.data.config.CommonConfig;
import hellfirepvp.astralsorcery.common.data.config.entry.common.CommonGeneralConfig;
import hellfirepvp.astralsorcery.common.data.config.registry.*;
import hellfirepvp.astralsorcery.common.data.research.ResearchIOThread;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.enchantment.amulet.AmuletRandomizeHelper;
import hellfirepvp.astralsorcery.common.enchantment.amulet.PlayerAmuletHandler;
import hellfirepvp.astralsorcery.common.event.ClientInitializedEvent;
import hellfirepvp.astralsorcery.common.event.handler.EventHandlerCache;
import hellfirepvp.astralsorcery.common.event.handler.EventHandlerInteract;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperRitualFlight;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperSpawnDeny;
import hellfirepvp.astralsorcery.common.integration.IntegrationCurios;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktOpenGui;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTickHelper;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.astralsorcery.common.registry.internal.InternalRegistryPrimer;
import hellfirepvp.astralsorcery.common.registry.internal.PrimerEventHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightUpdateHandler;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChunkTracker;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.DamageSourceUtil;
import hellfirepvp.astralsorcery.common.util.ServerLifecycleListener;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import hellfirepvp.observerlib.common.util.tick.TickManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:38
 */
public class CommonProxy {

    private static final UUID FAKEPLAYER_UUID = UUID.fromString("b0c3097f-8391-4b4b-a89a-553ef730b13a");

    public static DamageSource DAMAGE_SOURCE_BLEED   = DamageSourceUtil.newType("astralsorcery.bleed")
            .setDamageBypassesArmor();
    public static DamageSource DAMAGE_SOURCE_STELLAR = DamageSourceUtil.newType("astralsorcery.stellar")
            .setDamageBypassesArmor().setMagicDamage();
    public static DamageSource DAMAGE_SOURCE_REFLECT = DamageSourceUtil.newType("thorns");

    private InternalRegistryPrimer registryPrimer;
    private PrimerEventHandler registryEventHandler;
    private CommonScheduler commonScheduler;
    private TickManager tickManager;
    private List<ServerLifecycleListener> serverLifecycleListeners = Lists.newArrayList();

    private CommonConfig commonConfig;
    private ServerConfig serverConfig;

    public void initialize() {
        this.registryPrimer = new InternalRegistryPrimer();
        this.registryEventHandler = new PrimerEventHandler(this.registryPrimer);
        this.commonScheduler = new CommonScheduler();

        this.commonConfig = new CommonConfig();
        this.serverConfig = new ServerConfig();

        RegistryData.init();
        RegistryMaterials.init();
        RegistryGameRules.init();
        RegistryTags.registerTags();
        RegistryStructureTypes.init();
        PacketChannel.registerPackets();
        RegistryLootFunctions.registerLootFunctions();
        RegistryPerkAttributeTypes.init();
        RegistryPerkAttributeReaders.init();
        CustomAltarRecipeHandler.registerDefaultConverters();

        this.initializeConfigurations();

        ConfigRegistries.getRegistries().buildDataRegistries();

        this.tickManager = new TickManager();
        this.attachTickListeners(tickManager::register);

        this.serverLifecycleListeners.add(ResearchIOThread.startup());
        this.serverLifecycleListeners.add(ServerLifecycleListener.wrap(EventHandlerCache::onServerStart, EventHandlerCache::onServerStop));
        this.serverLifecycleListeners.add(ServerLifecycleListener.start(CelestialGatewayHandler.INSTANCE::onServerStart));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(BlockBreakHelper::clearServerCache));

        SyncDataHolder.initialize();
    }

    public void attachLifecycle(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onEnqueueIMC);

        modEventBus.addListener(RegistryRegistries::buildRegistries);
        registryEventHandler.attachEventHandlers(modEventBus);
    }

    public void attachEventHandlers(IEventBus eventBus) {
        eventBus.addListener(this::onClientInitialized);

        eventBus.addListener(this::onServerStop);
        eventBus.addListener(this::onServerStopping);
        eventBus.addListener(this::onServerStarting);
        eventBus.addListener(this::onServerStarted);

        EventHandlerInteract.attachListeners(eventBus);
        EventHandlerCache.attachListeners(eventBus);
        EventHelperSpawnDeny.attachListeners(eventBus);
        PerkAttributeLimiter.attachListeners(eventBus);

        eventBus.addListener(PlayerAmuletHandler::onEnchantmentAdd);
        eventBus.addListener(BlockDropCaptureAssist.INSTANCE::onDrop);
        eventBus.addListener(CelestialGatewayHandler.INSTANCE::onWorldInit);

        tickManager.attachListeners(eventBus);
        TransmissionChunkTracker.INSTANCE.attachListeners(eventBus);
    }

    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        registrar.accept(this.commonScheduler);
        registrar.accept(StarlightTransmissionHandler.getInstance());
        registrar.accept(StarlightUpdateHandler.getInstance());
        registrar.accept(SyncDataHolder.getTickInstance());
        registrar.accept(LinkHandler.getInstance());
        registrar.accept(SkyHandler.getInstance());
        registrar.accept(PlayerAmuletHandler.INSTANCE);
        registrar.accept(PerkTickHelper.INSTANCE);
        registrar.accept(PatreonManager.INSTANCE);

        EventHelperRitualFlight.attachTickListener(registrar);
        EventHelperSpawnDeny.attachTickListener(registrar);
        PerkCooldownHelper.attachTickListeners(registrar);
    }

    protected void initializeConfigurations() {
        ConfigRegistries.getRegistries().addDataRegistry(FluidRarityRegistry.INSTANCE);
        ConfigRegistries.getRegistries().addDataRegistry(TechnicalEntityRegistry.INSTANCE);
        ConfigRegistries.getRegistries().addDataRegistry(AmuletEnchantmentRegistry.INSTANCE);
        ConfigRegistries.getRegistries().addDataRegistry(GemAttributeRegistry.INSTANCE);
        ConfigRegistries.getRegistries().addDataRegistry(OreItemRarityRegistry.VOID_TRASH_REWARD);
        ConfigRegistries.getRegistries().addDataRegistry(OreBlockRarityRegistry.STONE_ENRICHMENT);

        ToolsConfig.CONFIG.newSubSection(WandsConfig.CONFIG);

        this.serverConfig.addConfigEntry(GeneralConfig.CONFIG);
        this.serverConfig.addConfigEntry(ToolsConfig.CONFIG);
        this.serverConfig.addConfigEntry(EntityConfig.CONFIG);
        this.serverConfig.addConfigEntry(CraftingConfig.CONFIG);
        this.serverConfig.addConfigEntry(LightNetworkConfig.CONFIG);
        this.serverConfig.addConfigEntry(LogConfig.CONFIG);
        this.serverConfig.addConfigEntry(PerkConfig.CONFIG);
        this.serverConfig.addConfigEntry(AmuletRandomizeHelper.CONFIG);

        this.commonConfig.addConfigEntry(CommonGeneralConfig.CONFIG);

        ConstellationEffectRegistry.addConfigEntries(this.serverConfig);
        RegistryWorldGeneration.registerFeatureConfigurations(this.serverConfig);
    }

    public InternalRegistryPrimer getRegistryPrimer() {
        return registryPrimer;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    // Utils

    public FakePlayer getASFakePlayerServer(ServerWorld world) {
        return FakePlayerFactory.get(world, new GameProfile(FAKEPLAYER_UUID, "AS-FakePlayer"));
    }

    public File getASServerDataDirectory() {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (server == null) {
            return null;
        }
        File asDataDir = server.getActiveAnvilConverter().getFile(server.getFolderName(), AstralSorcery.MODID);
        if (!asDataDir.exists()) {
            asDataDir.mkdirs();
        }
        return asDataDir;
    }

    public void scheduleClientside(Runnable r, int tickDelay) {}

    public void scheduleClientside(Runnable r) {
        this.scheduleClientside(r, 0);
    }

    public void scheduleDelayed(Runnable r, int tickDelay) {
        this.commonScheduler.addRunnable(r, tickDelay);
    }

    public void scheduleDelayed(Runnable r) {
        this.scheduleDelayed(r, 0);
    }

    // GUI stuff

    public void openGuiClient(GuiType type, CompoundNBT data) {
        //No-Op
    }

    public void openGui(PlayerEntity player, GuiType type, Object... data) {
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            PktOpenGui pkt = new PktOpenGui(type, type.serializeArguments(data));
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }
    }

    // Mod events

    private void onCommonSetup(FMLCommonSetupEvent event) {
        this.commonConfig.buildConfiguration();
        this.serverConfig.buildConfiguration();

        RegistryCapabilities.initialize();
        RegistryIngredientTypes.init();
        StarlightNetworkRegistry.setupRegistry();

        RegistryWorldGeneration.registerFeatures();

        PatreonDataManager.loadPatreonEffects();
    }

    private void onEnqueueIMC(InterModEnqueueEvent event) {
        Mods.CURIOS.executeIfPresent(() -> IntegrationCurios::initIMC);
    }

    // Generic events

    private void onClientInitialized(ClientInitializedEvent event) {

    }

    private void onServerStarted(FMLServerStartedEvent event) {
        this.serverLifecycleListeners.forEach(ServerLifecycleListener::onServerStart);
    }

    private void onServerStarting(FMLServerStartingEvent event) {
        CommandAstralSorcery.register(event.getCommandDispatcher());
    }

    private void onServerStopping(FMLServerStoppingEvent event) {
        this.serverLifecycleListeners.forEach(ServerLifecycleListener::onServerStop);
    }

    private void onServerStop(FMLServerStoppedEvent event) {
    }
}
