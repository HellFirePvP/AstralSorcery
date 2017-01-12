/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LensRecipe
 * Created by HellFirePvP
 * Date: 02.11.2016 / 20:27
 */
public class LensRecipe extends ConstellationRecipe {

    public LensRecipe() {
        super(new ShapedRecipe(new ItemStack(BlocksAS.lens))
                .addPart(OreDictAlias.BLOCK_GLASS_PANE_NOCOLOR,
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER));

        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AltarSlot.LOWER_LEFT,
                AltarSlot.LOWER_RIGHT);
        setCstItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AltarAdditionalSlot.DOWN_DOWN_LEFT,
                AltarAdditionalSlot.DOWN_DOWN_RIGHT);
        setCstItem(OreDictAlias.BLOCK_WOOD_LOGS,
                AltarAdditionalSlot.DOWN_LEFT_LEFT,
                AltarAdditionalSlot.DOWN_RIGHT_RIGHT);
        setCstItem(OreDictAlias.ITEM_GOLD_INGOT,
                AltarAdditionalSlot.UP_LEFT_LEFT,
                AltarAdditionalSlot.UP_RIGHT_RIGHT);
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack lens = super.getOutput(centralGridMap, altar);
        CrystalProperties crystalProp = CrystalProperties.getCrystalProperties(centralGridMap.get(ShapedRecipeSlot.CENTER).getApplicableItems().get(0));
        CrystalProperties.applyCrystalProperties(lens, crystalProp);
        return lens;
    }

}
