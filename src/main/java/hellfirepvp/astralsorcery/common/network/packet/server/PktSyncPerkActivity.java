/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
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
    private boolean unlock = false;
    private CompoundNBT newData = null, oldData = null;
    private Type type = null;

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(AbstractPerk perk, boolean unlock) {
        this.perk = perk;
        this.unlock = unlock;
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
            buffer.writeBoolean(packet.unlock);
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

            pkt.unlock = buffer.readBoolean();
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
            public void handleClient(PktSyncPerkActivity packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player == null) {
                        return;
                    }

                    //TODO perks
                    //if (packet.type != null) {
                    //    LogCategory.PERKS.info(() -> "Received perk activity packet on clientside: " + packet.type);
                    //    switch (packet.type) {
                    //        case CLEARALL:
                    //            PerkEffectHelper.EVENT_INSTANCE.clearAllPerksClient(Minecraft.getInstance().player);
                    //            break;
                    //        case UNLOCKALL:
                    //            PerkEffectHelper.EVENT_INSTANCE.reapplyAllPerksClient(Minecraft.getInstance().player);
                    //            break;
                    //        case DATACHANGE:
                    //            PerkEffectHelper.EVENT_INSTANCE.notifyPerkDataChangeClient(Minecraft.getInstance().player, packet.perk, packet.oldData, packet.newData);
                    //            break;
                    //        default:
                    //            break;
                    //    }
                    //    return;
                    //}
                    //if (packet.perk != null) {
                    //    LogCategory.PERKS.info(() -> "Received perk modification packet on clientside: " + packet.perk.getRegistryName() + " " + (packet.unlock ? "Application" : "Removal"));
                    //    PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(Minecraft.getInstance().player, Dist.CLIENT, packet.perk, !packet.unlock);
                    //}
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
