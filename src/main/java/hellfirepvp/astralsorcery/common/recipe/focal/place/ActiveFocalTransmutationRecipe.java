/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.place;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.data.ColumnPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveFocalTransmutationRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveFocalTransmutationRecipe {

    private final FocalTransmutationRecipe recipe;
    private final BaseConstellation usedConstellation;
    private final BlockPos transmutationPos;
    private float remainingDuration;
    private long lastTickedGameTick;

    private ActiveFocalTransmutationRecipe(FocalTransmutationRecipe recipe, BaseConstellation usedConstellation, BlockPos transmutationPos, float duration, long lastTickedGameTick) {
        this.recipe = recipe;
        this.usedConstellation = usedConstellation;
        this.transmutationPos = transmutationPos;
        this.remainingDuration = duration;
        this.lastTickedGameTick = lastTickedGameTick;
    }

    public FocalTransmutationRecipe getRecipe() {
        return this.recipe;
    }

    public BlockPos getTransmutationPos() {
        return this.transmutationPos;
    }

    public static ActiveFocalTransmutationRecipe create(FocalTransmutationRecipe recipe, BaseConstellation cst, BlockPos transmutationPos, long lastTickedGameTick) {
        return new ActiveFocalTransmutationRecipe(recipe, cst, transmutationPos, recipe.getDuration(), lastTickedGameTick);
    }

    public static Optional<ActiveFocalTransmutationRecipe> tryFindAtOpenSky(ServerLevel sLevel, BaseConstellation cst, ColumnPos pos) {
        return ChunkUtil.executeWithChunk(sLevel, pos.toBlockPos(0), () -> {
            BlockPos surfacePos = sLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos.toBlockPos(0)).below();
            return ActiveTransmutationHandler.getOrCreateActiveTransmutation(sLevel, surfacePos, cst, false);
        }, Optional.empty());
    }

    /*public static Optional<ActiveFocalTransmutationRecipe> tryFindFromFocusedStarlight(ServerLevel sLevel, BlockPos pos, BaseConstellation cst) {
        return ActiveTransmutationHandler.getOrCreateActiveTransmutation(sLevel, pos, cst, true);
    }*/

    public Optional<FocalTransmutationCraftingInput> match(ServerLevel sLevel, boolean isFocused) {
        FocalTransmutationCraftingInput runningInput = new FocalTransmutationCraftingInput(this.usedConstellation, sLevel, this.transmutationPos, isFocused);
        if (this.recipe.matches(runningInput, sLevel)) {
            return Optional.of(runningInput);
        }
        return Optional.empty();
    }

    public boolean tick(long gameTick, float reduction) {
        if (gameTick - this.lastTickedGameTick > 20) {
            return false;
        }

        this.remainingDuration -= reduction;
        this.lastTickedGameTick = gameTick;
        return true;
    }

    public boolean isFinished() {
        return this.remainingDuration <= 0;
    }

    public void finish(FocalTransmutationCraftingInput testedInput, ServerLevel sLevel) {
        this.recipe.consumeInputs(testedInput, sLevel.registryAccess());
        this.recipe.createOutput(testedInput, sLevel.registryAccess());
    }
}
