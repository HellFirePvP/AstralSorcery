/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitRecipe
 * Created by HellFirePvP
 * Date: 06.03.2017 / 15:57
 */
public class TraitRecipe extends ConstellationRecipe {

    private Map<TraitAltarSlot, ItemHandle> matchTraitStacks = new HashMap<>();

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public TraitRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public TraitRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.TRAIT_CRAFT, recipe);
        setPassiveStarlightRequirement(8500);
    }

    public TraitRecipe setTraitItem(Item i, TraitAltarSlot... slots) {
        return setTraitItem(new ItemStack(i), slots);
    }

    public TraitRecipe setTraitItem(Block b, TraitAltarSlot... slots) {
        return setTraitItem(new ItemStack(b), slots);
    }

    public TraitRecipe setTraitItem(ItemStack stack, TraitAltarSlot... slots) {
        return setTraitItem(new ItemHandle(stack), slots);
    }

    public TraitRecipe setTraitItem(String oreDict, TraitAltarSlot... slots) {
        return setTraitItem(new ItemHandle(oreDict), slots);
    }

    public TraitRecipe setTraitItem(FluidStack fluid, TraitAltarSlot... slots) {
        return setTraitItem(new ItemHandle(fluid), slots);
    }

    public TraitRecipe setTraitItem(Fluid fluid, int mbAmount, TraitAltarSlot... slots) {
        return setTraitItem(new FluidStack(fluid, mbAmount), slots);
    }

    public TraitRecipe setTraitItem(Fluid fluid, TraitAltarSlot... slots) {
        return setTraitItem(fluid, 1000, slots);
    }

    public TraitRecipe setTraitItem(ItemHandle handle, TraitAltarSlot... slots) {
        for (TraitAltarSlot slot : slots) {
            matchTraitStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getTraitItem(TraitAltarSlot slot) {
        ItemHandle handle = matchTraitStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getTraitItemHandle(TraitAltarSlot slot) {
        return matchTraitStacks.get(slot);
    }

    @Override
    public int craftingTickTime() {
        return 600;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        for (TraitAltarSlot slot : TraitAltarSlot.values()) {
            ItemHandle expected = matchTraitStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.getSlotId()).isEmpty()) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.RADIANCE;
    }

    public static enum TraitAltarSlot {

        UPPER_CENTER(21),
        LEFT_CENTER(22),
        RIGHT_CENTER(23),
        LOWER_CENTER(24);

        private final int slotId;

        private TraitAltarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
