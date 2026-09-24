/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.place;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.visual.type.FocalPointTransmutationSparkle;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveTransmutationHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveTransmutationHandler {

    private static final Map<ResourceKey<Level>, Map<BlockPos, ActiveFocalTransmutationRecipe>> activeRecipes = new HashMap<>();

    private static void removeTransmutation(ServerLevel level, BlockPos pos) {
        activeRecipes.getOrDefault(level.dimension(), new HashMap<>()).remove(pos);
    }

    public static Optional<ActiveFocalTransmutationRecipe> getActiveTransmutation(ServerLevel level, BlockPos pos) {
        return Optional.ofNullable(activeRecipes.getOrDefault(level.dimension(), Collections.emptyMap()).get(pos));
    }

    public static Optional<ActiveFocalTransmutationRecipe> getOrCreateActiveTransmutation(ServerLevel level, BlockPos pos, BaseConstellation cst, boolean isFocusedStarlight) {
        Optional<ActiveFocalTransmutationRecipe> recipe = getActiveTransmutation(level, pos);
        if (recipe.isPresent()) return recipe;
        return ChunkUtil.executeWithChunk(level, pos, () -> {
            return RecipeFinder.of(level).findFocalTransmutationRecipe(level, cst, pos, isFocusedStarlight).map(r -> {
                ActiveFocalTransmutationRecipe activeRecipe = ActiveFocalTransmutationRecipe.create(r.value(), cst, pos, level.getGameTime());
                activeRecipes.computeIfAbsent(level.dimension(), dim -> new HashMap<>()).put(pos, activeRecipe);
                return activeRecipe;
            });
        }, Optional.empty());
    }

    public static TriState receiveStarlight(ServerLevel level, BlockPos pos, StarlightTransmissionPacket packet) {
        return receiveStarlight(level, pos, packet.constellation(), packet.amount(), true);
    }

    // false -> no recipe/nothing happened, true -> success, state still valid, default -> success, no longer valid in the future though from this view
    public static TriState receiveStarlight(ServerLevel level, BlockPos pos, BaseConstellation cst, float amount, boolean isFocused) {
        Optional<ActiveFocalTransmutationRecipe> activeRecipeOpt = getOrCreateActiveTransmutation(level, pos, cst, isFocused);
        if (activeRecipeOpt.isEmpty()) {
            return TriState.FALSE;
        }
        ActiveFocalTransmutationRecipe activeRecipe = activeRecipeOpt.get();
        return activeRecipe.match(level, isFocused).map(usedInput -> {
            if (!activeRecipe.tick(level.getGameTime(), 1F)) {
                removeTransmutation(level, pos);
                return TriState.FALSE;
            }
            FocalPointTransmutationSparkle.at(activeRecipe.getTransmutationPos(), activeRecipe.getRecipe().getColor()).sendToNearby(level);
            if (activeRecipe.isFinished()) {
                activeRecipe.finish(usedInput, level);
                removeTransmutation(level, pos);
                return TriState.DEFAULT;
            }
            return TriState.TRUE;
        }).orElse(TriState.FALSE);
    }
}
