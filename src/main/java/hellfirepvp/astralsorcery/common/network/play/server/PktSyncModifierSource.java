/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
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


    private ModifierSource source = null;
    private PerkEffectHelper.Action action = null;

    public PktSyncModifierSource() {}

    public PktSyncModifierSource(ModifierSource perk, PerkEffectHelper.Action action) {
        this.source = perk;
        this.action = action;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncModifierSource> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.action, ByteBufUtils::writeEnumValue);
            ResourceLocation providerName = packet.source.getProviderName();
            ByteBufUtils.writeResourceLocation(buffer, providerName);
            ModifierSourceProvider provider = ModifierManager.getProvider(providerName);
            if (provider == null) {
                throw new IllegalArgumentException("Unknown provider: " + providerName);
            }
            provider.serialize(packet.source, buffer);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncModifierSource> decoder() {
        return buffer -> {
            PktSyncModifierSource pkt = new PktSyncModifierSource();

            pkt.action = ByteBufUtils.readOptional(buffer, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, PerkEffectHelper.Action.class));
            ResourceLocation providerName = ByteBufUtils.readResourceLocation(buffer);
            ModifierSourceProvider provider = ModifierManager.getProvider(providerName);
            if (provider == null) {
                throw new IllegalArgumentException("Unknown provider: " + providerName);
            }
            pkt.source = provider.deserialize(buffer);
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
                    PerkEffectHelper.modifySource(player, LogicalSide.CLIENT, packet.source, packet.action);
                });
            }

            @Override
            public void handle(PktSyncModifierSource packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
