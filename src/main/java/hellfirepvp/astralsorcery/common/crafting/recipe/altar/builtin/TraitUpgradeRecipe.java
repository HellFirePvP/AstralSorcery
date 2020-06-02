/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.AltarRecipeEffectsAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitUpgradeRecipe
 * Created by HellFirePvP
 * Date: 27.09.2019 / 22:53
 */
public class TraitUpgradeRecipe extends SimpleAltarRecipe {

    public TraitUpgradeRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId, altarType, duration, starlightRequirement, recipeGrid);

        this.addAltarEffect(AltarRecipeEffectsAS.UPGRADE_ALTAR);
    }

    public static TraitUpgradeRecipe convertToThis(SimpleAltarRecipe other) {
        return new TraitUpgradeRecipe(other.getId(), other.getAltarType(), other.getDuration(), other.getStarlightRequirement(), other.getInputs());
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs(TileAltar altar) {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(LogicalSide side, PlayerEntity crafter, TileAltar altar, boolean ignoreStarlightRequirement) {
        return altar.getAltarType() == AltarType.CONSTELLATION && super.matches(side, crafter, altar, ignoreStarlightRequirement);
    }

    @Override
    public void onRecipeCompletion(TileAltar altar, ActiveSimpleAltarRecipe activeRecipe) {
        super.onRecipeCompletion(altar, activeRecipe);

        ResearchManager.informCraftedAltar(altar, activeRecipe, new ItemStack(BlocksAS.ALTAR_RADIANCE));
        altar.getWorld().setBlockState(altar.getPos(), BlocksAS.ALTAR_RADIANCE.getDefaultState(), Constants.BlockFlags.DEFAULT);
    }
}
