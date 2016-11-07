package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockRitualPedestal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RecipeRitualPedestal
 * Created by HellFirePvP
 * Date: 01.11.2016 / 14:48
 */
public class RecipeRitualPedestal extends AttenuationRecipe {

    public RecipeRitualPedestal(boolean celestial) {
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
                .addPart((celestial ? ItemsAS.celestialCrystal : ItemsAS.rockCrystal),
                        ShapedRecipeSlot.CENTER));
        setAttItem(BlockMarble.MarbleBlockType.ENGRAVED.asStack(),
                AttenuationRecipe.AltarSlot.UPPER_LEFT,
                AttenuationRecipe.AltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.PILLAR.asStack(),
                AttenuationRecipe.AltarSlot.LOWER_LEFT,
                AttenuationRecipe.AltarSlot.LOWER_RIGHT);
        setPassiveStarlightRequirement(3000);
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack out = super.getOutput(centralGridMap, altar);
        ItemBlockRitualPedestal.setBeaconType(out, false);
        return out;
    }
}
