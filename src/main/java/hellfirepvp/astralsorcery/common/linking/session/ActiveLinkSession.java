/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking.session;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.linking.Linkable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveLinkSession
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ActiveLinkSession(UUID playerId, ResolvableLink selected, List<ResolvableLink> linked, @Nullable BiFunction<ActiveLinkSession, ServerLevel, Optional<Linkable>> serverLinkableProvider) {

    public static final Codec<ActiveLinkSession> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("playerId").xmap(UUID::fromString, UUID::toString).forGetter(ActiveLinkSession::playerId),
            ResolvableLink.CODEC.fieldOf("selected").forGetter(ActiveLinkSession::selected),
            ResolvableLink.CODEC.listOf().fieldOf("linked").forGetter(ActiveLinkSession::linked)
    ).apply(inst, ActiveLinkSession::unresolvable));

    public static ActiveLinkSession unresolvable(UUID playerId, ResolvableLink selected, List<ResolvableLink> linked) {
        return new ActiveLinkSession(playerId, selected, linked, null);
    }

    public Optional<Linkable> resolve(ServerLevel level) {
        if (this.serverLinkableProvider == null) return Optional.empty();
        return this.serverLinkableProvider.apply(this, level);
    }

    public ActiveLinkSession updateLinks(List<ResolvableLink> linked) {
        return new ActiveLinkSession(this.playerId, this.selected, linked, this.serverLinkableProvider);
    }

    public static class ResolvableLink {

        public static final Codec<ResolvableLink> CODEC = Codec.either(BlockPos.CODEC, Codec.INT)
                .xmap(either -> either.map(ResolvableLink::pos, ResolvableLink::entity),
                        link -> link.linkedPos != null ? Either.left(link.linkedPos) : Either.right(link.linkedEntityId));

        private final BlockPos linkedPos;
        private final int linkedEntityId;

        private ResolvableLink(BlockPos linkedPos, int linkedEntityId) {
            this.linkedPos = linkedPos;
            this.linkedEntityId = linkedEntityId;
        }

        public static ResolvableLink pos(BlockPos pos) {
            return new ResolvableLink(pos, -1);
        }

        public static ResolvableLink entity(int entityId) {
            return new ResolvableLink(null, entityId);
        }

        public void ifPos(Consumer<BlockPos> consumer) {
            if (this.linkedPos != null) {
                consumer.accept(this.linkedPos);
            }
        }

        public void ifEntity(Consumer<Integer> consumer) {
            if (this.linkedPos == null) {
                consumer.accept(this.linkedEntityId);
            }
        }

        public Optional<Vector3> resolveLocation(Level level) {
            if (this.linkedPos != null) {
                return Optional.of(Vector3.atCenter(this.linkedPos));
            } else {
                Entity entity = level.getEntity(this.linkedEntityId);
                return entity != null ? Optional.of(new Vector3(entity)) : Optional.empty();
            }
        }
    }
}
