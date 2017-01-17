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
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeRitualPedestal
 * Created by HellFirePvP
 * Date: 01.11.2016 / 14:48
 */
public class RecipeRitualPedestal extends AttunementRecipe {

    public RecipeRitualPedestal() {
        super(new ShapedRecipe(BlocksAS.ritualPedestal)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(BlockMarble.MarbleBlockType.PILLAR.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.CHISELED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER));
        setAttItem(BlockMarble.MarbleBlockType.ENGRAVED.asStack(),
                AttunementRecipe.AltarSlot.UPPER_LEFT,
                AttunementRecipe.AltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.PILLAR.asStack(),
                AttunementRecipe.AltarSlot.LOWER_LEFT,
                AttunementRecipe.AltarSlot.LOWER_RIGHT);
        setPassiveStarlightRequirement(3000);
    }
}
