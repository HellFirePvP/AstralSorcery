/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.tags;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralItemTagsProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralItemTagsProvider extends ItemTagsProvider {

    public AstralItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AstralSorcery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(TagsAS.Blocks.INFUSED_WOOD, TagsAS.Items.INFUSED_WOOD);
        copy(TagsAS.Blocks.MARBLE, TagsAS.Items.MARBLE);
        copy(TagsAS.Blocks.SOOTY_MARBLE, TagsAS.Items.SOOTY_MARBLE);
        tag(TagsAS.Items.GEMS_AQUAMARINE)
                .add(ItemsAS.AQUAMARINE.asItem());
        tag(Tags.Items.RAW_MATERIALS)
                .add(ItemsAS.RAW_STARMETAL.asItem());

        tag(ItemTags.LECTERN_BOOKS)
                .add(ItemsAS.TOME.asItem());

        tag(TagsAS.Items.CRYSTAL)
                .addTag(TagsAS.Items.CELESTIAL_CRYSTAL)
                .addTag(TagsAS.Items.ATTUNED_CRYSTAL)
                .add(ItemsAS.ROCK_CRYSTAL.asItem());
        tag(TagsAS.Items.CELESTIAL_CRYSTAL)
                .add(ItemsAS.CELESTIAL_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL.asItem());
        tag(TagsAS.Items.ROCK_CRYSTAL)
                .add(ItemsAS.ROCK_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_ROCK_CRYSTAL.asItem());
        tag(TagsAS.Items.ATTUNED_CRYSTAL)
                .add(ItemsAS.ATTUNED_ROCK_CRYSTAL.asItem())
                .add(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL.asItem());

        tag(TagsAS.Items.FUNCTIONAL_ALTAR_CONSTELLATION_ITEM)
                .addTag(TagsAS.Items.ATTUNED_CRYSTAL);
        tag(TagsAS.Items.FUNCTIONAL_ATTUNEABLE_ITEM)
                .add(ItemsAS.ROCK_CRYSTAL.asItem())
                .add(ItemsAS.CELESTIAL_CRYSTAL.asItem());
        tag(TagsAS.Items.FUNCTIONAL_PERKTREE_SOCKETABLE_ITEM)
                .add(ItemsAS.DYNAMISM_GEM_SKY.asItem())
                .add(ItemsAS.DYNAMISM_GEM_DAY.asItem())
                .add(ItemsAS.DYNAMISM_GEM_NIGHT.asItem());

        tag(TagsAS.Items.CURIOS_NECKLACE)
                .add(ItemsAS.ENCHANTMENT_AMULET.asItem());

        tag(ItemTags.AXES)
                .add(ItemsAS.CRYSTAL_AXE.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_AXE.asItem());
        tag(ItemTags.PICKAXES)
                .add(ItemsAS.CRYSTAL_PICKAXE.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_PICKAXE.asItem());
        tag(ItemTags.SHOVELS)
                .add(ItemsAS.CRYSTAL_SHOVEL.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_SHOVEL.asItem());
        tag(ItemTags.SWORDS)
                .add(ItemsAS.CRYSTAL_SWORD.asItem())
                .add(ItemsAS.IRIDESCENT_CRYSTAL_SWORD.asItem());

        tag(Tags.Items.INGOTS)
                .add(ItemsAS.STARMETAL_INGOT.asItem());
    }
}
