/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network.handlers;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarmetalFormHandler
 * Created by HellFirePvP
 * Date: 05.10.2016 / 19:25
 */
public class StarmetalFormHandler implements StarlightNetworkRegistry.IStarlightBlockHandler {

    private static Map<BlockPos, IronOreReceiverNode> turningIrons = new HashMap<>();

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, @Nullable IWeakConstellation starlightType, double amount) {
        long ms = System.currentTimeMillis();
        if(!turningIrons.containsKey(pos)) {
            IronOreReceiverNode node = new IronOreReceiverNode();
            turningIrons.put(pos, node);
            node.lastMSrec = ms;
            node.accCharge = 0D;
        }
        IronOreReceiverNode node = turningIrons.get(pos);
        long diff = ms - node.lastMSrec;
        if(diff >= 18_000) node.accCharge = 0;
        node.accCharge += amount;
        node.lastMSrec = ms;

        PktParticleEvent pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.STARMETAL_ORE_CHARGE, pos.getX(), pos.getY(), pos.getZ());
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 16));

        if(node.accCharge >= 13_000) {
            turningIrons.remove(pos);

            world.setBlockState(pos, BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.STARMETAL));
        }
    }

    public static class IronOreReceiverNode {

        private long lastMSrec;
        private double accCharge;

    }

}
