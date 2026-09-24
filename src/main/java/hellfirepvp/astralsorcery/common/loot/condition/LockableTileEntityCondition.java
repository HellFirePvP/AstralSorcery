/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.condition;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.base.TileDataOwned;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LockableTileEntityCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LockableTileEntityCondition implements LootItemCondition {

    private static final LockableTileEntityCondition INSTANCE = new LockableTileEntityCondition();
    public static final MapCodec<LockableTileEntityCondition> CODEC = MapCodec.unit(LockableTileEntityCondition::getInstance);

    private LockableTileEntityCondition() {}

    public static LockableTileEntityCondition getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (lootContext.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity tile = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (tile instanceof TileEntitySynchronized<?> baseTile && baseTile.getTileData() instanceof TileDataOwned.Lockable lockableData) {
                return lockableData.isLocked();
            }
        }
        return false;
    }

    @Override
    public LootItemConditionType getType() {
        return LootAS.LOCKABLE_TILE_ENTITY_CONDITION.get();
    }

    public static LootItemCondition.Builder lockable() {
        return LockableTileEntityCondition::getInstance;
    }
}
