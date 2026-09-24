/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RecipeUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RecipeUtil {

    @Nullable
    public static RecipeManager getRecipeManager() {
        if (EffectiveSide.get().isClient()) {
            return getClientRecipeManager();
        }
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        return srv == null ? null : srv.getRecipeManager();
    }

    @OnlyIn(Dist.CLIENT)
    private static RecipeManager getClientRecipeManager() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return null;
        return connection.getRecipeManager();
    }

    public static Set<Lumen> findDirectLumenMakingUp(Level level, Lumen target) {
        Set<Lumen> results = new HashSet<>();
        RecipeFinder finder = RecipeFinder.of(level);

        finder.findLumenGenerationRecipeByOutput(target).ifPresent(recipeRef -> {
            results.addAll(recipeRef.value().getLumenCombinationInputs().keySet());
        });

        return results;
    }

    public static Set<Lumen> findAnyLumenMakingUp(Level level, Lumen target) {
        Set<Lumen> results = new HashSet<>();
        RecipeFinder finder = RecipeFinder.of(level);

        Deque<Lumen> parts = new ArrayDeque<>();
        parts.add(target);
        while (!parts.isEmpty()) {
            Lumen part = parts.pop();
            finder.findLumenGenerationRecipeByOutput(part).ifPresent(recipeRef -> {
                recipeRef.value().getLumenCombinationInputs().keySet().forEach(input -> {
                    if (results.add(input)) {
                        parts.add(input);
                    }
                });
            });
        }

        return results;
    }
}
