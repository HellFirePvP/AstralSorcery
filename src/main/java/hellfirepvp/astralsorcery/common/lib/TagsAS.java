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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

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

        public static final Tag<Block> MARBLE = blockTagForge("marble");
        public static final Tag<Block> ORES = blockTagForge("ores");

    }

    public static class Items {

        public static final Tag<Item> CURIOS_NECKLACE = itemTag(CURIOS, "necklace");

        public static final Tag<Item> DUSTS_STARDUST = itemTag(ASTRAL_SORCERY, "stardust");
        public static final Tag<Item> INGOTS_STARMETAL = itemTag(ASTRAL_SORCERY, "starmetal");
        public static final Tag<Item> COLORED_LENS = itemTag(ASTRAL_SORCERY, "colored_lens");

    }

    private static Tag<Block> blockTag(Mods mod, String name) {
        return new BlockTags.Wrapper(new ResourceLocation(mod.getModId(), name));
    }

    private static Tag<Block> blockTagForge(String name) {
        return new BlockTags.Wrapper(new ResourceLocation("forge", name));
    }

    private static Tag<Item> itemTag(Mods mod, String name) {
        return new ItemTags.Wrapper(new ResourceLocation(mod.getModId(), name));
    }

    private static Tag<Item> itemTagForge(String name) {
        return new ItemTags.Wrapper(new ResourceLocation("forge", name));
    }
}
