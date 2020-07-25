/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import hellfirepvp.astralsorcery.common.crafting.nojson.CustomRecipe;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidStarlightRecipe
 * Created by HellFirePvP
 * Date: 30.09.2019 / 20:27
 */
public abstract class LiquidStarlightRecipe extends CustomRecipe {

    protected static final Random rand = new Random();
    private static final int WORLD_TIME_TOLERANCE = 10;

    public LiquidStarlightRecipe(ResourceLocation key) {
        super(key);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract List<Ingredient> getInputForRender();

    @OnlyIn(Dist.CLIENT)
    public abstract List<Ingredient> getOutputForRender();

    public abstract boolean doesStartRecipe(ItemStack item);

    public abstract boolean matches(ItemEntity trigger, World world, BlockPos at);

    public abstract void doServerCraftTick(ItemEntity trigger, World world, BlockPos at);

    @OnlyIn(Dist.CLIENT)
    public abstract void doClientEffectTick(ItemEntity trigger, World world, BlockPos at);

    protected final List<Entity> getEntitiesInBlock(IWorld world, BlockPos pos) {
        return world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
    }

    @Nullable
    protected final ItemStack consumeItemEntityInBlock(IWorld world, BlockPos pos, Item itemClass) {
        return consumeItemEntityInBlock(world, pos, 1, stack ->
                itemClass.getClass().isAssignableFrom(stack.getItem().getClass()));
    }

    @Nullable
    protected final ItemStack consumeItemEntityInBlock(IWorld world, BlockPos pos, int count, Predicate<ItemStack> match) {
        List<Entity> entities = getEntitiesInBlock(world, pos).stream()
                .filter(e -> e instanceof ItemEntity)
                .collect(Collectors.toList());
        for (Entity e : entities) {
            ItemEntity ie = (ItemEntity) e;
            if (ie.isAlive() &&
                    !ie.getItem().isEmpty() &&
                    ie.getItem().getCount() >= count &&
                    match.test(ie.getItem())) {

                ItemStack stored = ie.getItem();
                ItemStack found = ItemUtils.copyStackWithSize(stored, count);

                stored.shrink(count);
                ie.setItem(stored);
                return found;
            }
        }
        return null;
    }

    protected final int getAndIncrementCraftingTick(Entity e) {
        int tick = getCraftingTick(e);
        setCraftingTick(e, tick + 1);
        return tick;
    }

    protected final void setCraftingTick(Entity e, int tick) {
        long wTick = e.getEntityWorld().getGameTime();

        CompoundNBT nbt = NBTHelper.getPersistentData(e);
        nbt.putInt("craftTick", tick);
        nbt.putLong("wCraftTick", wTick);
    }

    protected final int getCraftingTick(Entity e) {
        long wTick = e.getEntityWorld().getGameTime();

        CompoundNBT nbt = NBTHelper.getPersistentData(e);
        if (!nbt.contains("wCraftTick", Constants.NBT.TAG_LONG)) {
            return 0;
        }

        long savedWTick = nbt.getLong("wCraftTick");
        if (Math.abs(wTick - savedWTick) > WORLD_TIME_TOLERANCE) {
            return 0;
        } else {
            return nbt.getInt("craftTick");
        }
    }

}
