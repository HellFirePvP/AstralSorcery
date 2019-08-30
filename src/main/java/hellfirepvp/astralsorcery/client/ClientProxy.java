/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetPreLoader;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournal;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalConstellationOverview;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.client.screen.journal.bookmark.BookmarkProvider;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.base.patreon.manager.PatreonManagerClient;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.registry.RegistryContainerTypes;
import hellfirepvp.astralsorcery.common.registry.RegistryEntities;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

        OBJLoader.INSTANCE.addDomain(AstralSorcery.MODID);

        this.clientConfig = new ClientConfig();

        super.initialize();

        this.clientConfig.buildConfiguration();

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
    }

    @Override
    public void attachEventHandlers(IEventBus eventBus) {
        super.attachEventHandlers(eventBus);

        EffectRenderEventHandler.getInstance().attachEventListeners(eventBus);
        ConnectionEventHandler.getInstance().attachEventListeners(eventBus);
    }

    @Override
    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        super.attachTickListeners(registrar);

        registrar.accept(this.clientScheduler);
        registrar.accept(RenderInfo.getInstance());
        registrar.accept(EffectUpdater.getInstance());
        registrar.accept(PatreonManagerClient.INSTANCE);

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

    private void onClientSetup(FMLClientSetupEvent event) {
        RegistryContainerTypes.initClient();
        RegistryEntities.initClient();

        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .addReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) ->
                        RegistriesAS.REGISTRY_PERKS.forEach(AbstractPerk::clearClientCaches));
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
