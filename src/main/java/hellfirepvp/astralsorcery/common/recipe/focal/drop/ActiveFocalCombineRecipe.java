/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.drop;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveFocalCombineRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveFocalCombineRecipe {

    private final FocalCombineRecipe recipe;
    private final BaseConstellation usedConstellation;
    private final Set<UUID> combineItems;
    private int duration;

    private ActiveFocalCombineRecipe(FocalCombineRecipe recipe, BaseConstellation usedConstellation, Set<UUID> combineItems, int duration) {
        this.recipe = recipe;
        this.usedConstellation = usedConstellation;
        this.combineItems = combineItems;
        this.duration = duration;
    }

    public FocalCombineRecipe getRecipe() {
        return this.recipe;
    }

    public static ActiveFocalCombineRecipe create(FocalCombineRecipe recipe, BaseConstellation cst, FocalCombineCraftingInput input) {
        Set<UUID> itemEntityIds = recipe.filterNecessaryItems(input).stream()
                .map(Entity::getUUID)
                .collect(Collectors.toSet());
        return new ActiveFocalCombineRecipe(recipe, cst, itemEntityIds, recipe.getDuration());
    }

    public static Optional<ActiveFocalCombineRecipe> tryFindAtOpenSky(ServerLevel sLevel, BaseConstellation cst, ColumnPos pos, int scanRange) {
        Set<ItemEntity> visitedItems = new HashSet<>();
        Predicate<ItemEntity> notVisited = e -> !visitedItems.contains(e) && e.isAlive() && e.getItem().getCount() > 0;
        BlockPos centerPos = pos.toBlockPos(0);
        Vec3i offset = new Vec3i(scanRange, 0, scanRange);

        BlockPos.betweenClosedStream(centerPos.subtract(offset), centerPos.offset(offset)).forEach(offsetPos ->
                ChunkUtil.executeWithChunk(sLevel, offsetPos, () -> {
                    BlockPos surfacePos = sLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, offsetPos);
                    visitedItems.addAll(sLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(surfacePos).inflate(2), notVisited));
                }));
        if (visitedItems.isEmpty()) {
            return Optional.empty();
        }

        RecipeFinder finder = RecipeFinder.of(sLevel);
        for (ItemEntity visited : visitedItems) {
            List<ItemEntity> combinable = visitedItems.stream()
                    .filter(e -> e.distanceTo(visited) < 0.5)
                    .toList();

            Optional<ActiveFocalCombineRecipe> recipeOpt = finder.findFocalCombineRecipe(sLevel, cst, combinable).map(holder -> {
                FocalCombineCraftingInput input = new FocalCombineCraftingInput(cst, sLevel, combinable);
                return create(holder.value(), cst, input);
            });
            if (recipeOpt.isPresent()) {
                return recipeOpt;
            }
        }
        return Optional.empty();
    }

    public Optional<FocalCombineCraftingInput> match(ServerLevel sLevel) {
        List<ItemEntity> itemEntities = this.combineItems.stream()
                .map(sLevel::getEntity)
                .filter(e -> e instanceof ItemEntity)
                .map(e -> (ItemEntity) e)
                .filter(Entity::isAlive)
                .filter(itemEntity -> itemEntity.getItem().getCount() > 0)
                .toList();
        Set<UUID> aliveItems = itemEntities.stream()
                .map(Entity::getUUID)
                .collect(Collectors.toSet());
        this.combineItems.removeIf(id -> !aliveItems.contains(id));

        FocalCombineCraftingInput runningInput = new FocalCombineCraftingInput(this.usedConstellation, sLevel, itemEntities);
        if (this.recipe.matches(runningInput, sLevel)) {
            return Optional.of(runningInput);
        }
        return Optional.empty();
    }

    public void tick() {
        this.duration--;
    }

    public boolean isFinished() {
        return this.duration <= 0;
    }

    public void finish(FocalCombineCraftingInput testedInput, ServerLevel sLevel) {
        this.recipe.consumeInputs(testedInput, sLevel.registryAccess());
        this.recipe.createOutput(testedInput, sLevel.registryAccess());
    }
}
