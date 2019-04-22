/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network.handlers;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightNetworkRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTransmutationHandler
 * Created by HellFirePvP
 * Date: 30.01.2017 / 12:38
 */
public class BlockTransmutationHandler implements StarlightNetworkRegistry.IStarlightBlockHandler {

    private static Map<BlockPos, ActiveTransmutation> runningTransmutations = new HashMap<>();

    @Override
    @Deprecated
    public boolean isApplicable(World world, BlockPos pos, IBlockState state) {
        return false;
    }

    @Override
    public boolean isApplicable(World world, BlockPos pos, IBlockState state, @Nullable IWeakConstellation starlightType) {
        return LightOreTransmutations.searchForTransmutation(state) != null;
    }

    @Override
    public void receiveStarlight(World world, Random rand, BlockPos pos, @Nullable IWeakConstellation starlightType, double amount) {
        long ms = System.currentTimeMillis();

        if(!runningTransmutations.containsKey(pos)) {
            IBlockState tryStateIn = world.getBlockState(pos);
            LightOreTransmutations.Transmutation tr = LightOreTransmutations.searchForTransmutation(tryStateIn);
            if(tr != null && (tr.getRequiredType() == null || tr.getRequiredType().equals(starlightType))) {
                ActiveTransmutation atr = new ActiveTransmutation();
                runningTransmutations.put(pos, atr);
                atr.accCharge = 0D;
                atr.lastMSrec = ms;
                atr.runningTransmutation = tr;
            } else {
                return;
            }
        }
        ActiveTransmutation node = runningTransmutations.get(pos);
        if(LightOreTransmutations.searchForTransmutation(world.getBlockState(pos)) != node.runningTransmutation) {
            runningTransmutations.remove(pos);
            return;
        }
        //Accept, but don't add charge
        if (node.runningTransmutation.getRequiredType() != null && !node.runningTransmutation.getRequiredType().equals(starlightType)) {
            return;
        }
        long diff = ms - node.lastMSrec;
        if(diff >= 15_000) node.accCharge = 0;
        node.accCharge += amount;
        node.lastMSrec = ms;

        if (ParticleEffectWatcher.INSTANCE.mayFire(world, pos)) {
            PktParticleEvent pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.TRANSMUTATION_CHARGE, pos.getX(), pos.getY(), pos.getZ());
            PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 16));
        }

        if(node.accCharge >= node.runningTransmutation.getCost()) {
            if (!world.setBlockState(pos, node.runningTransmutation.getOutput())) {
                node.accCharge -= 1000;
            } else {
                runningTransmutations.remove(pos);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public static void playTransmutationEffects(PktParticleEvent event) {
        Random rand = EffectHandler.STATIC_EFFECT_RAND;

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                event.getVec().getX() + rand.nextFloat(),
                event.getVec().getY() + rand.nextFloat(),
                event.getVec().getZ() + rand.nextFloat());
        p.motion(0, rand.nextFloat() * 0.05, 0);
        p.scale(0.2F).setColor(Color.WHITE);
    }

    public static class ActiveTransmutation {

        private LightOreTransmutations.Transmutation runningTransmutation;
        private long lastMSrec;
        private double accCharge;

    }

}
