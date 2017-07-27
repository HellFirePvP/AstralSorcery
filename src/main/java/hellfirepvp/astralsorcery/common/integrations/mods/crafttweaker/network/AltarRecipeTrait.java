/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeTrait
 * Created by HellFirePvP
 * Date: 24.07.2017 / 19:46
 */
public class AltarRecipeTrait extends BaseAltarRecipe {

    @Nullable
    private final IConstellation focusRequiredConstellation;

    AltarRecipeTrait() {
        super(null, null, 0, 0);
        this.focusRequiredConstellation = null;
    }

    public AltarRecipeTrait(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime, @Nullable IConstellation focus) {
        super(inputs, output, starlightRequired, craftingTickTime);
        this.focusRequiredConstellation = focus;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T4_ADD;
    }

    @Override
    public void applyRecipe() {
        AbstractAltarRecipe aar = buildRecipeUnsafe(
                TileAltar.AltarLevel.TRAIT_CRAFT,
                this.starlightRequired,
                this.craftingTickTime,
                this.output,
                this.inputs);
        if(aar instanceof TraitRecipe && focusRequiredConstellation != null) {
            ((TraitRecipe) aar).setRequiredConstellation(focusRequiredConstellation);
        }
        CraftingAccessManager.registerMTAltarRecipe(aar);
    }

}
