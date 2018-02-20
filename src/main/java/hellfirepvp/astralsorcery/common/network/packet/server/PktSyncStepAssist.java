/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncStepAssist
 * Created by HellFirePvP
 * Date: 06.04.2017 / 23:44
 */
public class PktSyncStepAssist implements IMessage, IMessageHandler<PktSyncStepAssist, IMessage> {

    public float stepHeight;

    public PktSyncStepAssist() {}

    public PktSyncStepAssist(float stepHeight) {
        this.stepHeight = stepHeight - 0.4F; //FFS mojang
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stepHeight = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(this.stepHeight);
    }

    @Override
    public IMessage onMessage(PktSyncStepAssist message, MessageContext ctx) {
        apply(message.stepHeight);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void apply(float stepHeight) {
        if(Minecraft.getMinecraft().player == null) {
            AstralSorcery.proxy.scheduleClientside(() -> apply(stepHeight), 4);
            return;
        }
        Minecraft.getMinecraft().player.stepHeight = stepHeight;
    }

}
