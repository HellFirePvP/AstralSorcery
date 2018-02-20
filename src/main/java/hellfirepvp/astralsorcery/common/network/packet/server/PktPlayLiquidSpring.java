/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFluidFountain;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayLiquidSpring
 * Created by HellFirePvP
 * Date: 02.11.2017 / 16:17
 */
public class PktPlayLiquidSpring implements IMessageHandler<PktPlayLiquidSpring, IMessage>, IMessage {

    private FluidStack stack;
    private Vector3 vec;

    public PktPlayLiquidSpring() {}

    public PktPlayLiquidSpring(FluidStack stack, Vector3 vec) {
        this.stack = stack;
        this.vec = vec;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.vec = ByteBufUtils.readVector(buf);
        this.stack = ByteBufUtils.readFluidStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVector(buf, vec);
        ByteBufUtils.writeFluidStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PktPlayLiquidSpring message, MessageContext ctx) {
        message.playEffect();
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void playEffect() {
        EntityFXFluidFountain.spawnAt(vec, stack);
    }

}
