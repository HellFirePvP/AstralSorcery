package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
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
        super(new ShapedRecipe(new ItemStack(ItemsAS.entityPlacer, 1, ItemEntityPlacer.PlacerType.GRINDSTONE.getMeta()))
                .addPart(Blocks.STONE,
                        ShapedRecipeSlot.CENTER)
                .addPart(Blocks.LOG,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(Items.STICK,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT));
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }
}
