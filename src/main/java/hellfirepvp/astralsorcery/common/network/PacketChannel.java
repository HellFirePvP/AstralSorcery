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
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.network.channel.BufferedReplyChannel;
import hellfirepvp.astralsorcery.common.network.channel.SimpleSendChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktCraftingTableFix;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PacketChannel
 * Created by HellFirePvP
 * Date: 21.04.2019 / 19:34
 */
public class PacketChannel {

    private static int packetIndex = 0;
    private static final String NET_COMM_VERSION = "0"; //AS network version

    public static final SimpleSendChannel CHANNEL = new BufferedReplyChannel(
            ReflectionHelper.createInstance(
                    new ResourceLocation(AstralSorcery.MODID, "net_channel"),
                    () -> NET_COMM_VERSION,
                    NET_COMM_VERSION::equals,
                    NET_COMM_VERSION::equals));

    public static void registerPackets() {
        // DEDICATED_SERVER -> CLIENT
        registerMessage(PktCraftingTableFix::new);


        // CLIENT -> DEDICATED_SERVER
    }

    private static <T extends ASPacket<T>> void registerMessage(Supplier<T> pktSupplier) {
        T packet = pktSupplier.get();
        CHANNEL.messageBuilder((Class<T>) packet.getClass(), packetIndex++)
                .encoder(packet.encoder())
                .decoder(packet.decoder())
                .consumer(packet.handler())
                .add();
    }

    public static PacketDistributor.TargetPoint pointFromPos(World world, Vec3i pos, double range) {
        return new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, world.getDimension().getType());
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean canBeSentToServer() {
        return ClientProxy.connected;
    }

}
