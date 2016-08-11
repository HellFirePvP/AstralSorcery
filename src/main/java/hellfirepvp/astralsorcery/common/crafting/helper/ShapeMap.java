package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShapeMap
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:20
 */
public class ShapeMap extends HashMap<ShapedRecipeSlot, ItemStack> {

    public ShapeMap() {
        super();
        put(ShapedRecipeSlot.UPPER_LEFT, null);
        put(ShapedRecipeSlot.UPPER_CENTER, null);
        put(ShapedRecipeSlot.UPPER_RIGHT, null);
        put(ShapedRecipeSlot.LEFT, null);
        put(ShapedRecipeSlot.CENTER, null);
        put(ShapedRecipeSlot.RIGHT, null);
        put(ShapedRecipeSlot.LOWER_LEFT, null);
        put(ShapedRecipeSlot.LOWER_CENTER, null);
        put(ShapedRecipeSlot.LOWER_RIGHT, null);
    }

}
