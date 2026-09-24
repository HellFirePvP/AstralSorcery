/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WeakPlayerReferenceComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record WeakPlayerReferenceComponent(UUID playerUUID) {

    public static final WeakPlayerReferenceComponent NONE = new WeakPlayerReferenceComponent(Util.NIL_UUID);

    public static final StreamCodec<RegistryFriendlyByteBuf, WeakPlayerReferenceComponent> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.uuidStreamCodec(),
            WeakPlayerReferenceComponent::playerUUID,
            WeakPlayerReferenceComponent::new
    );

    public Optional<Player> resolvePlayer(Level sLevel) {
        return Optional.ofNullable(sLevel.getPlayerByUUID(this.playerUUID()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeakPlayerReferenceComponent that = (WeakPlayerReferenceComponent) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerUUID);
    }
}
