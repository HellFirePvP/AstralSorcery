/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.condition.data.ArtifactConditionLoader;
import hellfirepvp.astralsorcery.common.artifact.effect.data.ArtifactEffectLoader;
import hellfirepvp.astralsorcery.common.command.CommandsAS;
import hellfirepvp.astralsorcery.common.config.BaseConfiguration;
import hellfirepvp.astralsorcery.common.config.json.JsonConfigurationManager;
import hellfirepvp.astralsorcery.common.config.json.data.AmuletEnchantmentDataRegistry;
import hellfirepvp.astralsorcery.common.config.json.data.KnownTreeRegistry;
import hellfirepvp.astralsorcery.common.config.json.data.PerkGemModifierRegistry;
import hellfirepvp.astralsorcery.common.config.server.*;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.data.sync.server.CelestialGatewaySyncData;
import hellfirepvp.astralsorcery.common.event.handler.TooltipEventHandler;
import hellfirepvp.astralsorcery.common.event.helper.DamageCancellingHelper;
import hellfirepvp.astralsorcery.common.event.helper.SwordParryHelper;
import hellfirepvp.astralsorcery.common.item.base.CauldronInteractableItem;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.*;
import hellfirepvp.astralsorcery.common.lumen.binding.usage.*;
import hellfirepvp.astralsorcery.common.patreon.PatreonDataManager;
import hellfirepvp.astralsorcery.common.patreon.PatreonManager;
import hellfirepvp.astralsorcery.common.perk.source.provider.lumen.LumenBindingSourceProvider;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentAmuletGenerator;
import hellfirepvp.astralsorcery.common.event.handler.InteractEventHandler;
import hellfirepvp.astralsorcery.common.event.handler.LootEventHandler;
import hellfirepvp.astralsorcery.common.event.handler.PlayerEventHandler;
import hellfirepvp.astralsorcery.common.event.helper.EnchantmentModifierHelper;
import hellfirepvp.astralsorcery.common.event.helper.InvulnerabilityHelper;
import hellfirepvp.astralsorcery.common.event.helper.TemporaryFlightHelper;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.linking.session.LinkSessionHelper;
import hellfirepvp.astralsorcery.common.network.NetworkRegistry;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeLimiter;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.data.PerkTreeLoader;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentSourceProvider;
import hellfirepvp.astralsorcery.common.perk.tick.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.tick.PerkTickHelper;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.init.InitCapabilities;
import hellfirepvp.astralsorcery.common.recipe.attunement.AttunementRecipe;
import hellfirepvp.astralsorcery.common.registry.RegistryBootstrap;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import hellfirepvp.astralsorcery.common.research.io.ResearchIOThread;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLinkHelper;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkTickHelper;
import hellfirepvp.astralsorcery.common.focal.FocalPointManager;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionLevelHelper;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import hellfirepvp.astralsorcery.common.util.TreeGrowUtil;
import hellfirepvp.astralsorcery.common.util.listener.ServerLifecycleListener;
import hellfirepvp.observerlib.common.event.BlockChangeNotifier;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommonProxy
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommonProxy {

    private BaseConfiguration commonConfig;
    private BaseConfiguration serverConfig;
    private BaseConfiguration startupConfig;

    private final List<ServerLifecycleListener> serverLifecycleListeners = new ArrayList<>();

    public void init() {
        this.commonConfig = BaseConfiguration.simple(ModConfig.Type.COMMON);
        this.serverConfig = BaseConfiguration.simple(ModConfig.Type.SERVER);
        this.startupConfig = BaseConfiguration.simple(ModConfig.Type.STARTUP);

        DataAS.init();
        VisualEffectTypes.init();
        AttunementRecipe.initRecipes();

        this.serverLifecycleListeners.add(ResearchIOThread.getInstance());
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> SyncDataManager.getInstance().clear(LogicalSide.SERVER)));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(ResearchManager::clearServerCache));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(LinkSessionHelper::clearServerCache));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(InvulnerabilityHelper::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(DamageCancellingHelper::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(SwordParryHelper::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(LumenBindingAbsorbDamageEffect::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(LumenBindingExtendMobEffectsEffect::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(PerkManager.getInstance()::clearServer));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> PerkAttributeType.clearCache(LogicalSide.SERVER)));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> PerkCooldownHelper.clearCache(LogicalSide.SERVER)));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> PerkTree.getInstance().clearCache(LogicalSide.SERVER)));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> PerkLevelManager.getInstance().clearCache(LogicalSide.SERVER)));
        this.serverLifecycleListeners.add(ServerLifecycleListener.stop(() -> StarlightTransmissionLevelHelper.getInstance().clearServer()));

        this.serverLifecycleListeners.add(ServerLifecycleListener.start(JsonConfigurationManager.getInstance()::loadRegistries));
        this.serverLifecycleListeners.add(ServerLifecycleListener.start(PerkTree.getInstance()::setupServerPerkTree));
        this.serverLifecycleListeners.add(ServerLifecycleListener.start(PerkLevelManager.getInstance()::initializeServerLevels));

        this.initConfigurations();
        this.commonConfig.build();
        this.serverConfig.build();
        this.startupConfig.build();
    }

    public void initConfigurations() {
        this.serverConfig.addConfigEntry(GeneralConfig.CONFIG);
        this.serverConfig.addConfigEntry(PerkConfig.CONFIG);
        PerkConfig.addPerkConfigs();
        this.serverConfig.addConfigEntry(EnchantmentAmuletGenerator.CONFIG);
        this.serverConfig.addConfigEntry(TilesConfig.CONFIG);
        this.serverConfig.addConfigEntry(ArtifactConfig.CONFIG);

        this.startupConfig.addConfigEntry(LootTableConfig.CONFIG);

        TilesConfig.CONFIG.newSubSection(TileTreeBeacon.CONFIG);

        JsonConfigurationManager.getInstance().addRegistry("amulet_enchantments", AmuletEnchantmentDataRegistry.getInstance());
        JsonConfigurationManager.getInstance().addRegistry("perk_gem_modifiers", PerkGemModifierRegistry.getInstance());
        JsonConfigurationManager.getInstance().addRegistry("known_trees", KnownTreeRegistry.getInstance());
    }

    public void initLifecycle(IEventBus modEventBus) {
        //Registry
        RegistryBootstrap.registerDeferredRegisterEvents(modEventBus);
        modEventBus.addListener(InitCapabilities::init);
        modEventBus.addListener(NetworkRegistry::registerPackets);
        modEventBus.addListener(BaseConfiguration::reloadConfigurations);
        modEventBus.addListener(this::onBuildCreativeTabContents);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(EntitiesAS::registerAttributes);

        //Feature
    }

    public void initListeners(IEventBus eventBus) {
        eventBus.addListener(this::onServerStopping);
        eventBus.addListener(this::onServerStarted);
        eventBus.addListener(this::onRegisterCommands);
        eventBus.addListener(this::onRegisterReloadListeners);

        PlayerEventHandler.attachListeners(eventBus);
        TooltipEventHandler.attachListeners(eventBus);
        InteractEventHandler.attachListeners(eventBus);
        LootEventHandler.attachListeners(eventBus);
        InvulnerabilityHelper.attachListeners(eventBus);
        TemporaryFlightHelper.attachListeners(eventBus);
        DamageCancellingHelper.attachListeners(eventBus);
        SwordParryHelper.attachListeners(eventBus);
        SyncDataManager.getInstance().attachEventListeners(eventBus);
        LevelSkyHandler.getInstance().attachEventListeners(eventBus);
        StarlightNetworkTickHelper.getInstance().attachEventListeners(eventBus);
        FocalPointManager.getInstance().attachEventListeners(eventBus);
        ModifierManager.getInstance().attachEventListeners(eventBus);
        PerkManager.getInstance().attachEventListeners(eventBus);
        PerkCooldownHelper.attachEventListeners(eventBus);
        PerkAttributeLimiter.attachEventListeners(eventBus);
        PerkTickHelper.attachEventListeners(eventBus);
        EquipmentSourceProvider.attachEventListeners(eventBus);
        LumenBindingSourceProvider.attachEventListeners(eventBus);
        EnchantmentModifierHelper.attachListeners(eventBus);
        LinkSessionHelper.attachEventListeners(eventBus);
        StarlightTransmissionLevelHelper.getInstance().attachEventListeners(eventBus);
        TreeGrowUtil.attachEventListeners(eventBus);
        CelestialGatewaySyncData.attachListeners(eventBus);
        PatreonManager.attachListeners(eventBus);
        LumenBindingHitAddEffectEffect.attachEventListeners(eventBus);
        LumenBindingPlaceLightEffect.attachEventListeners(eventBus);
        LumenBindingCollectDropsEffect.attachEventListeners(eventBus);
        LumenBindingAbsorbDamageEffect.attachEventListeners(eventBus);
        LumenBindingDamageBurstEffect.attachEventListeners(eventBus);
        LumenBindingExtendMobEffectsEffect.attachEventListeners(eventBus);
        LumenBindingAoeCropGrowthEffect.attachEventListeners(eventBus);
        LumenBindingUsageBlockBreak.attachEventListeners(eventBus);
        LumenBindingUsageDamageDealt.attachEventListeners(eventBus);
        LumenBindingUsageDamageTaken.attachEventListeners(eventBus);
        LumenBindingUsageHealthRecovered.attachEventListeners(eventBus);
        LumenBindingUsageMovement.attachEventListeners(eventBus);

        BlockChangeNotifier.addListener(StarlightNetworkLinkHelper.getInstance());
    }

    public File getServerDataDirectory() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }

        File asDataDir = server.getWorldPath(new LevelResource(AstralSorcery.MODID)).toFile();
        if (!asDataDir.exists()) asDataDir.mkdirs();
        return asDataDir;
    }

    private void onServerStarted(ServerStartedEvent event) {
        this.serverLifecycleListeners.forEach(listener -> listener.onServerStart(event.getServer()));
    }

    private void onServerStopping(ServerStoppingEvent event) {
        this.serverLifecycleListeners.forEach(listener -> listener.onServerStop(event.getServer()));
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        FluidsAS.addLiquidInteractions();
        PatreonDataManager.loadPatreonEffects();
        ItemsAS.ITEM_REGISTER.getEntries().forEach(holder -> {
            holder.asOptional().ifPresent(item -> {
                if (item instanceof DispenseItemBehavior behavior) {
                    DispenserBlock.registerBehavior(item, behavior);
                }
                if (item instanceof CauldronInteractableItem interactableItem) {
                    interactableItem.getInteractionMap().map().put(item, interactableItem.getInteraction());
                }
            });
        });
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandsAS.register(event.getDispatcher());
    }

    private void onRegisterReloadListeners(AddReloadListenerEvent event) {
        event.addListener(ResearchNodeLoader.getInstance());
        event.addListener(PerkTreeLoader.getInstance());
        event.addListener(ArtifactConditionLoader.getInstance());
        event.addListener(ArtifactEffectLoader.getPositiveInstance());
        event.addListener(ArtifactEffectLoader.getNegativeInstance());
        event.addListener(LumenBindingTypeLoader.getInstance());
    }

    private void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ItemsAS.ITEM_REGISTER.getEntries().forEach(holder -> {
            holder.asOptional()
                    .filter(item -> item instanceof CreativeTabItem)
                    .map(item -> (CreativeTabItem) item)
                    .filter(item -> item.isInTab(event.getTab()))
                    .ifPresent(item -> item.fillCreativeTab(event::accept));
        });
    }
}
