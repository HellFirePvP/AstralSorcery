package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 23.10.2016 / 17:00
 */
public class GrindstoneRecipe extends DiscoveryRecipe {

    public GrindstoneRecipe() {
        super(new ShapedRecipe(ItemEntityPlacer.PlacerType.GRINDSTONE.asStack())
                .addPart(BlockMarble.MarbleBlockType.RAW.asStack(),
                        ShapedRecipeSlot.CENTER)
                .addPart(Blocks.PLANKS,
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_LEFT));
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }
}
