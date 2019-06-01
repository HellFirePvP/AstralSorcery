/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktCraftingTableFix
 * Created by HellFirePvP
 * Date: 01.06.2019 / 18:03
 */
public class PktCraftingTableFix extends ASPacket<PktCraftingTableFix> {

    private BlockPos at;

    public PktCraftingTableFix() {}

    public PktCraftingTableFix(BlockPos at) {
        this.at = at;
    }

    @Override
    @Nonnull
    public Encoder<PktCraftingTableFix> encoder() {
        return (pktCraftingTableFix, buffer) -> {
            ByteBufUtils.writePos(buffer, pktCraftingTableFix.at);
        };
    }

    @Override
    @Nonnull
    public Decoder<PktCraftingTableFix> decoder() {
        return buffer -> {
            return new PktCraftingTableFix(ByteBufUtils.readPos(buffer));
        };
    }

    @Override
    @Nonnull
    public Handler<PktCraftingTableFix> handler() {
        return (message, ctx, side) -> {

        };
    }
}
