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
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
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
 * Class: AttenuationRecipe
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:20
 */
public class AttunementRecipe extends DiscoveryRecipe {

    private Map<AttunementAltarSlot, ItemHandle> additionalSlots = new HashMap<>();

    protected AttunementRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public AttunementRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.ATTUNEMENT, recipe);
        setPassiveStarlightRequirement(1400);
    }

    public AttunementRecipe setAttItem(Block b, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(b), slots);
    }

    public AttunementRecipe setAttItem(Item i, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(i), slots);
    }

    public AttunementRecipe setAttItem(ItemStack stack, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(stack), slots);
    }

    public AttunementRecipe setAttItem(String oreDict, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(oreDict), slots);
    }

    public AttunementRecipe setAttItem(FluidStack fluid, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(fluid), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, int mbAmount, AttunementAltarSlot... slots) {
        return setAttItem(new FluidStack(fluid, mbAmount), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, AttunementAltarSlot... slots) {
        return setAttItem(fluid, 1000, slots);
    }

    public AttunementRecipe setAttItem(ItemHandle handle, AttunementAltarSlot... slots) {
        for (AttunementAltarSlot slot : slots) {
            additionalSlots.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getAttItems(AttunementAltarSlot slot) {
        ItemHandle handle = additionalSlots.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getAttItemHandle(AttunementAltarSlot slot) {
        return additionalSlots.get(slot);
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler, boolean ignoreStarlightRequirement) {
        for (AttunementAltarSlot slot : AttunementAltarSlot.values()) {
            ItemHandle expected = additionalSlots.get(slot);
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
    public int craftingTickTime() {
        return 300;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 pos = new Vector3(altar).add(0.5, 0.5, 0.5);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            particle.motion(
                    rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.1  * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
            particle.scale(0.2F);
        }
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.ATTUNEMENT;
    }

    public static enum AttunementAltarSlot {

        UPPER_LEFT(9),
        UPPER_RIGHT(10),
        LOWER_LEFT(11),
        LOWER_RIGHT(12);

        private final int slotId;

        private AttunementAltarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
