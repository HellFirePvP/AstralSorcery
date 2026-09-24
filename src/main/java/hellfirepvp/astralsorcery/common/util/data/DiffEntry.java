/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DiffEntry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record DiffEntry<T>(T value, DiffEntry.Type type) {

    public static <T> StreamCodec<RegistryFriendlyByteBuf, DiffEntry<T>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, T> valueCodec) {
        return StreamCodec.composite(
                valueCodec,
                DiffEntry::value,
                NeoForgeStreamCodecs.enumCodec(Type.class),
                DiffEntry::type,
                DiffEntry::new);
    }

    public enum Type {

        ADDITION,
        UPDATE,
        REMOVAL

    }
}
