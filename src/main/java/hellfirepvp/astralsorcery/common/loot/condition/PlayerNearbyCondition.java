/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerNearbyCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record PlayerNearbyCondition(double distance, boolean failIfNoPlayer) implements LootItemCondition {

    public static final MapCodec<PlayerNearbyCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.DOUBLE.fieldOf("distance").forGetter(PlayerNearbyCondition::distance),
            Codec.BOOL.fieldOf("failIfNoPlayer").forGetter(PlayerNearbyCondition::failIfNoPlayer)
    ).apply(inst, PlayerNearbyCondition::new));

    @Override
    public boolean test(LootContext lootContext) {
        if (lootContext.hasParam(LootContextParams.THIS_ENTITY) && lootContext.hasParam(LootContextParams.ORIGIN)) {
            Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity instanceof Player) {
                Vec3 origin = lootContext.getParamOrNull(LootContextParams.ORIGIN);
                if (origin != null) {
                    BlockPos entityPos = entity.blockPosition();
                    double distanceSq = origin.distanceToSqr(entityPos.getX() + 0.5, entityPos.getY() + 0.5, entityPos.getZ() + 0.5);
                    return distanceSq <= (distance * distance);
                }
            }
        }
        return !this.failIfNoPlayer();
    }

    @Override
    public LootItemConditionType getType() {
        return LootAS.PLAYER_NEARBY_CONDITION.get();
    }

    public static LootItemCondition.Builder nearby(double distance, boolean failIfNoPlayer) {
        return () -> new PlayerNearbyCondition(distance, failIfNoPlayer);
    }
}
