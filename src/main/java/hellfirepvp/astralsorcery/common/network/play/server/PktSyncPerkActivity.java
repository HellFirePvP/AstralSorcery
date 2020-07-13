/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncPerkActivity
 * Created by HellFirePvP
 * Date: 02.06.2019 / 08:35
 */
public class PktSyncPerkActivity extends ASPacket<PktSyncPerkActivity> {

    private Type type = null;
    private AbstractPerk perk = null;
    private CompoundNBT newData = null, oldData = null;

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(Type type) {
        if (type == Type.DATACHANGE) {
            throw new IllegalArgumentException("Passed Datachange-Type without supplying data!");
        }
        this.type = type;
    }

    public PktSyncPerkActivity(AbstractPerk perk, CompoundNBT oldData, CompoundNBT newData) {
        this.type = Type.DATACHANGE;
        this.perk = perk;
        this.oldData = oldData;
        this.newData = newData;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncPerkActivity> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeEnumValue);

            ByteBufUtils.writeOptional(buffer, packet.perk, ByteBufUtils::writeRegistryEntry);
            ByteBufUtils.writeOptional(buffer, packet.newData, ByteBufUtils::writeNBTTag);
            ByteBufUtils.writeOptional(buffer, packet.oldData, ByteBufUtils::writeNBTTag);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncPerkActivity> decoder() {
        return buffer -> {
            PktSyncPerkActivity pkt = new PktSyncPerkActivity();

            pkt.type = ByteBufUtils.readOptional(buffer, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, Type.class));

            pkt.perk = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.newData = ByteBufUtils.readOptional(buffer, ByteBufUtils::readNBTTag);
            pkt.oldData = ByteBufUtils.readOptional(buffer, ByteBufUtils::readNBTTag);
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSyncPerkActivity> handler() {
        return new Handler<PktSyncPerkActivity>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncPerkActivity packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player == null) {
                        return;
                    }

                    switch (packet.type) {
                        case CLEARALL:
                            PerkEffectHelper.clientClearAllPerks();
                            break;
                        case UNLOCKALL:
                            PerkEffectHelper.clientRefreshAllPerks();
                            break;
                        case DATACHANGE:
                            PerkEffectHelper.clientChangePerkData(packet.perk, packet.oldData, packet.newData);
                            break;
                        default:
                            break;
                    }
                });
            }

            @Override
            public void handle(PktSyncPerkActivity packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    public static enum Type {

        CLEARALL,
        UNLOCKALL,
        DATACHANGE

    }
}
