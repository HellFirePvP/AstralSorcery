/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 22:22
 */
public class ConstellationRecipe extends AttunementRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 4, 3,  4),
            new Vector3(-4, 3,  4),
            new Vector3( 4, 3, -4),
            new Vector3(-4, 3, -4)
    };

    private Map<ConstellationAtlarSlot, ItemHandle> matchStacks = new HashMap<>();

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public ConstellationRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.CONSTELLATION_CRAFT, recipe);
        setPassiveStarlightRequirement(3200);
    }

    public ConstellationRecipe setCstItem(Item i, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(i), slots);
    }

    public ConstellationRecipe setCstItem(Block b, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(b), slots);
    }

    public ConstellationRecipe setCstItem(ItemStack stack, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(stack), slots);
    }

    public ConstellationRecipe setCstItem(String oreDict, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(oreDict), slots);
    }

    public ConstellationRecipe setCstItem(FluidStack fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(fluid), slots);
    }

    public ConstellationRecipe setCstItem(Fluid fluid, int mbAmount, ConstellationAtlarSlot... slots) {
        return setCstItem(new FluidStack(fluid, mbAmount), slots);
    }

    public ConstellationRecipe setCstItem(Fluid fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(fluid, 1000, slots);
    }

    public ConstellationRecipe setCstItem(ItemHandle handle, ConstellationAtlarSlot... slots) {
        for (ConstellationAtlarSlot slot : slots) {
            matchStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getCstItems(ConstellationAtlarSlot slot) {
        ItemHandle handle = matchStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getCstItemHandle(ConstellationAtlarSlot slot) {
        return matchStacks.get(slot);
    }

    @Override
    public int craftingTickTime() {
        return 500;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        for (ConstellationAtlarSlot slot : ConstellationAtlarSlot.values()) {
            ItemHandle expected = matchStacks.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.slotId);
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(!invHandler.getStackInSlot(slot.slotId).isEmpty()) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarVec = new Vector3(altar);
            Vector3 thisAltar = altarVec.clone().add(0.5, 0.5, 0.5);
            for (int i = 0; i < 4; i++) {
                Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
                dir.multiply(rand.nextFloat()).add(thisAltar.clone());

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
                particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat())).scale(0.2F + (0.2F * rand.nextFloat())).gravity(0.004);
            }
        }

    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.CONSTELLATION;
    }

    public static enum ConstellationAtlarSlot {

        UP_UP_LEFT(13),
        UP_UP_RIGHT(14),
        UP_LEFT_LEFT(15),
        UP_RIGHT_RIGHT(16),

        DOWN_LEFT_LEFT(17),
        DOWN_RIGHT_RIGHT(18),
        DOWN_DOWN_LEFT(19),
        DOWN_DOWN_RIGHT(20);

        private final int slotId;

        ConstellationAtlarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }
    }

}
