/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network.provider;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.linking.LinkContainer;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.network.SimpleTransmissionNode;
import net.minecraft.core.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SimpleTransmissionNodeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SimpleTransmissionNodeProvider implements TransmissionNodeProvider<SimpleTransmissionNode> {

    @Override
    public SimpleTransmissionNode provideNewNode(BlockPos pos) {
        return new SimpleTransmissionNode(pos);
    }

    @Override
    public MapCodec<SimpleTransmissionNode> nodeCodec() {
        return SimpleTransmissionNode.CODEC;
    }
}
