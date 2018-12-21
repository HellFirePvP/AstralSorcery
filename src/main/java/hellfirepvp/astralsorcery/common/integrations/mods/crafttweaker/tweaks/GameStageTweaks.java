/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GameStageTweaks
 * Created by HellFirePvP
 * Date: 16.11.2018 / 17:17
 */
@ZenClass("mods.astralsorcery.GameStages")
public class GameStageTweaks extends BaseTweaker {

    private static Map<String, Integer> stageLevelCap = new HashMap<>();
    private static Map<String, Collection<String>> constellationStages = new HashMap<>();

    @ZenMethod
    public static void addLevelCap(String stageName, int levelCap) {
        stageLevelCap.put(stageName, levelCap);
    }

    @ZenMethod
    public static void addConstellationDiscoveryStage(String stageName, String unlocalizedConstellationName) {
        constellationStages.computeIfAbsent(unlocalizedConstellationName, str -> Lists.newArrayList())
                .add(stageName);
    }

    public static int getMaxCap(String gameStageName) {
        return stageLevelCap.getOrDefault(gameStageName, -1);
    }

    public static boolean canDiscover(Collection<String> gameStages, String constellationName) {
        Collection<String> stages = constellationStages.getOrDefault(constellationName, Lists.newArrayList());
        if (stages.isEmpty()) {
            return true;
        }
        for (String gameStage : gameStages) {
            if (stages.contains(gameStage)) {
                return true;
            }
        }
        return false;
    }

}
