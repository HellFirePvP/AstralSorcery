/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionReceiverNode;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ForwardingStarlightReceiverNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ForwardingStarlightReceiverNode implements TransmissionReceiverNode {

    public static final MapCodec<ForwardingStarlightReceiverNode> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(ForwardingStarlightReceiverNode::getNodePos)
    ).apply(inst, ForwardingStarlightReceiverNode::new));

    private final BlockPos pos;

    public ForwardingStarlightReceiverNode(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet) {
        MiscUtil.getTileAt(sLevel, this.getNodePos(), ReceiverTile.class, false)
                .ifPresent(tile -> tile.receiveStarlight(sLevel, packet));
    }

    @Override
    public BlockPos getNodePos() {
        return this.pos;
    }

    @Override
    public TransmissionNodeProvider<?> getProvider() {
        return StarlightNetworkNodesAS.FORWARDING_RECEIVER_NODE.value();
    }

    public static interface ReceiverTile extends IBlockEntityExtension {

        void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet);

    }
}
