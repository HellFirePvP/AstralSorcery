/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.item.base.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktClearBlockStorageStack
 * Created by HellFirePvP
 * Date: 02.06.2019 / 12:30
 */
public class PktClearBlockStorageStack extends ASPacket<PktClearBlockStorageStack> {

    @Nonnull
    @Override
    public Encoder<PktClearBlockStorageStack> encoder() {
        return (packet, buffer) -> {};
    }

    @Nonnull
    @Override
    public Decoder<PktClearBlockStorageStack> decoder() {
        return buffer -> new PktClearBlockStorageStack();
    }

    @Nonnull
    @Override
    public Handler<PktClearBlockStorageStack> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                if (side == LogicalSide.SERVER) {
                    ItemBlockStorage.clearContainerFor(context.getSender());
                }
            });
        };
    }
}
