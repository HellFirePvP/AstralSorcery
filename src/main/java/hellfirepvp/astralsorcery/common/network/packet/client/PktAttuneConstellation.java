/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttuneConstellation
 * Created by HellFirePvP
 * Date: 19.12.2016 / 12:42
 */
public class PktAttuneConstellation implements IMessage, IMessageHandler<PktAttuneConstellation, IMessage> {

    public IMajorConstellation attunement = null;
    private int worldId = -1;
    private BlockPos at = BlockPos.ORIGIN;


    public PktAttuneConstellation() {}

    public PktAttuneConstellation(IMajorConstellation attunement, int worldId, BlockPos pos) {
        this.attunement = attunement;
        this.worldId = worldId;
        this.at = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.attunement = ConstellationRegistry.getMajorConstellationByName(ByteBufUtils.readString(buf));
        this.worldId = buf.readInt();
        this.at = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, attunement.getUnlocalizedName());
        buf.writeInt(worldId);
        ByteBufUtils.writePos(buf, at);
    }

    @Override
    public IMessage onMessage(PktAttuneConstellation message, MessageContext ctx) {
        IMajorConstellation cst = message.attunement;
        if(cst != null) {
            World w = DimensionManager.getWorld(message.worldId);
            TileAttunementAltar ta = MiscUtils.getTileAt(w, message.at, TileAttunementAltar.class, false);
            if(ta != null) {
                ta.askForAttunement(ctx.getServerHandler().player, cst);
            }
            /*EntityPlayer req = ctx.getServerHandler().playerEntity;
            PlayerProgress prog = ResearchManager.getProgress(req, Side.SERVER);
            if(prog != null && prog.getAttunedConstellation() == null) {
                ResearchManager.setAttunedConstellation(req, cst);
            }*/
        }
        return null;
    }
}
