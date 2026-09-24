/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VectorPosEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class VectorPosEffect implements VisualEffectTypes.Effect {

    private final Vector3 pos;

    protected VectorPosEffect(Vector3 pos) {
        this.pos = pos;
    }

    public final BlockPos getBlockPos() {
        return this.pos.toBlockPos();
    }

    public final Vector3 getPos() {
        return this.pos.copy();
    }

    public void sendToNearby(ServerLevel sLevel) {
        this.sendToNearby(sLevel, this.getBlockPos());
    }

    protected static <T extends VectorPosEffect> StreamCodec<RegistryFriendlyByteBuf, T> createCodec(Function<Vector3, T> factory) {
        return StreamCodec.composite(Vector3.STREAM_CODEC, VectorPosEffect::getPos, factory);
    }
}
