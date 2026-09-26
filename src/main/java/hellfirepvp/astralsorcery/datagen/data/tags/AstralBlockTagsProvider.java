/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralBlockTagsProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralBlockTagsProvider extends BlockTagsProvider {

    public AstralBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.addMiningTags();

        this.tag(TagsAS.Blocks.MARBLE)
                .add(
                        BlocksAS.MARBLE_ARCH.get(),
                        BlocksAS.MARBLE_BRICKS.get(),
                        BlocksAS.MARBLE_CHISELED.get(),
                        BlocksAS.MARBLE_ENGRAVED.get(),
                        BlocksAS.MARBLE_PILLAR.get(),
                        BlocksAS.MARBLE_RAW.get(),
                        BlocksAS.MARBLE_RUNED.get()
                );
        this.tag(Tags.Blocks.ORES)
                .add(
                        BlocksAS.ROCK_CRYSTAL_ORE.get(),
                        BlocksAS.AQUAMARINE_SHALE.get(),
                        BlocksAS.STARMETAL_ORE.get()
                );
        this.tag(TagsAS.Blocks.SOOTY_MARBLE)
                .add(
                        BlocksAS.SOOTY_MARBLE_ARCH.get(),
                        BlocksAS.SOOTY_MARBLE_BRICKS.get(),
                        BlocksAS.SOOTY_MARBLE_CHISELED.get(),
                        BlocksAS.SOOTY_MARBLE_ENGRAVED.get(),
                        BlocksAS.SOOTY_MARBLE_PILLAR.get(),
                        BlocksAS.SOOTY_MARBLE_RAW.get(),
                        BlocksAS.SOOTY_MARBLE_RUNED.get()
                );
        this.tag(TagsAS.Blocks.INFUSED_WOOD)
                .add(
                        BlocksAS.INFUSED_WOOD_RAW.get(),
                        BlocksAS.INFUSED_WOOD_ARCH.get(),
                        BlocksAS.INFUSED_WOOD_COLUMN.get(),
                        BlocksAS.INFUSED_WOOD_ENGRAVED.get(),
                        BlocksAS.INFUSED_WOOD_ENRICHED.get(),
                        BlocksAS.INFUSED_WOOD_INFUSED.get(),
                        BlocksAS.INFUSED_WOOD_PLANKS.get()
                );

        this.tag(BlockTags.SMALL_FLOWERS)
                .add(
                        BlocksAS.GLIMMER_AMARANTH.get(),
                        BlocksAS.HYACINTH.get(),
                        BlocksAS.IRIS.get(),
                        BlocksAS.ORCHID.get(),
                        BlocksAS.PROTEA.get(),
                        BlocksAS.THISTLE.get()
                );
        this.tag(BlockTags.FLOWER_POTS)
                .add(
                        BlocksAS.POTTED_GLIMMER_AMARANTH.get(),
                        BlocksAS.POTTED_HYACINTH.get(),
                        BlocksAS.POTTED_IRIS.get(),
                        BlocksAS.POTTED_ORCHID.get(),
                        BlocksAS.POTTED_PROTEA.get(),
                        BlocksAS.POTTED_THISTLE.get()
                );

        this.tag(TagsAS.Blocks.VALID_TREE_BEACON_BLOCK)
                .addTag(BlockTags.LEAVES)
                .addTag(BlockTags.LOGS)
                .add(
                        Blocks.VINE,
                        Blocks.MANGROVE_ROOTS,
                        Blocks.MUDDY_MANGROVE_ROOTS
                );

        this.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED)
                .add(
                        BlocksAS.LUMEN_ARRAY.get(),
                        BlocksAS.LUMEN_ALCHEMY_ARRAY.get(),
                        BlocksAS.LUMEN_FILAMENT.get(),
                        BlocksAS.LUMEN_CRYSTALLIZER.get(),
                        BlocksAS.LENS.get(),
                        BlocksAS.PRISM.get(),
                        BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL.get(),
                        BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL.get(),
                        BlocksAS.STELLAR_FILAMENT.get(),
                        BlocksAS.INFUSER.get(),
                        BlocksAS.ATTUNEMENT_ALTAR.get(),
                        BlocksAS.TREE_BEACON.get(),
                        BlocksAS.CELESTIAL_GATEWAY.get(),
                        BlocksAS.ALTAR_ILLUMINATION.get(),
                        BlocksAS.ALTAR_RESONANCE.get(),
                        BlocksAS.ALTAR_LUMINANCE.get(),
                        BlocksAS.ALTAR_RADIANCE.get()
                );
        this.tag(TagsAS.Blocks.SIMULATED_NON_MOVEABLE)
                .add(
                        BlocksAS.LUMEN_ARRAY.get(),
                        BlocksAS.LUMEN_ALCHEMY_ARRAY.get(),
                        BlocksAS.LUMEN_FILAMENT.get(),
                        BlocksAS.LUMEN_CRYSTALLIZER.get(),
                        BlocksAS.LENS.get(),
                        BlocksAS.PRISM.get(),
                        BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL.get(),
                        BlocksAS.STARLIGHT_FOCUS_CELESTIAL_CRYSTAL.get(),
                        BlocksAS.STELLAR_FILAMENT.get(),
                        BlocksAS.INFUSER.get(),
                        BlocksAS.ATTUNEMENT_ALTAR.get(),
                        BlocksAS.TREE_BEACON.get(),
                        BlocksAS.CELESTIAL_GATEWAY.get(),
                        BlocksAS.ALTAR_ILLUMINATION.get(),
                        BlocksAS.ALTAR_RESONANCE.get(),
                        BlocksAS.ALTAR_LUMINANCE.get(),
                        BlocksAS.ALTAR_RADIANCE.get()
                );
    }

    private void addMiningTags() {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        BlocksAS.MARBLE_ARCH.get(),
                        BlocksAS.MARBLE_BRICKS.get(),
                        BlocksAS.MARBLE_CHISELED.get(),
                        BlocksAS.MARBLE_ENGRAVED.get(),
                        BlocksAS.MARBLE_PILLAR.get(),
                        BlocksAS.MARBLE_RAW.get(),
                        BlocksAS.MARBLE_RUNED.get(),
                        BlocksAS.MARBLE_SLAB.get(),
                        BlocksAS.MARBLE_STAIRS.get(),
                        BlocksAS.SOOTY_MARBLE_ARCH.get(),
                        BlocksAS.SOOTY_MARBLE_BRICKS.get(),
                        BlocksAS.SOOTY_MARBLE_CHISELED.get(),
                        BlocksAS.SOOTY_MARBLE_ENGRAVED.get(),
                        BlocksAS.SOOTY_MARBLE_PILLAR.get(),
                        BlocksAS.SOOTY_MARBLE_RAW.get(),
                        BlocksAS.SOOTY_MARBLE_RUNED.get(),
                        BlocksAS.SOOTY_MARBLE_SLAB.get(),
                        BlocksAS.SOOTY_MARBLE_STAIRS.get(),

                        BlocksAS.ROCK_CRYSTAL_ORE.get(),
                        BlocksAS.STARMETAL_ORE.get(),
                        BlocksAS.RAW_STARMETAL_BLOCK.get(),

                        BlocksAS.ALTAR_ILLUMINATION.get(),
                        BlocksAS.ALTAR_RESONANCE.get(),
                        BlocksAS.ALTAR_LUMINANCE.get(),
                        BlocksAS.ALTAR_RADIANCE.get(),
                        BlocksAS.FOCUS_RELAY.get(),
                        BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get(),
                        BlocksAS.GEM_CRYSTAL_CLUSTER.get(),
                        BlocksAS.LUMEN_CRYSTAL_CLUSTER.get(),
                        BlocksAS.LUMEN_ARRAY.get(),
                        BlocksAS.LUMEN_ALCHEMY_ARRAY.get(),
                        BlocksAS.LUMEN_CRYSTALLIZER.get(),
                        BlocksAS.LIGHTWELL.get(),
                        BlocksAS.INFUSER.get(),
                        BlocksAS.CHALICE.get(),
                        BlocksAS.ATTUNEMENT_ALTAR.get(),
                        BlocksAS.STELLAR_FILAMENT.get(),
                        BlocksAS.CELESTIAL_GATEWAY.get(),
                        BlocksAS.CAVE_ILLUMINATOR.get()
                );
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        BlocksAS.INFUSED_WOOD_RAW.get(),
                        BlocksAS.INFUSED_WOOD_ARCH.get(),
                        BlocksAS.INFUSED_WOOD_COLUMN.get(),
                        BlocksAS.INFUSED_WOOD_ENGRAVED.get(),
                        BlocksAS.INFUSED_WOOD_ENRICHED.get(),
                        BlocksAS.INFUSED_WOOD_INFUSED.get(),
                        BlocksAS.INFUSED_WOOD_PLANKS.get(),
                        BlocksAS.INFUSED_WOOD_SLAB.get(),
                        BlocksAS.INFUSED_WOOD_STAIRS.get(),

                        BlocksAS.TREE_BEACON.get()
                );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        BlocksAS.AQUAMARINE_SHALE.get()
                );

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        BlocksAS.MARBLE_ARCH.get(),
                        BlocksAS.MARBLE_BRICKS.get(),
                        BlocksAS.MARBLE_CHISELED.get(),
                        BlocksAS.MARBLE_ENGRAVED.get(),
                        BlocksAS.MARBLE_PILLAR.get(),
                        BlocksAS.MARBLE_RAW.get(),
                        BlocksAS.MARBLE_RUNED.get(),
                        BlocksAS.MARBLE_SLAB.get(),
                        BlocksAS.MARBLE_STAIRS.get(),
                        BlocksAS.SOOTY_MARBLE_ARCH.get(),
                        BlocksAS.SOOTY_MARBLE_BRICKS.get(),
                        BlocksAS.SOOTY_MARBLE_CHISELED.get(),
                        BlocksAS.SOOTY_MARBLE_ENGRAVED.get(),
                        BlocksAS.SOOTY_MARBLE_PILLAR.get(),
                        BlocksAS.SOOTY_MARBLE_RAW.get(),
                        BlocksAS.SOOTY_MARBLE_RUNED.get(),
                        BlocksAS.SOOTY_MARBLE_SLAB.get(),
                        BlocksAS.SOOTY_MARBLE_STAIRS.get(),

                        BlocksAS.AQUAMARINE_SHALE.get(),
                        BlocksAS.FOCUS_RELAY.get(),
                        BlocksAS.STELLAR_FILAMENT.get(),
                        BlocksAS.CAVE_ILLUMINATOR.get()
                );
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        BlocksAS.ROCK_CRYSTAL_ORE.get(),
                        BlocksAS.STARMETAL_ORE.get(),
                        BlocksAS.RAW_STARMETAL_BLOCK.get(),

                        BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get(),
                        BlocksAS.GEM_CRYSTAL_CLUSTER.get(),
                        BlocksAS.LUMEN_CRYSTAL_CLUSTER.get(),
                        BlocksAS.ALTAR_ILLUMINATION.get(),
                        BlocksAS.ALTAR_RESONANCE.get(),
                        BlocksAS.ALTAR_LUMINANCE.get(),
                        BlocksAS.ALTAR_RADIANCE.get(),
                        BlocksAS.LUMEN_ARRAY.get(),
                        BlocksAS.LUMEN_ALCHEMY_ARRAY.get(),
                        BlocksAS.LUMEN_CRYSTALLIZER.get(),
                        BlocksAS.LIGHTWELL.get(),
                        BlocksAS.INFUSER.get(),
                        BlocksAS.CHALICE.get(),
                        BlocksAS.ATTUNEMENT_ALTAR.get(),
                        BlocksAS.TREE_BEACON.get(),
                        BlocksAS.CELESTIAL_GATEWAY.get()
                );
    }
}
