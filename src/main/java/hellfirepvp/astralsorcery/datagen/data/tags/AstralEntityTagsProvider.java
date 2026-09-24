/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralEntityTagsProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralEntityTagsProvider extends EntityTypeTagsProvider {

    public AstralEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED)
                .add(EntitiesAS.FLARE.get())
                .add(EntitiesAS.ILLUMINATION_SPARK.get())
                .add(EntitiesAS.NOCTURNAL_SPARK.get())
                .add(EntitiesAS.VIVID_SPARK.get());

        tag(Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED)
                .add(EntitiesAS.FLARE.get())
                .add(EntitiesAS.ILLUMINATION_SPARK.get())
                .add(EntitiesAS.NOCTURNAL_SPARK.get())
                .add(EntitiesAS.VIVID_SPARK.get());
    }
}
