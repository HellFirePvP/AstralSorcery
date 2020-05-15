/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.network.base.ASLoginPacket;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.network.channel.BufferedReplyChannel;
import hellfirepvp.astralsorcery.common.network.channel.SimpleSendChannel;
import hellfirepvp.astralsorcery.common.network.login.client.PktLoginAcknowledge;
import hellfirepvp.astralsorcery.common.network.login.server.PktLoginSyncDataHolder;
import hellfirepvp.astralsorcery.common.network.login.server.PktLoginSyncGateway;
import hellfirepvp.astralsorcery.common.network.play.client.*;
import hellfirepvp.astralsorcery.common.network.play.server.*;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.function.BiConsumer;
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

    public static final SimpleSendChannel CHANNEL = new BufferedReplyChannel(NetworkRegistry.newSimpleChannel(
            AstralSorcery.key("net_channel"),
            () -> NET_COMM_VERSION,
            NET_COMM_VERSION::equals,
            NET_COMM_VERSION::equals));

    public static void registerPackets() {
        // LOGIN DEDICATED_SERVER -> CLIENT
        registerLoginMessage(PktLoginSyncDataHolder::new, PktLoginSyncDataHolder::makeLogin);
        registerLoginMessage(PktLoginSyncGateway::new, PktLoginSyncGateway::makeLogin);

        // LOGIN CLIENT -> DEDICATED_SERVER
        registerLoginMessage(PktLoginAcknowledge::new, PktLoginAcknowledge::new);

        // PLAY DEDICATED_SERVER -> CLIENT
        registerMessage(PktOreScan::new);
        registerMessage(PktPlayEffect::new);
        registerMessage(PktPlayLiquidInteraction::new);
        registerMessage(PktProgressionUpdate::new);
        registerMessage(PktShootEntity::new);
        registerMessage(PktSyncCharge::new);
        registerMessage(PktSyncData::new);
        registerMessage(PktSyncKnowledge::new);
        registerMessage(PktSyncModifierSource::new);
        registerMessage(PktSyncPerkActivity::new);
        registerMessage(PktSyncStepAssist::new);
        registerMessage(PktUpdateGateways::new);
        registerMessage(PktOpenGui::new);

        // PLAY CLIENT -> DEDICATED_SERVER
        registerMessage(PktAttunePlayerConstellation::new);
        registerMessage(PktClearBlockStorageStack::new);
        registerMessage(PktDiscoverConstellation::new);
        registerMessage(PktElytraCapeState::new);
        registerMessage(PktEngraveGlass::new);
        registerMessage(PktPerkGemModification::new);
        registerMessage(PktRequestPerkSealAction::new);
        registerMessage(PktRequestSeed::new);
        registerMessage(PktRequestTeleport::new);
        registerMessage(PktRotateTelescope::new);
        registerMessage(PktUnlockPerk::new);
        registerMessage(PktToggleClientOption::new);
    }

    private static <T extends ASLoginPacket<T>> void registerLoginMessage(Supplier<T> pktSupplier, Supplier<T> makeLoginPacket) {
        T packet = pktSupplier.get();
        int index = packetIndex++;
        CHANNEL.messageBuilder((Class<T>) packet.getClass(), index)
                .loginIndex(ASLoginPacket::getLoginIndex, ASLoginPacket::setLoginIndex)
                .encoder(packet.encoder())
                .decoder(packet.decoder())
                .consumer((t, contextSupplier) -> {
                    BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
                    if (contextSupplier.get().getDirection().getReceptionSide().isServer()) {
                        handler = FMLHandshakeHandler.indexFirst((handshakeHandler, pkt, ctxSupplier) -> packet.handler().accept(pkt, ctxSupplier));
                    } else {
                        handler = packet.handler();
                    }

                    handler.accept(t, contextSupplier);
                })
                .buildLoginPacketList((local) -> Collections.singletonList(Pair.of(packet.getClass().getName(), makeLoginPacket.get())))
                .add();
    }

    private static <T extends ASPacket<T>> void registerMessage(Supplier<T> pktSupplier) {
        T packet = pktSupplier.get();
        CHANNEL.messageBuilder((Class<T>) packet.getClass(), packetIndex++)
                .encoder(packet.encoder())
                .decoder(packet.decoder())
                .consumer(packet.handler())
                .add();
    }

    public static PacketDistributor.TargetPoint pointFromPos(IWorld world, Vec3i pos, double range) {
        return new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), range, world.getDimension().getType());
    }

}
