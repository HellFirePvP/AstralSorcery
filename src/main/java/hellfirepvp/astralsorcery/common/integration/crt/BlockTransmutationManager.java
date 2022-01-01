/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.crt;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl_native.blocks.ExpandBlockState;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockMatchInformation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationManager
 * Created by Jaredlll08
 * Date: 03.17.2021 / 15:36
 */
@ZenRegister
@ZenCodeType.Name("mods.astralsorcery.BlockTransmutationManager")
public class BlockTransmutationManager implements IRecipeManager {

    @ZenCodeType.Method
    public void addRecipe(String name, BlockState outState, MCTag<Block> input, double starlight, @ZenCodeType.Optional("null") ResourceLocation constellationKey) {
        addTransmutation(name, outState, starlight, constellationKey, transmutation -> {
            transmutation.addInputOption(new BlockMatchInformation((ITag<Block>) input.getInternal()));
        });
    }
    
    @ZenCodeType.Method
    public void addRecipe(String name, BlockState outState, BlockState input, boolean exact, double starlight, @ZenCodeType.Optional("null") ResourceLocation constellationKey) {
        addTransmutation(name, outState, starlight, constellationKey, transmutation -> {
            transmutation.addInputOption(new BlockMatchInformation(input, exact));
        });
    }

    private void addTransmutation(String name, BlockState outState, double starlight,
                                  @ZenCodeType.Optional("null") ResourceLocation constellationKey, Consumer<BlockTransmutation> addInputRequirements) {
        name = fixRecipeName(name);
        IWeakConstellation weakConstellation = null;
        if(constellationKey != null) {
            if(!RegistriesAS.REGISTRY_CONSTELLATIONS.containsKey(constellationKey)) {
                throw new IllegalArgumentException("Invalid constellation key: \"" + constellationKey + "\"");
            }
            IConstellation constellation = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(constellationKey);
            if(constellation instanceof IWeakConstellation) {
                weakConstellation = (IWeakConstellation) constellation;
            } else {
                throw new IllegalArgumentException("Constellation: \"" + constellationKey + "\" is not a weak constellation!");
            }
        }
        BlockTransmutation transmutation = new BlockTransmutation(new ResourceLocation(name), outState, starlight, weakConstellation);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, transmutation));
    }
    
    // There is currently an issue with optional booleans, so this is the work around
    @ZenCodeType.Method
    public void removeRecipe(BlockState outputState) {
        this.removeRecipe(outputState, false);
    }
    
    @ZenCodeType.Method
    public void removeRecipe(BlockState outputState, boolean exact) {
        BlockMatchInformation matcher = new BlockMatchInformation(outputState, exact);
        CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
            if(iRecipe instanceof BlockTransmutation) {
                BlockTransmutation recipe = (BlockTransmutation) iRecipe;
                return matcher.test(recipe.getOutput());
            }
            return false;
        }, action -> "Removing Block Transmutation recipes that output " + ExpandBlockState.getCommandString(outputState)));
    }
    
    @Override
    public void removeRecipe(IItemStack output) {
        throw new UnsupportedOperationException("Cannot remove Astral Sorcery Block Transmutation recipes by IItemStacks, use the BlockState method instead!");
    }
    
    @Override
    public IRecipeType<BlockTransmutation> getRecipeType() {
        return RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getType();
    }
}
