/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityMatchPredicate
 * Created by HellFirePvP
 * Date: 08.10.2026 / 21:16
 */
public class EntityMatchPredicate {

    public static final Codec<EntityMatchPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            UUIDUtil.CODEC.optionalFieldOf("ownerId").forGetter(EntityMatchPredicate::getOwnerId),
            Codec.BOOL.fieldOf("targetPlayers").forGetter(EntityMatchPredicate::doesTargetPlayers),
            EntityPredicate.CODEC.fieldOf("entityPredicate").forGetter(EntityMatchPredicate::getEntityPredicate)
    ).apply(inst, EntityMatchPredicate::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityMatchPredicate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), EntityMatchPredicate::getOwnerId,
            ByteBufCodecs.BOOL, EntityMatchPredicate::doesTargetPlayers,
            (id, targetPlayers) -> new EntityMatchPredicate(id, targetPlayers, EntityPredicate.Builder.entity().build())
    );

    private final Optional<UUID> ownerId;
    private final boolean targetPlayers;
    private final EntityPredicate entityPredicate;

    public EntityMatchPredicate(Optional<UUID> ownerId, boolean targetPlayers, EntityPredicate entityPredicate) {
        this.ownerId = ownerId;
        this.targetPlayers = targetPlayers;
        this.entityPredicate = entityPredicate;
    }

    public static EntityMatchPredicate withPlayers(Optional<Entity> owner) {
        return withPlayers(owner, EntityPredicate.Builder.entity());
    }

    public static EntityMatchPredicate withPlayers(Optional<Entity> owner, EntityPredicate.Builder filter) {
        return new EntityMatchPredicate(owner.map(Entity::getUUID), true, filter.build());
    }

    public static EntityMatchPredicate withoutPlayers(Optional<Entity> owner) {
        return withoutPlayers(owner, EntityPredicate.Builder.entity());
    }

    public static EntityMatchPredicate withoutPlayers(Optional<Entity> owner, EntityPredicate.Builder filter) {
        return new EntityMatchPredicate(owner.map(Entity::getUUID), false, filter.build());
    }

    public Optional<UUID> getOwnerId() {
        return this.ownerId;
    }

    public boolean doesTargetPlayers() {
        return this.targetPlayers;
    }

    public EntityPredicate getEntityPredicate() {
        return this.entityPredicate;
    }
}
