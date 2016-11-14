package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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

    protected AttenuationRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected AttenuationRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public AttenuationRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public AttenuationRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.ATTENUATION, recipe);
        setPassiveStarlightRequirement(2000);
    }

    public AttenuationRecipe setAttItem(Block b, AltarSlot... slots) {
        return this.setAttItem(new ItemStack(b), slots);
    }

    public AttenuationRecipe setAttItem(Item i, AltarSlot... slots) {
        return this.setAttItem(new ItemStack(i), slots);
    }

    public AttenuationRecipe setAttItem(ItemStack stack, AltarSlot... slots) {
        for (AltarSlot slot : slots) {
            additionalSlots.put(slot, stack.copy());
        }
        return this;
    }

    @Nullable
    public ItemStack getAttItem(AltarSlot slot) {
        return additionalSlots.get(slot);
    }

    @Override
    public boolean matches(TileAltar altar) {
        for (AltarSlot slot : AltarSlot.values()) {
            ItemStack expected = additionalSlots.get(slot);
            if(expected != null) {
                ItemStack altarItem = altar.getStackInSlot(slot.slotId);
                if(!ItemUtils.stackEqualsNonNBT(altarItem, expected)) {
                    return false;
                }
            } else {
                if(altar.getStackInSlot(slot.slotId) != null) return false;
            }
        }

        return super.matches(altar);
    }

    @Override
    public int craftingTickTime() {
        return 500;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
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
