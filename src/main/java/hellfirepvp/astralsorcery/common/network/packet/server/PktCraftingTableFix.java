/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktCraftingTableFix
 * Created by HellFirePvP
 * Date: 11.08.2016 / 09:47
 */
public class PktCraftingTableFix implements IMessage, IMessageHandler<PktCraftingTableFix, IMessage> {

    private BlockPos at;

    public PktCraftingTableFix() {}

    public PktCraftingTableFix(BlockPos at) {
        this.at = at;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        at = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(at.toLong());
    }

    @Override
    public IMessage onMessage(PktCraftingTableFix message, MessageContext ctx) {
        if(ctx.side == Side.CLIENT) {
            openProperCraftingTableGui(message);
        }
        return null;
    }

    //A crafting table that knows its position. useful.
    @SideOnly(Side.CLIENT)
    private void openProperCraftingTableGui(PktCraftingTableFix message) {
        AstralSorcery.proxy.scheduleClientside(new TaskOpenProperCraftingTable(message));
    }

    public BlockPos getPos() {
        return at;
    }

    @SideOnly(Side.CLIENT)
    public static class TaskOpenProperCraftingTable implements Runnable {

        private final PktCraftingTableFix message;

        public TaskOpenProperCraftingTable(PktCraftingTableFix message) {
            this.message = message;
        }

        @Override
        public void run() {
            ShapedLightProximityRecipe.clientWorkbenchPosition = message.at;
        }

    }

}
