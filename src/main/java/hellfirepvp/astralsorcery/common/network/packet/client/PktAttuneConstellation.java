package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttuneConstellation
 * Created by HellFirePvP
 * Date: 19.12.2016 / 12:42
 */
public class PktAttuneConstellation implements IMessage, IMessageHandler<PktAttuneConstellation, IMessage> {

    public IMajorConstellation attunement = null;

    public PktAttuneConstellation() {}

    public PktAttuneConstellation(IMajorConstellation attunement) {
        this.attunement = attunement;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.attunement = ConstellationRegistry.getMajorConstellationByName(ByteBufUtils.readString(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, attunement.getUnlocalizedName());
    }

    @Override
    public IMessage onMessage(PktAttuneConstellation message, MessageContext ctx) {
        IMajorConstellation cst = message.attunement;
        if(cst != null) {
            EntityPlayer req = ctx.getServerHandler().playerEntity;
            PlayerProgress prog = ResearchManager.getProgress(req, Side.SERVER);
            if(prog != null && prog.getAttunedConstellation() == null) {
                ResearchManager.setAttunedConstellation(req, cst);
            }
        }
        return null;
    }
}
