/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.client.registry.*;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalPerkTree;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetPreLoader
 * Created by HellFirePvP
 * Date: 11.07.2019 / 20:29
 */
public class AssetPreLoader implements ISelectiveResourceReloadListener {

    public static final AssetPreLoader INSTANCE = new AssetPreLoader();

    private boolean initialized = false;

    private AssetPreLoader() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
            if (initialized) {
                return;
            }
            RegistryTextures.loadTextures();
            RegistrySprites.loadSprites();
            RegistryRenderTypes.init();
            RegistryEffectTemplates.init();
            RegistryEffectTypes.init();
            ScreenJournalPerkTree.initializeDrawBuffer();

            initialized = true;
        }
    }

}
