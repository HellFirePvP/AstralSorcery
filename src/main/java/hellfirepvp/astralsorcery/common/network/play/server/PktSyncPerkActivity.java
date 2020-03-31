/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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

    private AbstractPerk perk = null;
    private PerkEffectHelper.Action action = null;
    private CompoundNBT newData = null, oldData = null;
    private Type type = null;

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(AbstractPerk perk, PerkEffectHelper.Action action) {
        this.perk = perk;
        this.action = action;
    }

    public PktSyncPerkActivity(Type type) {
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
            ByteBufUtils.writeOptional(buffer, packet.action, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeEnumValue);
            ByteBufUtils.writeOptional(buffer, packet.perk,
                    ((byteBuf, perk) -> ByteBufUtils.writeResourceLocation(byteBuf, perk.getRegistryName())));
            ByteBufUtils.writeOptional(buffer, packet.newData, ByteBufUtils::writeNBTTag);
            ByteBufUtils.writeOptional(buffer, packet.oldData, ByteBufUtils::writeNBTTag);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncPerkActivity> decoder() {
        return buffer -> {
            PktSyncPerkActivity pkt = new PktSyncPerkActivity();

            pkt.action = ByteBufUtils.readOptional(buffer, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, PerkEffectHelper.Action.class));
            pkt.type = ByteBufUtils.readOptional(buffer, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, Type.class));
            ResourceLocation key = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
            if (key != null) {
                pkt.perk = PerkTree.PERK_TREE.getPerk(key);
            }
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

                    if (packet.type != null) {
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
                        return;
                    }
                    if (packet.perk != null) {
                        PerkEffectHelper.modifySource(player, LogicalSide.CLIENT, packet.perk, packet.action);
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
