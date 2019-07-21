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
import hellfirepvp.astralsorcery.client.registry.RegistryEffectTemplates;
import hellfirepvp.astralsorcery.client.registry.RegistrySprites;
import hellfirepvp.astralsorcery.client.registry.RegistryTextures;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetPreLoader;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientProxy
 * Created by HellFirePvP
 * Date: 19.04.2019 / 18:38
 */
public class ClientProxy extends CommonProxy {

    public static boolean connected = false;

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
    }

    @Override
    protected void initializeConfigurations() {
        super.initializeConfigurations();

        this.clientConfig.addConfigEntry(RenderingConfig.CONFIG);
    }

    @Override
    public void attachLifecycle(IEventBus modEventBus) {
        super.attachLifecycle(modEventBus);

        modEventBus.addListener(this::clientSetup);

        modEventBus.addListener(RegistryItems::registerColors);
        modEventBus.addListener(RegistryBlocks::registerColors);
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
    }

    @Override
    public void scheduleClientside(Runnable r, int tickDelay) {
        this.clientScheduler.addRunnable(r, tickDelay);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ClientGuiHandler.registerScreens();
    }

}
