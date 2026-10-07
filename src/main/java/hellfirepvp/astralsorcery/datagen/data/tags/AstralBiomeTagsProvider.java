/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBiomeTagsProvider
 * Created by HellFirePvP
 * Date: 07.10.2026 / 14:03
 */
public class AstralBiomeTagsProvider extends TagsProvider<Biome> {

    public AstralBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, provider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TagsAS.Biomes.FOCAL_POINT_BIOMES)
                .addTag(Tags.Biomes.IS_OVERWORLD);
        this.tag(TagsAS.Biomes.OBLITERATION_BIOMES)
                .addTag(Tags.Biomes.IS_OVERWORLD)
                .addTag(Tags.Biomes.IS_END);
        this.tag(TagsAS.Biomes.DIG_SITE_BIOMES)
                .addTag(Tags.Biomes.IS_FOREST)
                .addTag(Tags.Biomes.IS_MOUNTAIN)
                .addTag(Tags.Biomes.IS_SAVANNA)
                .addTag(Tags.Biomes.IS_DESERT);
        this.tag(TagsAS.Biomes.MOON_DIAL_BIOMES)
                .addTag(Tags.Biomes.IS_DESERT);
        this.tag(TagsAS.Biomes.COLUMN_BIOMES)
                .addTag(Tags.Biomes.IS_OVERWORLD);
        this.tag(TagsAS.Biomes.ROTUNDA_BIOMES)
                .addTag(Tags.Biomes.IS_MOUNTAIN_PEAK);
    }
}
