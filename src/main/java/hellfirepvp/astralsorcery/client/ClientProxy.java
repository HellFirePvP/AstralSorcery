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
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;

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

    private ClientGuiHandler guiHandler;
    private ClientScheduler clientScheduler;

    private ClientConfig clientConfig;

    @Override
    public void initialize() {
        this.guiHandler = new ClientGuiHandler();
        AstralSorcery.getModContainer().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> this.guiHandler);
        this.clientScheduler = new ClientScheduler();

        try {
            ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(AssetLibrary.INSTANCE);
        } catch (Exception exc) {
            AstralSorcery.log.warn("Could not add AssetLibrary to resource manager! Texture reloading will have no effect on AstralSorcery textures.");
            AssetLibrary.INSTANCE.onResourceManagerReload(null);
        }
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
    }

    @Override
    public void attachEventHandlers(IEventBus eventBus) {
        super.attachEventHandlers(eventBus);
    }

    @Override
    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        super.attachTickListeners(registrar);

        registrar.accept(this.clientScheduler);
    }

}
