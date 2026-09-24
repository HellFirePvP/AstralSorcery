/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.init;

import hellfirepvp.astralsorcery.client.lib.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AssetInitializer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AssetInitializer implements ResourceManagerReloadListener {

    private static final AssetInitializer INSTANCE = new AssetInitializer();

    private boolean initialized = false;

    private AssetInitializer() {}

    public static AssetInitializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (this.initialized) return;

        TexturesAS.init();
        SpritesAS.init();
        RenderTypesAS.init();
        EffectTemplatesAS.init();
        ShaderProgramsAS.init();

        this.initialized = true;
    }
}
