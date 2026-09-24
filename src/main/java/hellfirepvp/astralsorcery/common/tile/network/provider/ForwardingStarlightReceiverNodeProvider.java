/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network.provider;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.ForwardingStarlightReceiverNode;
import net.minecraft.core.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ForwardingStarlightReceiverNodeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ForwardingStarlightReceiverNodeProvider implements TransmissionReceiverNodeProvider<ForwardingStarlightReceiverNode> {

    @Override
    public ForwardingStarlightReceiverNode provideNewNode(BlockPos pos) {
        return new ForwardingStarlightReceiverNode(pos);
    }

    @Override
    public MapCodec<ForwardingStarlightReceiverNode> nodeCodec() {
        return ForwardingStarlightReceiverNode.CODEC;
    }
}
