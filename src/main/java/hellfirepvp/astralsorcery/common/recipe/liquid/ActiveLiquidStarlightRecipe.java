/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid;

import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveLiquidStarlightRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveLiquidStarlightRecipe {

    private static final int WORLD_TIME_TOLERANCE = 4;

    //This is primarily just a helper class to do the on tick stuff for liquid starlight recipes
    //Cause we kinda don't have a state on the entity really, only a tick timer saved on the trigger entity
    private ActiveLiquidStarlightRecipe() {}

    public static void tryProgressCraft(ItemEntity itemEntity) {
        if (!itemEntity.isAlive()) return;
        BlockPos pos = itemEntity.blockPosition();
        Level level = itemEntity.level();

        RecipeFinder.of(level).findLiquidStarlightRecipe(itemEntity).ifPresent(recipeHolder -> {
            LiquidStarlightRecipe recipe = recipeHolder.value();
            LiquidStarlightRecipeInput input = LiquidStarlightRecipeInput.of(itemEntity);
            if (!recipe.matches(input, level)) {
                return;
            }

            RandomSource rand = RandomSource.create(pos.asLong() + itemEntity.getId() + itemEntity.getId() << 16);
            int craftTick = getAndIncrementCraftingTick(itemEntity);
            if (!level.isClientSide()) {
                doServerCraftTick(itemEntity, level, rand, recipe, craftTick);
            } else {
                doClientCraftTick(itemEntity, level, rand, recipe, craftTick);
            }
        });
    }

    private static void doServerCraftTick(ItemEntity triggerEntity, Level level, RandomSource rand, LiquidStarlightRecipe recipe, int craftTick) {
        int requiredTicks = recipe.getDuration() + rand.nextInt(Math.max(recipe.getRandomAdditionalDuration() + 1, 1));
        if (craftTick >= requiredTicks) {
            LiquidStarlightRecipeInput input = LiquidStarlightRecipeInput.of(triggerEntity);
            if (recipe.matches(input, level) && recipe.consumeInputs(input, level.registryAccess())) {
                recipe.createOutput(input, level.registryAccess());
            }
        }
    }

    private static void doClientCraftTick(ItemEntity itemEntity, Level level, RandomSource rand, LiquidStarlightRecipe recipe, int craftTick) {
        RandomSource effectRand = RandomSource.create(rand.nextLong() ^ (long) craftTick << 32 ^ craftTick);
        LiquidStarlightRecipeInput input = LiquidStarlightRecipeInput.of(itemEntity);
        if (recipe.matches(input, level)) {
            recipe.getOutputModifiers().forEach(modifier -> {
                modifier.playCraftingEffects(recipe, input, effectRand, craftTick);
            });
        }
    }

    private static int getAndIncrementCraftingTick(Entity e) {
        int tick = getCraftingTick(e);
        setCraftingTick(e, tick + 1);
        return tick;
    }

    private static void setCraftingTick(Entity e, int tick) {
        long wTick = e.getCommandSenderWorld().getGameTime();

        CompoundTag tag = e.getPersistentData();
        tag.putInt("liquidStarlight_craftTick", tick);
        tag.putLong("liquidStarlight_wCraftTick", wTick);
    }

    private static int getCraftingTick(Entity e) {
        long wTick = e.getCommandSenderWorld().getGameTime();

        CompoundTag tag = e.getPersistentData();
        if (!tag.contains("liquidStarlight_wCraftTick", Tag.TAG_LONG)) {
            return 0;
        }

        long savedWTick = tag.getLong("liquidStarlight_wCraftTick");
        if (Math.abs(wTick - savedWTick) > WORLD_TIME_TOLERANCE) {
            return 0;
        } else {
            return tag.getInt("liquidStarlight_craftTick");
        }
    }
}
