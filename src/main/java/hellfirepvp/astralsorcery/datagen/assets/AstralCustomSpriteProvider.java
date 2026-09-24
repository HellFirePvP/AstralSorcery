/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.assets;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralCustomSpriteProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralCustomSpriteProvider extends SpriteSourceProvider {

    public AstralCustomSpriteProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        this.atlas(BLOCKS_ATLAS)
                .addSource(new SingleFile(AstralSorcery.key("model/astrolabe_in_hand"), Optional.empty()));
    }
}
