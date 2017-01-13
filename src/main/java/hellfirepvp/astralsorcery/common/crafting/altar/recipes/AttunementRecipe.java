/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
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

    private Map<AltarSlot, ItemHandle> additionalSlots = new HashMap<>();

    protected AttunementRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected AttunementRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public AttunementRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public AttunementRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.ATTUNEMENT, recipe);
        setPassiveStarlightRequirement(2000);
    }

    public AttunementRecipe setAttItem(Block b, AltarSlot... slots) {
        return this.setAttItem(new ItemStack(b), slots);
    }

    public AttunementRecipe setAttItem(Item i, AltarSlot... slots) {
        return this.setAttItem(new ItemStack(i), slots);
    }

    public AttunementRecipe setAttItem(ItemStack stack, AltarSlot... slots) {
        return this.setAttItem(new ItemHandle(stack), slots);
    }

    public AttunementRecipe setAttItem(String oreDict, AltarSlot... slots) {
        return this.setAttItem(new ItemHandle(oreDict), slots);
    }

    public AttunementRecipe setAttItem(FluidStack fluid, AltarSlot... slots) {
        return this.setAttItem(new ItemHandle(fluid), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, int mbAmount, AltarSlot... slots) {
        return setAttItem(new FluidStack(fluid, mbAmount), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, AltarSlot... slots) {
        return setAttItem(fluid, 1000, slots);
    }

    public AttunementRecipe setAttItem(ItemHandle handle, AltarSlot... slots) {
        for (AltarSlot slot : slots) {
            additionalSlots.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getAttItems(AltarSlot slot) {
        ItemHandle handle = additionalSlots.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getAttItemHandle(AltarSlot slot) {
        return additionalSlots.get(slot);
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler) {
        for (AltarSlot slot : AltarSlot.values()) {
            ItemHandle expected = additionalSlots.get(slot);
            if(expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.slotId);
                if(!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if(invHandler.getStackInSlot(slot.slotId) != null) return false;
            }
        }

        return super.matches(altar, invHandler);
    }

    @Override
    public int craftingTickTime() {
        return 300;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, long tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 pos = new Vector3(altar).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        particle.motion(
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1  * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.ATTUNEMENT;
    }

    public static enum AltarSlot {

        UPPER_LEFT(9),
        UPPER_RIGHT(10),
        LOWER_LEFT(11),
        LOWER_RIGHT(12);

        public final int slotId;

        private AltarSlot(int slotId) {
            this.slotId = slotId;
        }
    }

}
