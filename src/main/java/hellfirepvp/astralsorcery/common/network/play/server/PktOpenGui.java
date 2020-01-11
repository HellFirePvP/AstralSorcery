/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.GuiType;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktOpenGui
 * Created by HellFirePvP
 * Date: 03.08.2019 / 13:12
 */
public class PktOpenGui extends ASPacket<PktOpenGui> {

    private GuiType type;
    private CompoundNBT data;

    public PktOpenGui() {}

    public PktOpenGui(GuiType type, CompoundNBT data) {
        this.type = type;
        this.data = data;
    }

    @Nonnull
    @Override
    public Encoder<PktOpenGui> encoder() {
        return (pkt, buffer) -> {
            ByteBufUtils.writeEnumValue(buffer, pkt.type);
            ByteBufUtils.writeNBTTag(buffer, pkt.data);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktOpenGui> decoder() {
        return buffer -> {
            return new PktOpenGui(ByteBufUtils.readEnumValue(buffer, GuiType.class), ByteBufUtils.readNBTTag(buffer));
        };
    }

    @Nonnull
    @Override
    public Handler<PktOpenGui> handler() {
        return new Handler<PktOpenGui>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktOpenGui packet, NetworkEvent.Context context) {
                if (Minecraft.getInstance().player != null) {
                    context.enqueueWork(() -> AstralSorcery.getProxy().openGuiClient(packet.type, packet.data));
                }
            }

            @Override
            public void handle(PktOpenGui packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
