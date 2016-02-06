package hellfire.astralSorcery.common;

import hellfire.astralSorcery.api.AstralSorceryAPI;
import hellfire.astralSorcery.common.api.DefaultAPIHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static hellfire.astralSorcery.common.lib.LibConstants.*;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:31
 */
@Mod(modid = MODID, name = NAME, version = VERSION)
public class AstralSorcery {

    @Mod.Instance(MODID)
    public static AstralSorcery instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    public static Logger log = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void onConstruct(FMLConstructionEvent event) {
        AstralSorceryAPI.setAPIHandler(new DefaultAPIHandler());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

}
