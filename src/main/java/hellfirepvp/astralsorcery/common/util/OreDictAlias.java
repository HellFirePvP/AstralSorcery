/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.EnumDyeColor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreDictAlias
 * Created by HellFirePvP
 * Date: 26.12.2016 / 18:20
 */
public class OreDictAlias {

    private static final String[] DYE_COLORS_BY_META = new String[] {
            "dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue",
            "dyeYellow", "dyeLime", "dyePink", "dyeGray",
            "dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue",
            "dyeBrown", "dyeGreen", "dyeRed", "dyeBlack" };

    public static String getDyeOreDict(EnumDyeColor color) {
        return DYE_COLORS_BY_META[color.getMetadata()];
    }

    public static String ITEM_ENDERPEARL = "enderpearl";
    public static String ITEM_DIAMOND = "gemDiamond";
    public static String ITEM_GOLD_INGOT = "ingotGold";
    public static String ITEM_IRON_INGOT = "ingotIron";
    public static String ITEM_GLOWSTONE_DUST = "dustGlowstone";
    public static String ITEM_SUGARCANE = "sugarcane";
    public static String ITEM_CARROT = "cropCarrot";
    public static String ITEM_STICKS = "stickWood";
    public static String ITEM_GOLD_NUGGET = "nuggetGold";
    public static String ITEM_DYE_PURPLE = "dyePurple";
    public static String ITEM_DYE_ALL = "dye";
    public static String ITEM_NETHER_QUARTZ = "gemQuartz";
    public static String ITEM_REDSTONE_DUST = "dustRedstone";
    public static String ITEM_LEATHER = "leather";
    public static String ITEM_FEATHER = "feather";
    public static String ITEM_SUGAR_CANE = "sugarcane";
    public static String ITEM_GUNPOWDER = "gunpowder";
    public static String ITEM_WHEAT = "cropWheat";
    public static String ITEM_BONE = "bone";
    public static String ITEM_EGG = "egg";

    public static String BLOCK_IRON_ORE = "oreIron";

    public static String ITEM_AQUAMARINE = "gemAquamarine";
    public static String ITEM_STARMETAL_INGOT = "ingotAstralStarmetal";
    public static String ITEM_STARMETAL_DUST = "dustAstralStarmetal";

    public static String BLOCK_CRAFTING_TABLE = "workbench";
    public static String BLOCK_MARBLE = "stoneMarble";
    public static String BLOCK_WOOD_PLANKS = "plankWood";
    public static String BLOCK_WOOD_LOGS = "logWood";
    public static String BLOCK_SAPLING = "treeSapling";
    public static String BLOCK_LEAVES = "treeLeaves";
    public static String BLOCK_COBBLESTONE = "cobblestone";
    public static String BLOCK_GLASS_PANE_NOCOLOR = "paneGlassColorless";

}
