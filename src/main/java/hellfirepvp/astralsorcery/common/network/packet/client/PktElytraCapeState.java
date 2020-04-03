/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectVicio;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key.KeyMantleFlight;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktElytraCapeState
 * Created by HellFirePvP
 * Date: 15.10.2017 / 21:33
 */
public class PktElytraCapeState implements IMessageHandler<PktElytraCapeState, IMessage>, IMessage {

    private byte type = 0;

    public PktElytraCapeState() {}

    public static PktElytraCapeState resetFallDistance() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 0;
        return st;
    }

    public static PktElytraCapeState setFlying() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 1;
        return st;
    }

    public static PktElytraCapeState resetFlying() {
        PktElytraCapeState st = new PktElytraCapeState();
        st.type = 2;
        return st;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.type);
    }

    @Override
    public IMessage onMessage(PktElytraCapeState message, MessageContext ctx) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            EntityPlayer pl = ctx.getServerHandler().player;

            CapeEffectVicio vic = ItemCape.getCapeEffect(pl, Constellations.vicio);
            if (vic == null) {
                return;
            }

            boolean hasFlightPerk = ResearchManager.getProgress(pl, Side.SERVER).hasPerkEffect(p -> p instanceof KeyMantleFlight);

            switch (message.type) {
                case 0: {
                    if (pl.isElytraFlying()) {
                        pl.fallDistance = 0F;
                    }
                    break;
                }
                case 1: {
                    if (!hasFlightPerk) {
                        pl.setFlag(7, true);
                    }
                    break;
                }
                case 2: {
                    pl.setFlag(7, false);
                    break;
                }
                default:
                    break;
            }
        });
        return null;
    }
}
