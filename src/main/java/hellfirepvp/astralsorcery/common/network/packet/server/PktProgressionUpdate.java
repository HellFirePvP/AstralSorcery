package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalPages;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktProgressionUpdate
 * Created by HellFirePvP
 * Date: 24.10.2016 / 23:54
 */
public class PktProgressionUpdate implements IMessage, IMessageHandler<PktProgressionUpdate, IMessage> {

    public int tier = -1;
    public boolean isProg = false;
    public boolean isPresent =false;

    public PktProgressionUpdate() {
        isPresent = false;
    }

    public PktProgressionUpdate(ResearchProgression prog) {
        this.tier = prog.getProgressId();
        this.isPresent = true;
    }

    public PktProgressionUpdate(ProgressionTier tier) {
        this.isProg = true;
        this.tier = tier.ordinal();
        this.isPresent = true;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tier = buf.readInt();
        this.isProg = buf.readBoolean();
        this.isPresent = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tier);
        buf.writeBoolean(isProg);
        buf.writeBoolean(isPresent);
    }

    @Override
    public IMessage onMessage(PktProgressionUpdate message, MessageContext ctx) {
        if(message.isPresent) {
            if(message.isProg) {
                addProgressChatMessage(message.tier);
            } else {
                addResearchChatMessage(message.tier);
            }
        }
        closeAndRefreshJournal();
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void closeAndRefreshJournal() {
        GuiScreen open = Minecraft.getMinecraft().currentScreen;
        if(open != null) {
            if(open instanceof GuiScreenJournal) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }

        GuiJournalProgression.resetJournal();
    }

    @SideOnly(Side.CLIENT)
    private void addResearchChatMessage(int resId) {
        ResearchProgression prog = ResearchProgression.getById(resId);
        String tr = I18n.translateToLocal(prog.getUnlocalizedName());
        String out = String.format(I18n.translateToLocal("progress.gain.research.chat"), tr);
        out = TextFormatting.AQUA + out;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(out));
    }

    @SideOnly(Side.CLIENT)
    private void addProgressChatMessage(int progId) {
        String out = TextFormatting.BLUE + I18n.translateToLocal("progress.gain.progress.chat");
        Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(out));
    }

}
