package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 22:22
 */
public class ConstellationRecipe extends AttenuationRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 4, 3,  4),
            new Vector3(-4, 3,  4),
            new Vector3( 4, 3, -4),
            new Vector3(-4, 3, -4)
    };

    private Map<AltarAdditionalSlot, ItemStack> matchStacks = new HashMap<>();
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
        for (AltarAdditionalSlot slot : slots) {
            matchStacks.put(slot, stack.copy());
        }
        return this;
    }

    @Nullable
    public ItemStack getCstItem(AltarAdditionalSlot slot) {
        return matchStacks.get(slot);
    }

    @Override
    public int craftingTickTime() {
        return 600;
    }

    public void setSkyConstellation(IConstellation constellation) {
        this.skyConstellationNeeded = constellation;
    }

    @Override
    public boolean matches(TileAltar altar) {
        if(skyConstellationNeeded != null) {
            DataActiveCelestials cel = SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS);
            Collection<IConstellation> activeConstellations = cel.getActiveConstellations(altar.getWorld().provider.getDimension());
            if(activeConstellations == null || !activeConstellations.contains(skyConstellationNeeded)) return false;
        }
        for (AltarAdditionalSlot slot : AltarAdditionalSlot.values()) {
            ItemStack expected = matchStacks.get(slot);
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
