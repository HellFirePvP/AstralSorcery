/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockLinkConnection
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record EntityLinkConnection(UUID entityUid) {

    public static final Codec<EntityLinkConnection> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CodecUtil.uuidCodec().fieldOf("entityUid").forGetter(EntityLinkConnection::entityUid)
    ).apply(inst, EntityLinkConnection::new));

    public Optional<Entity> resolveEntity(ServerLevel level) {
        return Optional.ofNullable(level.getEntity(this.entityUid()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EntityLinkConnection that = (EntityLinkConnection) o;
        return Objects.equals(entityUid, that.entityUid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityUid);
    }
}
