/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.constants;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.Mods;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TagsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TagsAS {

    private TagsAS() {}

    public static class Blocks {

        public static final TagKey<Block> INFUSED_WOOD = blockTag("infused_woods");
        public static final TagKey<Block> MARBLE = blockTagCommon("marbles");
        public static final TagKey<Block> SOOTY_MARBLE = blockTag("sooty_marbles");

        public static final TagKey<Block> VALID_TREE_BEACON_BLOCK = blockTag("valid_tree_beacon_block");

        public static final TagKey<Block> SIMULATED_NON_MOVEABLE = BlockTags.create(Mods.SIMULATED.key("non_movable"));

    }

    public static class Items {

        public static final TagKey<Item> GEMS_AQUAMARINE = itemTagCommon("gems/aquamarine");

        public static final TagKey<Item> INFUSED_WOOD = itemTag("infused_woods");
        public static final TagKey<Item> MARBLE = itemTagCommon("marbles");
        public static final TagKey<Item> SOOTY_MARBLE = itemTag("sooty_marbles");

        public static final TagKey<Item> CRYSTAL = itemTag("crystal");
        public static final TagKey<Item> ROCK_CRYSTAL = itemTag("rock_crystal");
        public static final TagKey<Item> CELESTIAL_CRYSTAL = itemTag("celestial_crystal");
        public static final TagKey<Item> ATTUNED_CRYSTAL = itemTag("attuned_crystal");

        public static final TagKey<Item> FUNCTIONAL_ALTAR_CONSTELLATION_ITEM = itemTag("functional_altar_constellation_item");
        public static final TagKey<Item> FUNCTIONAL_ATTUNEABLE_ITEM = itemTag("functional_attuneable_item");
        public static final TagKey<Item> FUNCTIONAL_PERKTREE_SOCKETABLE_ITEM = itemTag("functional_perktree_socketable_item");

        public static final TagKey<Item> CURIOS_NECKLACE = ItemTags.create(Mods.CURIOS.key("necklace"));

    }

    public static class DamageTypes {

        public static final TagKey<DamageType> IS_ELEMENTAL = damageTypeTag("is_elemental");

    }

    public static class Constellations {

        public static final TagKey<BaseConstellation> MAY_BE_FOCAL_POINT =
                TagKey.create(RegistriesAS.KEY_CONSTELLATIONS, AstralSorcery.key("may_be_focal_point"));

    }

    private static TagKey<Block> blockTagCommon(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
    }

    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(AstralSorcery.key(name));
    }

    private static TagKey<Item> itemTagCommon(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
    }

    private static TagKey<Item> itemTag(String name) {
        return ItemTags.create(AstralSorcery.key(name));
    }

    private static TagKey<DamageType> damageTypeTag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, AstralSorcery.key(name));
    }
}
