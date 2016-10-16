package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttenuationRecipe
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:20
 */
public class AttenuationRecipe extends DiscoveryRecipe {

    private Map<AltarSlot, ItemStack> additionalSlots = new HashMap<>();
    private Constellation skyConstellationNeeded = null;

    public AttenuationRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public AttenuationRecipe(IAccessibleRecipe recipe) {
        super(recipe);
        setPassiveStarlightRequirement(2000);
    }

    public void setSkyConstellation(Constellation constellation) {
        this.skyConstellationNeeded = constellation;
    }

    public AttenuationRecipe setItem(Block b, AltarSlot... slots) {
        return this.setItem(new ItemStack(b), slots);
    }

    public AttenuationRecipe setItem(Item i, AltarSlot... slots) {
        return this.setItem(new ItemStack(i), slots);
    }

    public AttenuationRecipe setItem(ItemStack stack, AltarSlot... slots) {
        for (AltarSlot slot : slots) {
            additionalSlots.put(slot, stack.copy());
        }
        return this;
    }

    @Override
    public boolean matches(TileAltar altar) {
        if(skyConstellationNeeded == null) return super.matches(altar);

        for (AltarSlot slot : additionalSlots.keySet()) {
            ItemStack altarItem = altar.getStackInSlot(slot.slotId);
            if(!ItemUtils.stackEqualsNonNBT(altarItem, additionalSlots.get(slot))) return false;
        }

        DataActiveCelestials cel = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
        return cel.getActiveConstellations().contains(skyConstellationNeeded) && super.matches(altar);
    }

    @Override
    public int craftingTickTime() {
        return 300;
    }

    @Override
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);
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
