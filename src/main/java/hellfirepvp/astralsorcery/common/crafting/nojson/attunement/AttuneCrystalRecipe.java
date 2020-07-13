package hellfirepvp.astralsorcery.common.crafting.nojson.attunement;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active.ActiveCrystalAttunementRecipe;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCrystalBase;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttuneCrystalRecipe
 * Created by HellFirePvP
 * Date: 05.12.2019 / 06:33
 */
public class AttuneCrystalRecipe extends AttunementRecipe<ActiveCrystalAttunementRecipe> {

    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public AttuneCrystalRecipe() {
        super(AstralSorcery.key("attune_crystal"));
    }

    @Override
    public boolean canStartCrafting(TileAttunementAltar altar) {
        World world = altar.getWorld();
        if (DayTimeHelper.isNight(world)) {
            return findApplicableCrystal(altar) != null;
        }
        return false;
    }

    @Nonnull
    @Override
    public ActiveCrystalAttunementRecipe createRecipe(TileAttunementAltar altar) {
        ItemEntity crystal = findApplicableCrystal(altar);
        return new ActiveCrystalAttunementRecipe(this, altar.getActiveConstellation(), crystal.getEntityId());
    }

    @Nonnull
    @Override
    public ActiveCrystalAttunementRecipe deserialize(TileAttunementAltar altar, CompoundNBT nbt, @Nullable ActiveCrystalAttunementRecipe previousInstance) {
        return new ActiveCrystalAttunementRecipe(this, nbt);
    }

    @Nullable
    private static ItemEntity findApplicableCrystal(TileAttunementAltar altar) {
        IConstellation cst = altar.getActiveConstellation();
        if (cst == null) {
            return null;
        }

        AxisAlignedBB boxAt = BOX.offset(altar.getPos().up()).grow(1);

        Vector3 thisVec = new Vector3(altar).add(0.5, 1.5, 0.5);
        List<ItemEntity> items = altar.getWorld().getEntitiesWithinAABB(ItemEntity.class, boxAt);
        if (!items.isEmpty()) {
            ItemEntity item = EntityUtils.selectClosest(items, (iEntity) -> thisVec.distanceSquared(iEntity.getPositionVector()));
            if (isApplicableCrystal(item, cst)) {
                return item;
            }
        }
        return null;
    }

    public static boolean isApplicableCrystal(ItemEntity entity, IConstellation cst) {
        ItemStack stack;
        if (entity.isAlive() &&
                !(stack = entity.getItem()).isEmpty() &&
                stack.getItem() instanceof ItemCrystalBase) {

            if (!(stack.getItem() instanceof ConstellationItem)) {
                return cst instanceof IWeakConstellation;
            } else {
                IWeakConstellation attuned = ((ConstellationItem) stack.getItem()).getAttunedConstellation(stack);
                IMinorConstellation trait = ((ConstellationItem) stack.getItem()).getTraitConstellation(stack);

                if (attuned == null && cst instanceof IWeakConstellation) {
                    return true;
                } else if (trait == null && cst instanceof IMinorConstellation) {
                    return true;
                }
            }
        }
        return false;
    }
}
