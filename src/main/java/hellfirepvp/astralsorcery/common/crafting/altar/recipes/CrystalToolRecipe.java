/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalToolBase;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolRecipe
 * Created by HellFirePvP
 * Date: 26.09.2016 / 01:44
 */
public class CrystalToolRecipe extends DiscoveryRecipe {

    private final ShapedRecipeSlot[] positions;

    public CrystalToolRecipe(AccessibleRecipeAdapater recipe, ShapedRecipeSlot... crystalPositions) {
        super(recipe);
        this.positions = crystalPositions;
    }

    @Override
    public int craftingTickTime() {
        return (int) (super.craftingTickTime() * 1.5);
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack toolOut = super.getOutput(centralGridMap, altar);
        List<CrystalProperties> prop = new LinkedList<>();
        for (ShapedRecipeSlot slot : ShapedRecipeSlot.values()) {
            ItemHandle handle = centralGridMap.get(slot);
            if(handle == null) continue;
            if(handle.getApplicableItems().size() != 1) continue; //Force it to be the crystal. and only the crystal.
            ItemStack stack = handle.getApplicableItems().get(0);
            CrystalProperties c = CrystalProperties.getCrystalProperties(stack);
            if(c == null) continue;
            prop.add(c);
        }
        ItemCrystalToolBase.setToolProperties(toolOut, ToolCrystalProperties.merge(prop));
        return toolOut;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        ItemStack stack = super.getOutputForRender();
        List<CrystalProperties> props = new LinkedList<>();
        for (ShapedRecipeSlot position : positions) {
            props.add(CrystalProperties.getMaxRockProperties());
        }
        ItemCrystalToolBase.setToolProperties(stack, ToolCrystalProperties.merge(props));
        return stack;
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }

}
