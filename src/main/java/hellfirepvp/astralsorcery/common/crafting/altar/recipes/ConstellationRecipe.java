package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
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

    private Map<AltarAdditionalSlot, ItemHandle> matchStacks = new HashMap<>();
    private IConstellation skyConstellationNeeded = null;

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public ConstellationRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public ConstellationRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.CONSTELLATION_CRAFT, recipe);
        setPassiveStarlightRequirement(3700);
    }

    public ConstellationRecipe setCstItem(Item i, AltarAdditionalSlot... slots) {
        return setCstItem(new ItemStack(i), slots);
    }

    public ConstellationRecipe setCstItem(Block b, AltarAdditionalSlot... slots) {
        return setCstItem(new ItemStack(b), slots);
    }

    public ConstellationRecipe setCstItem(ItemStack stack, AltarAdditionalSlot... slots) {
        return setCstItem(new ItemHandle(stack), slots);
    }

    public ConstellationRecipe setCstItem(String oreDict, AltarAdditionalSlot... slots) {
        return setCstItem(new ItemHandle(oreDict), slots);
    }

    public ConstellationRecipe setCstItem(FluidStack fluid, AltarAdditionalSlot... slots) {
        return setCstItem(new ItemHandle(fluid), slots);
    }

    public ConstellationRecipe setCstItem(Fluid fluid, int mbAmount, AltarAdditionalSlot... slots) {
        return setCstItem(new FluidStack(fluid, mbAmount), slots);
    }

    public ConstellationRecipe setAttItem(Fluid fluid, AltarAdditionalSlot... slots) {
        return setCstItem(fluid, 1000, slots);
    }

    public ConstellationRecipe setCstItem(ItemHandle handle, AltarAdditionalSlot... slots) {
        for (AltarAdditionalSlot slot : slots) {
            matchStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getCstItems(AltarAdditionalSlot slot) {
        ItemHandle handle = matchStacks.get(slot);
        if(handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getCstItemHandle(AltarAdditionalSlot slot) {
        return matchStacks.get(slot);
    }

    @Override
    public int craftingTickTime() {
        return 500;
    }

    public void setSkyConstellation(IConstellation constellation) {
        this.skyConstellationNeeded = constellation;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler) {
        if(skyConstellationNeeded != null) {
            DataActiveCelestials cel = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            Collection<IConstellation> activeConstellations = cel.getActiveConstellations(altar.getWorld().provider.getDimension());
            if(activeConstellations == null || !activeConstellations.contains(skyConstellationNeeded)) return false;
        }
        for (AltarAdditionalSlot slot : AltarAdditionalSlot.values()) {
            ItemHandle expected = matchStacks.get(slot);
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
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 altarVec = new Vector3(altar);
        Vector3 thisAltar = altarVec.clone().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 4; i++) {
            Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
            dir.multiply(rand.nextFloat()).add(thisAltar.clone());

            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat())).scale(0.2F + (0.2F * rand.nextFloat())).gravity(0.004);
        }

    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.CONSTELLATION;
    }

    public static enum AltarAdditionalSlot {

        UP_UP_LEFT(13),
        UP_UP_RIGHT(14),
        UP_LEFT_LEFT(15),
        UP_RIGHT_RIGHT(16),

        DOWN_LEFT_LEFT(17),
        DOWN_RIGHT_RIGHT(18),
        DOWN_DOWN_LEFT(19),
        DOWN_DOWN_RIGHT(20);

        private final int slotId;

        AltarAdditionalSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }
    }

}
