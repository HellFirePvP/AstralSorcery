/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.linking.LinkContainer;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import net.minecraft.core.BlockPos;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SimpleTransmissionNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SimpleSingleTransmissionNode extends SimpleTransmissionNode {

    public static final MapCodec<SimpleSingleTransmissionNode> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(SimpleSingleTransmissionNode::getNodePos),
            LinkContainer.CODEC.fieldOf("linkContainer").forGetter(node -> node.linkContainer),
            Codec.FLOAT.fieldOf("lossMultiplier").forGetter(SimpleTransmissionNode::getLossMultiplier)
    ).apply(inst, SimpleSingleTransmissionNode::new));

    public SimpleSingleTransmissionNode(BlockPos pos) {
        super(pos);
    }

    public SimpleSingleTransmissionNode(BlockPos pos, LinkContainer linkContainer, float lossMultiplier) {
        super(pos, linkContainer, lossMultiplier);
    }

    @Override
    public TransmissionNodeProvider<?> getProvider() {
        return StarlightNetworkNodesAS.SIMPLE_SINGLE_NODE.value();
    }

    @Override
    public Optional<Integer> getMaxBlockLinkCount() {
        return Optional.of(1);
    }
}
