/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileDataOwned
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TileDataOwned {

    static <T extends TileDataOwned> Products.P1<RecordCodecBuilder.Mu<T>, Optional<UUID>> ownedFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                CodecUtil.optional(CodecUtil.uuidCodec(), "ownerId", TileDataOwned::getOwnerId)
        );
    }

    default <T extends TileEntitySynchronized.Data> T self() {
        return MiscUtil.cast(this);
    }

    default void markForUpdate() {
        this.self().markForUpdate();
    }

    default void markDirty() {
        this.self().markDirty();
    }

    default boolean isOwner(Player player) {
        return player != null && this.isOwner(player.getUUID());
    }

    default boolean isOwner(UUID playerId) {
        return this.getOwnerId() != null && this.getOwnerId().equals(playerId);
    }

    @Nullable
    UUID getOwnerId();

    void setOwnerId(UUID ownerId);

    default Optional<ServerPlayer> getOwner(ServerLevel sLevel)  {
        return this.getOwner(sLevel.getServer());
    }

    default Optional<ServerPlayer> getOwner(MinecraftServer server)  {
        UUID playerId = this.getOwnerId();
        if (playerId != null) {
            return Optional.ofNullable(server.getPlayerList().getPlayer(playerId));
        }
        return Optional.empty();
    }

    default boolean hasOwner() {
        return this.getOwnerId() != null;
    }

    interface Lockable extends TileDataOwned {

        static <T extends TileDataOwned.Lockable> Products.P2<RecordCodecBuilder.Mu<T>, Optional<UUID>, Boolean> ownedLockableFields(RecordCodecBuilder.Instance<T> instance) {
            return ownedFields(instance).and(
                    CodecUtil.defaulted(Codec.BOOL, "locked", () -> false, TileDataOwned.Lockable::isLocked)
            );
        }

        void setLocked(boolean locked);

        boolean isLocked();
    }
}
