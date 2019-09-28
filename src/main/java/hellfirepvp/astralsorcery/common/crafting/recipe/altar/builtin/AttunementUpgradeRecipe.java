/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.builtin;

import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.AltarRecipeEffectsAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementUpgradeRecipe
 * Created by HellFirePvP
 * Date: 27.09.2019 / 22:23
 */
public class AttunementUpgradeRecipe extends SimpleAltarRecipe {

    public AttunementUpgradeRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId, altarType, duration, starlightRequirement, recipeGrid);

        this.addAltarEffect(AltarRecipeEffectsAS.UPGRADE_ALTAR);
    }

    public static AttunementUpgradeRecipe convertToThis(SimpleAltarRecipe other) {
        return new AttunementUpgradeRecipe(other.getId(), other.getAltarType(), other.getDuration(), other.getStarlightRequirement(), other.getInputs());
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs(TileAltar altar) {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(TileAltar altar, boolean ignoreStarlightRequirement) {
        return altar.getAltarType() == AltarType.DISCOVERY && super.matches(altar, ignoreStarlightRequirement);
    }

    @Override
    public void onRecipeCompletion(TileAltar altar) {
        super.onRecipeCompletion(altar);

        ResearchManager.informCraftedAltar(altar, altar.getActiveRecipe(), new ItemStack(BlocksAS.ALTAR_ATTUNEMENT));
        altar.getWorld().setBlockState(altar.getPos(), BlocksAS.ALTAR_ATTUNEMENT.getDefaultState(), Constants.BlockFlags.DEFAULT);
    }
}
