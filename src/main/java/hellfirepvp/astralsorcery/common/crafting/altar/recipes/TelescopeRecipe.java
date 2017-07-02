/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TelescopeRecipe
 * Created by HellFirePvP
 * Date: 26.09.2016 / 13:54
 */
public class TelescopeRecipe extends AttunementRecipe {

    public TelescopeRecipe() {
        super(shapedRecipe("telescope", BlockMachine.MachineType.TELESCOPE.asStack())
                .addPart(ItemsAS.handTelescope,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.BLOCK_WOOD_PLANKS,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(OreDictAlias.ITEM_STICKS,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
        .unregisteredAccessibleShapedRecipe());
    }

    @Override
    public void onCraftServerFinish(TileAltar altar, Random rand) {
        EntityPlayer crafter = altar.getActiveCraftingTask().tryGetCraftingPlayerServer();
        if(crafter != null) {
            //FIXME RE-ADD AFTER ADVANCEMENTS
            //crafter.addStat(RegistryAchievements.achvBuildActTelescope);
        }
        super.onCraftServerFinish(altar, rand);
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }

}
