/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.data.config.ClientConfig;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.handler.EffectUpdater;
import hellfirepvp.astralsorcery.client.event.ConnectionEventHandler;
import hellfirepvp.astralsorcery.client.event.EffectRenderEventHandler;
import hellfirepvp.astralsorcery.client.event.LightbeamRenderHelper;
import hellfirepvp.astralsorcery.client.event.SkyRenderEventHandler;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetPreLoader;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalConstellationOverview;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.ColorizationHelper;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.base.patreon.manager.PatreonManagerClient;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.registry.*;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:38
 */
public class ClientProxy extends CommonProxy {

    private ClientScheduler clientScheduler;

    private ClientConfig clientConfig;

    @Override
    public void initialize() {
        this.clientScheduler = new ClientScheduler();

        IReloadableResourceManager resMgr = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        resMgr.addReloadListener(AssetLibrary.INSTANCE);
        resMgr.addReloadListener(AssetPreLoader.INSTANCE);
        resMgr.addReloadListener(ColorizationHelper.onReload());

        OBJLoader.INSTANCE.addDomain(AstralSorcery.MODID);

        this.clientConfig = new ClientConfig();

        super.initialize();

        this.addTomeBookmarks();
    }

    @Override
    protected void initializeConfigurations() {
        super.initializeConfigurations();

        this.clientConfig.addConfigEntry(RenderingConfig.CONFIG);
    }

    @Override
    public void attachLifecycle(IEventBus modEventBus) {
        super.attachLifecycle(modEventBus);

        modEventBus.addListener(RegistryItems::registerColors);
        modEventBus.addListener(RegistryBlocks::registerColors);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::stitchBucketTextures);
        modEventBus.addListener(this::onModelBake);
    }

    @Override
    public void attachEventHandlers(IEventBus eventBus) {
        super.attachEventHandlers(eventBus);

        EffectRenderEventHandler.getInstance().attachEventListeners(eventBus);
        ConnectionEventHandler.getInstance().attachEventListeners(eventBus);

        eventBus.addListener(EventPriority.LOWEST, SkyRenderEventHandler::onRender);
        eventBus.addListener(EventPriority.LOWEST, SkyRenderEventHandler::onFog);
    }

    @Override
    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        super.attachTickListeners(registrar);

        registrar.accept(this.clientScheduler);
        registrar.accept(RenderInfo.getInstance());
        registrar.accept(EffectUpdater.getInstance());
        registrar.accept(PatreonManagerClient.INSTANCE);
        registrar.accept(ClientCameraManager.INSTANCE);

        LightbeamRenderHelper.attachTickListener(registrar);
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        this.clientScheduler.addRunnable(r, tickDelay);
    }

    @Override
    public void openGuiClient(GuiType type, CompoundNBT data) {
        Minecraft.getInstance().displayGuiScreen(type.deserialize(data));
    }

    @Override
    public void openGui(PlayerEntity player, GuiType type, Object... data) {
        if (player instanceof AbstractClientPlayerEntity) {
            openGuiClient(type, type.serializeArguments(data));
            return;
        }
        super.openGui(player, type, data);
    }

    // Append custom textures otherwise not referenced
    private void stitchBucketTextures(TextureStitchEvent.Pre event) {
        if (event.getMap().getBasePath().equals("textures")) {
            event.addSprite(AstralSorcery.key("fluid/bucket_mask"));
        }
    }

    private void onModelBake(ModelBakeEvent event) {
        //Returns actually a SimpleModelState, which is however both an IModelState and ISprite
        ISprite bucketTransforms = (ISprite) ForgeBlockStateV1.Transforms.get("forge:default-item")
                .orElseThrow(() -> new IllegalStateException("Forge ModelTransforms not initialized!"));

        RegistryFluids.registerFluidBucketRender((bucketModel, modelName) -> {
            IBakedModel baked = bucketModel.bake(event.getModelLoader(), Minecraft.getInstance().getTextureMap()::getSprite, bucketTransforms, DefaultVertexFormats.ITEM);
            event.getModelRegistry().put(modelName, baked);
        });
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        this.clientConfig.buildConfiguration();

        RegistryContainerTypes.initClient();
        RegistryEntities.initClient();
        RegistryTileEntities.initClient();

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .addReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) ->
                        RegistriesAS.REGISTRY_PERKS.forEach(AbstractPerk::clearClientResourceCaches));
    }

    private void addTomeBookmarks() {
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.progression", 10,
                ScreenJournalProgression::getJournalInstance,
                () -> true));
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.constellations", 20,
                ScreenJournalConstellationOverview::getConstellationScreen,
                () -> !ResearchHelper.getClientProgress().getSeenConstellations().isEmpty()));
        ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.perks", 30,
                ScreenJournalPerkTree::new,
                () -> ResearchHelper.getClientProgress().getAttunedConstellation() != null));
        //TODO knowledge fragment gui
        //ScreenJournal.addBookmark(new BookmarkProvider("screen.astralsorcery.tome.knowledge", 40,
        //        GuiJournalKnowledgeIndex::new,
        //        () -> !((KnowledgeFragmentData) PersistentDataManager.INSTANCE
        //                .getData(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS))
        //                .getAllFragments().isEmpty()));
    }

}
