package hellfire.astralSorcery.common.integration;

import hellfire.astralSorcery.common.AstralSorcery;
import net.minecraftforge.fml.common.Loader;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 21:23
 */
public class IntegrationHandler {

    public static void initIntegrations() {

    }

    private static <T extends Integration> T registerIntegration(String modid, String classPath) {
        AstralSorcery.log.info("Checking integration for " + modid);

        if(!Loader.isModLoaded(modid)) {
            return null;
        }

        T integration;
        try {
            integration = (T) Class.forName(classPath).newInstance();
        } catch (Throwable tr) {
            AstralSorcery.log.error("Integration for could not be initialized!");
            return null;
        }
        
        integration.init();
        AstralSorcery.log.info("Integration for " + modid + " loaded!");
        return integration;
    }

}
