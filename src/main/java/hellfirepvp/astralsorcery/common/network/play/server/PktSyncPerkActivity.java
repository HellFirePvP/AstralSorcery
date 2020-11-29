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
import hellfirepvp.astralsorcery.common.perk.PerkTree;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncPerkActivity
 * Created by HellFirePvP
 * Date: 02.06.2019 / 08:35
 */
public class PktSyncPerkActivity extends ASPacket<PktSyncPerkActivity> {

    private Type type = null;
    private ResourceLocation perkKey = null;
    private CompoundNBT newData = null, oldData = null;
    private List<ResourceLocation> perkKeys = new ArrayList<>();

    public PktSyncPerkActivity() {}

    public PktSyncPerkActivity(Type type) {
        if (type == Type.DATACHANGE) {
            throw new IllegalArgumentException("Passed Datachange-Type without supplying data!");
        }
        this.type = type;
    }

    public PktSyncPerkActivity(AbstractPerk perk, CompoundNBT oldData, CompoundNBT newData) {
        this.type = Type.DATACHANGE;
        this.perkKey = perk.getRegistryName();
        this.oldData = oldData;
        this.newData = newData;
    }

    public PktSyncPerkActivity(Collection<ResourceLocation> removals) {
        this.type = Type.REMOVE_LISTED;
        this.perkKeys = new ArrayList<>(removals);
    }

    @Nonnull
    @Override
    public Encoder<PktSyncPerkActivity> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeEnumValue);

            ByteBufUtils.writeOptional(buffer, packet.perkKey, ByteBufUtils::writeResourceLocation);
            ByteBufUtils.writeOptional(buffer, packet.newData, ByteBufUtils::writeNBTTag);
            ByteBufUtils.writeOptional(buffer, packet.oldData, ByteBufUtils::writeNBTTag);

            ByteBufUtils.writeCollection(buffer, packet.perkKeys, ByteBufUtils::writeResourceLocation);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncPerkActivity> decoder() {
        return buffer -> {
            PktSyncPerkActivity pkt = new PktSyncPerkActivity();

            pkt.type = ByteBufUtils.readOptional(buffer, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, Type.class));

            pkt.perkKey = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
            pkt.newData = ByteBufUtils.readOptional(buffer, ByteBufUtils::readNBTTag);
            pkt.oldData = ByteBufUtils.readOptional(buffer, ByteBufUtils::readNBTTag);

            pkt.perkKeys = ByteBufUtils.readList(buffer, ByteBufUtils::readResourceLocation);
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
                        case REMOVE_LISTED:
                            List<AbstractPerk> perks = packet.perkKeys.stream()
                                    .map(key -> PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, key))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(Collectors.toList());
                            PerkEffectHelper.modifySources(player, LogicalSide.CLIENT, perks, PerkEffectHelper.Action.REMOVE);
                            break;
                        case UNLOCKALL:
                            PerkEffectHelper.clientRefreshAllPerks();
                            break;
                        case DATACHANGE:
                            PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, packet.perkKey).ifPresent(perk -> {
                                PerkEffectHelper.clientChangePerkData(perk, packet.oldData, packet.newData);
                            });
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
        REMOVE_LISTED,
        UNLOCKALL,
        DATACHANGE

    }
}
