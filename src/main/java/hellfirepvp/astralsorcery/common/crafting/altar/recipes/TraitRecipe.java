/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ICraftingProgress;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
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
public class TraitRecipe extends ConstellationRecipe implements ICraftingProgress {

    public static final BlockPos[] offsetRelays = new BlockPos[] {
            new BlockPos(0, 0, 3),
            new BlockPos(2, 0, 2),
            new BlockPos(3, 0, 0),
            new BlockPos(2, 0, -2),
            new BlockPos(0, 0, -3),
            new BlockPos(-2, 0, -2),
            new BlockPos(-3, 0, 0),
            new BlockPos(-2, 0, 2)
    };

    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
    private IConstellation requiredConstellation = null;

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

    public TraitRecipe addTraitItem(Item i) {
        return addTraitItem(new ItemStack(i));
    }

    public TraitRecipe addTraitItem(Block b) {
        return addTraitItem(new ItemStack(b));
    }

    public TraitRecipe addTraitItem(ItemStack stack) {
        return addTraitItem(new ItemHandle(stack));
    }

    public TraitRecipe addTraitItem(String oreDict) {
        return addTraitItem(new ItemHandle(oreDict));
    }

    public TraitRecipe addTraitItem(FluidStack fluid) {
        return addTraitItem(new ItemHandle(fluid));
    }

    public TraitRecipe addTraitItem(Fluid fluid, int mbAmount) {
        return addTraitItem(new FluidStack(fluid, mbAmount));
    }

    public TraitRecipe addTraitItem(Fluid fluid) {
        return addTraitItem(fluid, 1000);
    }

    public TraitRecipe addTraitItem(ItemHandle handle) {
        additionallyRequiredStacks.add(handle);
        return this;
    }

    @Nonnull
    public List<NonNullList<ItemStack>> getTraitItems() {
        List<NonNullList<ItemStack>> out = Lists.newArrayList();
        for (ItemHandle handle : additionallyRequiredStacks) {
            out.add(handle.getApplicableItems());
        }
        return out;
    }

    @Nonnull
    public List<ItemHandle> getTraitItemHandles() {
        return Lists.newArrayList(additionallyRequiredStacks);
    }

    public void setRequiredConstellation(IConstellation requiredConstellation) {
        this.requiredConstellation = requiredConstellation;
    }

    @Nullable
    public IConstellation getRequiredConstellation() {
        return requiredConstellation;
    }

    @Override
    public int craftingTickTime() {
        return 1000;
    }

    @Override
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData, int activeCraftingTick) {
        return true;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        IConstellation req = getRequiredConstellation();
        if(req != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if(focus != null) {
                if(!req.equals(focus)) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.RADIANCE;
    }

    //public static enum TraitAltarSlot {

    //    UPPER_CENTER(21),
    //    LEFT_CENTER(22),
    //    RIGHT_CENTER(23),
    //    LOWER_CENTER(24);

    //    private final int slotId;

    //    private TraitAltarSlot(int slotId) {
    //        this.slotId = slotId;
    //    }

    //    public int getSlotId() {
    //        return slotId;
    //    }

    //}

}
