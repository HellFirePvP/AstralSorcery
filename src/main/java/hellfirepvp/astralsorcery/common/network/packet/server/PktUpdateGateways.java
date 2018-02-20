/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUpdateGateways
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:52
 */
public class PktUpdateGateways implements IMessage, IMessageHandler<PktUpdateGateways, IMessage> {

    private Map<Integer, List<GatewayCache.GatewayNode>> positions;

    public PktUpdateGateways() {}

    public PktUpdateGateways(Map<Integer, List<GatewayCache.GatewayNode>> positions) {
        this.positions = positions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        positions = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int dimId = buf.readInt();
            int posSize = buf.readInt();
            List<GatewayCache.GatewayNode> posList = new ArrayList<>(posSize);
            for (int j = 0; j < posSize; j++) {
                BlockPos at = ByteBufUtils.readPos(buf);
                String displ = ByteBufUtils.readString(buf);
                posList.add(new GatewayCache.GatewayNode(at, displ));
            }
            positions.put(dimId, posList);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(positions.size());
        for (Integer dimKey : positions.keySet()) {
            buf.writeInt(dimKey);
            List<GatewayCache.GatewayNode> l = positions.get(dimKey);
            buf.writeInt(l.size());
            for (GatewayCache.GatewayNode pos : l) {
                ByteBufUtils.writePos(buf, pos);
                ByteBufUtils.writeString(buf, pos.display);
            }
        }
    }

    @Override
    public IMessage onMessage(PktUpdateGateways message, MessageContext ctx) {
        CelestialGatewaySystem.instance.updateClientCache(message.positions);
        return null;
    }
}
