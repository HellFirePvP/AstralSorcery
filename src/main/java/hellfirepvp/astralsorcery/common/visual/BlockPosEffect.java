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
 * Class: BlockPosEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class BlockPosEffect implements VisualEffectTypes.Effect {

    private final BlockPos pos;

    protected BlockPosEffect(BlockPos pos) {
        this.pos = pos;
    }

    public final BlockPos getBlockPos() {
        return this.pos;
    }

    public final Vector3 getPos() {
        return new Vector3(this.pos);
    }

    public final Vector3 getCenteredPos() {
        return Vector3.atCenter(this.getBlockPos());
    }

    public void sendToNearby(ServerLevel sLevel) {
        this.sendToNearby(sLevel, this.getBlockPos());
    }

    protected static <T extends BlockPosEffect> StreamCodec<RegistryFriendlyByteBuf, T> createCodec(Function<BlockPos, T> factory) {
        return StreamCodec.composite(BlockPos.STREAM_CODEC, BlockPosEffect::getBlockPos, factory);
    }
}
