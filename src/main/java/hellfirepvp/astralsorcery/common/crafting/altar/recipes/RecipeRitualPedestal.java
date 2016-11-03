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
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RUNED.ordinal()),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_CENTER,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart((celestial ? ItemsAS.celestialCrystal : ItemsAS.rockCrystal),
                        ShapedRecipeSlot.CENTER));
        setAttItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.ENGRAVED.ordinal()),
                AttenuationRecipe.AltarSlot.UPPER_LEFT,
                AttenuationRecipe.AltarSlot.UPPER_RIGHT);
        setAttItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
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
