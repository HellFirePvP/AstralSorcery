/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.base.Mods;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import static hellfirepvp.astralsorcery.common.base.Mods.ASTRAL_SORCERY;
import static hellfirepvp.astralsorcery.common.base.Mods.CURIOS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TagsAS
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:08
 */
public class TagsAS {

    private TagsAS() {}

    public static class Blocks {

        public static final ITag.INamedTag<Block> MARBLE = blockTagForge("marble");
        public static final ITag.INamedTag<Block> ORES = blockTagForge("ores");

    }

    public static class Items {

        public static final ITag.INamedTag<Item> CURIOS_NECKLACE = itemTag(CURIOS, "necklace");

        public static final ITag.INamedTag<Item> FORGE_GEM_AQUAMARINE = itemTagForge("gems/aquamarine");

        public static final ITag.INamedTag<Item> DUSTS_STARDUST = itemTag(ASTRAL_SORCERY, "stardust");
        public static final ITag.INamedTag<Item> INGOTS_STARMETAL = itemTag(ASTRAL_SORCERY, "starmetal");
        public static final ITag.INamedTag<Item> COLORED_LENS = itemTag(ASTRAL_SORCERY, "colored_lens");

    }

    private static ITag.INamedTag<Block> blockTagForge(String name) {
        return blockTag(Mods.FORGE, name);
    }

    private static ITag.INamedTag<Block> blockTag(Mods mod, String name) {
        return BlockTags.makeWrapperTag(mod.key(name).toString());
    }

    private static ITag.INamedTag<Item> itemTagForge(String name) {
        return itemTag(Mods.FORGE, name);
    }

    private static ITag.INamedTag<Item> itemTag(Mods mod, String name) {
        return ItemTags.makeWrapperTag(mod.key(name).toString());
    }
}
