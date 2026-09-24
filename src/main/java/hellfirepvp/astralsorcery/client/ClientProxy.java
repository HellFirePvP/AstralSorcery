/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.config.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.helper.*;
import hellfirepvp.astralsorcery.client.init.AssetInitializer;
import hellfirepvp.astralsorcery.client.init.InitBlockRenderTypes;
import hellfirepvp.astralsorcery.client.init.InitItemProperties;
import hellfirepvp.astralsorcery.client.lib.*;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.tome.*;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.data.LumenDisplayPositionLoader;
import hellfirepvp.astralsorcery.client.screen.tome.page.*;
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationPositionLoader;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.client.util.camera.CameraManager;
import hellfirepvp.astralsorcery.client.util.structure.StructurePreviewHelper;
import hellfirepvp.astralsorcery.client.util.tooltip.ArtifactDecoratedClientComponent;
import hellfirepvp.astralsorcery.client.util.tooltip.ItemStackClientComponent;
import hellfirepvp.astralsorcery.client.util.tooltip.StoredLumenClientComponent;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.config.BaseConfiguration;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.ingredient.HasStoredLumenIngredient;
import hellfirepvp.astralsorcery.common.ingredient.IsEnchantedIngredient;
import hellfirepvp.astralsorcery.common.ingredient.IsLumenBindableIngredient;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.patreon.PatreonManagerClient;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.tick.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import hellfirepvp.astralsorcery.common.util.tooltip.ArtifactDecoratedTooltip;
import hellfirepvp.astralsorcery.common.util.tooltip.ItemStackTooltip;
import hellfirepvp.astralsorcery.common.util.level.LevelEffectSeedCache;
import hellfirepvp.astralsorcery.common.util.listener.ClientLifecycleListener;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ClientProxy extends CommonProxy {

    private final TimeoutList<Runnable> effectTasks = new TimeoutList<>(Runnable::run);
    private final List<ClientLifecycleListener> clientLifecycleListeners = new ArrayList<>();
    private static long clientTick = 0;

    private BaseConfiguration clientConfig;

    @Override
    public void init() {
        this.clientConfig = BaseConfiguration.simple(ModConfig.Type.CLIENT);

        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(LevelEffectSeedCache::clearClient));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(LevelSkyHandler.getInstance()::clientClearCache));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(() -> SyncDataManager.getInstance().clear(LogicalSide.CLIENT)));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(ClientLinkHelper::clearActiveSession));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(CameraManager.getInstance()::clearTransformers));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(EffectHandler.getInstance()::clearAllEffects));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(ScreenEffectTicketManager.getInstance()::clearAllEffects));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(TomeResearchScreen::resetOpenTome));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(PerkManager::clientClearAllPerks));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(ModifierManager::clearClientCache));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(PerkManager.getInstance()::clearClient));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(LumenBindingTypeLoader.getInstance()::clearClientBindings));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(() -> PerkAttributeType.clearCache(LogicalSide.CLIENT)));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(() -> PerkCooldownHelper.clearCache(LogicalSide.CLIENT)));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(() -> PerkTree.getInstance().clearCache(LogicalSide.CLIENT)));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(() -> PerkLevelManager.getInstance().clearCache(LogicalSide.CLIENT)));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(IsLumenBindableIngredient::clearDisplayCache));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(IsEnchantedIngredient::clearDisplayCache));
        this.clientLifecycleListeners.add(ClientLifecycleListener.disconnect(HasStoredLumenIngredient::clearDisplayCache));

        super.init();

        TomeScreen.addBookmark(TomeResearchScreen.BOOKMARK);
        TomeScreen.addBookmark(TomeConstellationScreen.BOOKMARK);
        TomeScreen.addBookmark(TomePerkTreeScreen.BOOKMARK);
        TomeScreen.addBookmark(TomeLumenScreen.BOOKMARK);

        RenderPageRecipe.registerPageFactory(RecipeTypesAS.LUMEN_GENERATION_TYPE.getKey(), RenderPageLumenGeneration::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.getKey(), RenderPageLumenCrystallization::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.getKey(), RenderPageFocalTransmutation::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.FOCAL_COMBINE_TYPE.getKey(), RenderPageFocalCombination::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.LIGHTWELL_TYPE.getKey(), RenderPageLightwell::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.INFUSION_TYPE.getKey(), RenderPageStarlightInfusion::new);
        RenderPageRecipe.registerPageFactory(RecipeTypesAS.ALTAR_CRAFTING_TYPE.getKey(), RenderPageAltar::new);
        RenderPageRecipe.registerPageFactory(ResourceKey.create(Registries.RECIPE_TYPE, ResourceLocation.withDefaultNamespace("crafting")), RenderPageCraftingRecipe::new);

        this.clientConfig.build();
    }

    @Override
    public void initConfigurations() {
        super.initConfigurations();

        this.clientConfig.addConfigEntry(RenderingConfig.CONFIG);
    }

    @Override
    public void initLifecycle(IEventBus modEventBus) {
        super.initLifecycle(modEventBus);

        modEventBus.addListener(this::onRegisterClientReloadListeners);
        modEventBus.addListener(this::onItemColorSetup);
        modEventBus.addListener(this::onBlockColorSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(TexturesAS::registerAtlases);
        modEventBus.addListener(ShadersAS::registerShaders);
        modEventBus.addListener(CustomModelsAS::registerCustomModels);
        modEventBus.addListener(RenderersAS::registerTileEntityRenders);
        modEventBus.addListener(ModelLayersAS::registerModelLayers);
        modEventBus.addListener(ClientExtensionsAS::registerExtensions);
        modEventBus.addListener(RenderLumenDisplayOverlay::registerLayers);
        modEventBus.addListener(RenderPerkExperienceOverlay::registerLayers);
        modEventBus.addListener(MenuScreensAS::registerScreens);

        modEventBus.addListener(this::registerCustomComponents);
    }

    @Override
    public void initListeners(IEventBus eventBus) {
        super.initListeners(eventBus);

        eventBus.addListener(this::onClientConnect);
        eventBus.addListener(this::onClientDisconnect);
        eventBus.addListener(this::onClientTick);
        eventBus.addListener(this.effectTasks::onClientTick);

        eventBus.addListener(EffectHandler.getInstance()::displayDebug);
        eventBus.addListener(EffectHandler.getInstance()::tick);
        eventBus.addListener(ScreenEffectTicketManager.getInstance()::tick);
        eventBus.addListener(TooltipUtil.getInstance()::colorTooltip);
        eventBus.addListener(TooltipUtil.getInstance()::tooltipContext);
        eventBus.addListener(RenderAstrolabeOverlay::astrolabeMouseScroll);
        eventBus.addListener(RenderAstrolabeOverlay::overrideFov);
        eventBus.addListener(RenderAstrolabeOverlay::preventScreenOpen);
        eventBus.addListener(RenderAstrolabeOverlay::overrideMouseClickDuringDrawing);
        eventBus.addListener(FocalPointEffectHelper::onClientTick);
        eventBus.addListener(CameraManager.getInstance()::onClientTick);
        eventBus.addListener(CameraManager.getInstance()::onRenderTick);
        eventBus.addListener(CameraManager.getInstance()::onMouseInput);
        eventBus.addListener(GatewayInterfaceRenderHelper::onClientTick);
        eventBus.addListener(RenderPerkExperienceOverlay::onClientTick);
        eventBus.addListener(TomeLumenScreen::recipesSyncedFromServer);
        eventBus.addListener(StructurePreviewHelper::renderPreview);
        eventBus.addListener(StructurePreviewHelper::tickPreview);

        LinkSessionEffectHelper.attachEventListeners(eventBus);
        StarlightTransmissionEffectHelper.attachEventListeners(eventBus);
        StoredLumenTooltipHelper.attachEventListeners(eventBus);
        ArtifactTooltipHelper.attachEventListeners(eventBus);
        GatewayInterfaceInteractHelper.attachEventListeners(eventBus);
        WandPreviewRenderHelper.attachEventListeners(eventBus);
        PatreonManagerClient.attachListeners(eventBus);
    }

    private void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(SkyConstellationPositionLoader.getInstance());
        event.registerReloadListener(LumenDisplayPositionLoader.getInstance());
        event.registerReloadListener(AssetLibrary.getInstance());
        event.registerReloadListener(AssetInitializer.getInstance());
        event.registerReloadListener(ColorExtractUtil.reload());
        event.registerReloadListener((stage, resMgr, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                stage.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    PerkTree.getInstance().getPerkPoints(LogicalSide.CLIENT).stream()
                            .map(PerkTreePoint::getPerk)
                            .forEach(AbstractPerk::clearTooltipCache);
        }));
    }

    private void onItemColorSetup(RegisterColorHandlersEvent.Item event) {
        ItemsAS.ITEM_REGISTER.getEntries().forEach(item -> {
            if (item.get() instanceof ItemDynamicColor dynamicColorItem) {
                event.register((stack, tint) -> dynamicColorItem.getColor(stack, ClientProxy.getClientTick(), tint), dynamicColorItem);
            }
        });
    }

    private void onBlockColorSetup(RegisterColorHandlersEvent.Block event) {
        BlocksAS.BLOCK_REGISTER.getEntries().forEach(block -> {
            if (block.get() instanceof BlockDynamicColor dynamicColorBlock) {
                event.register((state, level, pos, tintIndex) -> dynamicColorBlock.getColor(state, ClientProxy.getClientTick(), level, pos, tintIndex), block.get());
            }
        });
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        InitBlockRenderTypes.init();
        InitItemProperties.init();
    }

    private void registerCustomComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStackTooltip.class, ItemStackClientComponent::create);
        event.register(StoredLumenDisplayTooltip.class, StoredLumenClientComponent::create);
        event.register(ArtifactDecoratedTooltip.class, ArtifactDecoratedClientComponent::create);
    }

    private void onClientConnect(ClientPlayerNetworkEvent.LoggingIn event) {
        this.clientLifecycleListeners.forEach(ClientLifecycleListener::onClientConnect);
    }

    private void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        this.clientLifecycleListeners.forEach(ClientLifecycleListener::onClientDisconnect);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        clientTick++;
    }

    public static long getClientTick() {
        return clientTick;
    }

    public static void scheduleEffectTask(Runnable task) {
        scheduleEffectTask(1, task);
    }

    public static void scheduleEffectTask(int delay, Runnable task) {
        if (AstralSorcery.getInstance().getProxy() instanceof ClientProxy clientProxy) {
            clientProxy.effectTasks.add(delay, task);
        }
    }
}
