/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiMapDrawing;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktBurnParchment
 * Created by HellFirePvP
 * Date: 30.04.2017 / 22:20
 */
public class PktBurnParchment implements IMessage, IMessageHandler<PktBurnParchment, PktBurnParchment> {

    public int dimId;
    public BlockPos tablePos;

    public PktBurnParchment() {}

    public PktBurnParchment(int dimid, BlockPos tablePos) {
        this.tablePos = tablePos;
        this.dimId = dimid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tablePos = ByteBufUtils.readPos(buf);
        this.dimId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writePos(buf, this.tablePos);
        buf.writeInt(dimId);
    }

    @Override
    public PktBurnParchment onMessage(PktBurnParchment message, MessageContext ctx) {
        if(ctx.side == Side.SERVER) {
            World world = DimensionManager.getWorld(message.dimId);
            if(world != null) {
                TileMapDrawingTable tmt = MiscUtils.getTileAt(world, message.tablePos, TileMapDrawingTable.class, false);
                if(tmt != null) {
                    if(tmt.burnParchment()) {
                        return new PktBurnParchment(-1, BlockPos.ORIGIN);
                    }
                }
            }
            return null;
        } else {
            closeTable();
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private void closeTable() {
        if(Minecraft.getMinecraft().currentScreen != null &&
                Minecraft.getMinecraft().currentScreen instanceof GuiMapDrawing) {
            AstralSorcery.proxy.scheduleClientside(() -> Minecraft.getMinecraft().displayGuiScreen(null));
        }
    }

}
