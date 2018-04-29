/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktDisplaySextantTarget
 * Created by HellFirePvP
 * Date: 29.04.2018 / 10:10
 */
public class PktDisplaySextantTarget implements IMessage, IMessageHandler<PktDisplaySextantTarget, IMessage> {

    private SextantFinder.TargetObject target;
    private BlockPos targetPos;

    public PktDisplaySextantTarget() {}

    public PktDisplaySextantTarget(SextantFinder.TargetObject target, BlockPos targetPos) {
        this.target = target;
        this.targetPos = targetPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target = SextantFinder.getByName(ByteBufUtils.readString(buf));
        this.targetPos = ByteBufUtils.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.target.getRegistryName());
        ByteBufUtils.writePos(buf, this.targetPos);
    }

    @Override
    public IMessage onMessage(PktDisplaySextantTarget message, MessageContext ctx) {
        handleClient(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handleClient(PktDisplaySextantTarget pkt) {
        if(pkt.target != null && Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EffectHandler.getInstance().requestSextantTargetAt(Minecraft.getMinecraft().world, pkt.targetPos, pkt.target);
            });
        }
    }
}
