package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
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
    private int guiId;

    public PktCraftingTableFix() {}

    public PktCraftingTableFix(BlockPos at, int guiId) {
        this.at = at;
        this.guiId = guiId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        at = BlockPos.fromLong(buf.readLong());
        guiId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(at.toLong());
        buf.writeInt(guiId);
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

    public static void sendOpenCraftingTable(EntityPlayer player, BlockPos at) {
        EntityPlayerMP mp = (EntityPlayerMP) player;
        BlockWorkbench.InterfaceCraftingTable containerInterface = new BlockWorkbench.InterfaceCraftingTable(mp.world, at);
        mp.getNextWindowId();
        int guiId = mp.currentWindowId;
        PacketChannel.CHANNEL.sendTo(new PktCraftingTableFix(at, guiId), mp);
        mp.openContainer = containerInterface.createContainer(player.inventory, player);
        mp.openContainer.windowId = guiId;
        mp.openContainer.addListener(mp);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(mp, mp.openContainer));
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
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            GuiCrafting gui = new GuiCrafting(player.inventory, player.world, message.at);
            Minecraft.getMinecraft().displayGuiScreen(gui);
            player.openContainer.windowId = message.guiId;
        }

    }

}
