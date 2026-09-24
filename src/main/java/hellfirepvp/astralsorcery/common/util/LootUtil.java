/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.config.server.LootTableConfig;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LootUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LootUtil {

    private LootUtil() {}

    public static boolean doesContextFulfill(LootContext ctx, LootContextParamSet set) {
        for (LootContextParam<?> required : set.getRequired()) {
            if (!ctx.hasParam(required)) {
                return false;
            }
        }
        return true;
    }

    public static List<ItemStack> generateArtifactLoot(ServerLevel sLevel, IntRange range, RandomSource rand) {
        ResourceLocation table = LootTableConfig.CONFIG.getRandomArtifactShardLootTable(rand);
        LootTable loot = sLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, table));
        LootParams params = new LootParams.Builder(sLevel).withLuck(1).create(LootContextParamSets.EMPTY);

        List<ItemStack> result = new ArrayList<>();
        int generateCount = range.getRandom(rand);
        do {
            List<ItemStack> generated = loot.getRandomItems(params, rand);
            if (generated.isEmpty()) return List.of();
            result.addAll(generated);
        } while (result.size() < generateCount);
        return result.subList(0, generateCount);
    }
}
