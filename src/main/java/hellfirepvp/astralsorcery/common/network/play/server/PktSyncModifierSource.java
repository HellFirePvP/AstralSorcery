/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncModifierSource
 * Created by HellFirePvP
 * Date: 01.04.2020 / 21:45
 */
public class PktSyncModifierSource extends ASPacket<PktSyncModifierSource> {

    private ModifierSource source, newSource;
    private ActionType actionType;

    public PktSyncModifierSource() {}

    private PktSyncModifierSource(ModifierSource source, ModifierSource newSource, ActionType actionType) {
        this.source = source;
        this.newSource = newSource;
        this.actionType = actionType;
    }

    public static PktSyncModifierSource add(ModifierSource source) {
        return new PktSyncModifierSource(source, null, ActionType.ADD);
    }

    public static PktSyncModifierSource remove(ModifierSource source) {
        return new PktSyncModifierSource(source, null, ActionType.REMOVE);
    }

    public static PktSyncModifierSource update(ModifierSource existing, ModifierSource newSource) {
        return new PktSyncModifierSource(existing, newSource, ActionType.UPDATE);
    }

    @Nonnull
    @Override
    public Encoder<PktSyncModifierSource> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.source, ByteBufUtils::writeModifierSource);
            ByteBufUtils.writeOptional(buffer, packet.newSource, ByteBufUtils::writeModifierSource);
            ByteBufUtils.writeEnumValue(buffer, packet.actionType);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncModifierSource> decoder() {
        return buffer -> {
            PktSyncModifierSource pkt = new PktSyncModifierSource();

            pkt.source = ByteBufUtils.readOptional(buffer, ByteBufUtils::readModifierSource);
            pkt.newSource = ByteBufUtils.readOptional(buffer, ByteBufUtils::readModifierSource);
            pkt.actionType = ByteBufUtils.readEnumValue(buffer, PktSyncModifierSource.ActionType.class);
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSyncModifierSource> handler() {
        return new Handler<PktSyncModifierSource>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncModifierSource packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player == null) {
                        return;
                    }
                    switch (packet.actionType) {
                        case UPDATE:
                            PerkEffectHelper.updateSource(player, LogicalSide.CLIENT, packet.source, packet.newSource);
                            break;
                        case ADD:
                            PerkEffectHelper.modifySource(player, LogicalSide.CLIENT, packet.source, PerkEffectHelper.Action.ADD);
                            break;
                        case REMOVE:
                            PerkEffectHelper.modifySource(player, LogicalSide.CLIENT, packet.source, PerkEffectHelper.Action.REMOVE);
                            break;
                    }
                });
            }

            @Override
            public void handle(PktSyncModifierSource packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }

    private static enum ActionType {

        ADD,
        REMOVE,
        UPDATE;

    }
}
