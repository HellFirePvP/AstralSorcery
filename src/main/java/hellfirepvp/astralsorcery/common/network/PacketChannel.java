/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.common.network.channel.BufferedReplyChannel;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PacketChannel
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:34
 */
public class PacketChannel {

    private static final String NET_COMM_VERSION = "0";
    public static final SimpleChannel CHANNEL = new BufferedReplyChannel(
            ReflectionHelper.createInstance(
                    new ResourceLocation(AstralSorcery.MODID, "net_channel"),
                    () -> NET_COMM_VERSION,
                    NET_COMM_VERSION::equals,
                    NET_COMM_VERSION::equals));

    @OnlyIn(Dist.CLIENT)
    public static boolean canBeSentToServer() {
        return ClientProxy.connected;
    }

    public static void setupChannel() {
        int id = 0;


    }

    public static PacketDistributor.TargetPoint pointFromPos(World world, Vec3i pos, double range) {
        return new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, world.getDimension().getType());
    }

}
