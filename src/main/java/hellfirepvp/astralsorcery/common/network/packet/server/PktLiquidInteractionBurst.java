/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLiquidInteractionBurst
 * Created by HellFirePvP
 * Date: 28.10.2017 / 23:37
 */
public class PktLiquidInteractionBurst implements IMessageHandler<PktLiquidInteractionBurst, IMessage>, IMessage {

    private static Random rand = new Random();

    private FluidStack comp1, comp2;
    private Vector3 pos;

    public PktLiquidInteractionBurst() {}

    public PktLiquidInteractionBurst(FluidStack comp1, FluidStack comp2, Vector3 pos) {
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.comp1 = ByteBufUtils.readFluidStack(buf);
        this.comp2 = ByteBufUtils.readFluidStack(buf);
        this.pos = ByteBufUtils.readVector(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeFluidStack(buf, comp1);
        ByteBufUtils.writeFluidStack(buf, comp2);
        ByteBufUtils.writeVector(buf, pos);
    }

    @Override
    public IMessage onMessage(PktLiquidInteractionBurst message, MessageContext ctx) {
        playClientEffect(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void playClientEffect(PktLiquidInteractionBurst message) {
        if(Minecraft.getMinecraft().world == null) return;

        TextureAtlasSprite tas1 = RenderingUtils.tryGetFlowingTextureOfFluidStack(message.comp1);

        for (int i = 0; i < 11 + rand.nextInt(3); i++) {
            EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(message.pos, tas1);
            cube.setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, message.pos.toBlockPos());
            cube.setScale(0.35F).tumble().setMotion(
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.017F * (rand.nextBoolean() ? 1 : -1));
        }

        TextureAtlasSprite tas2 = RenderingUtils.tryGetFlowingTextureOfFluidStack(message.comp2);

        for (int i = 0; i < 11 + rand.nextInt(3); i++) {
            EntityFXFloatingCube cube = RenderingUtils.spawnFloatingBlockCubeParticle(message.pos, tas2);
            cube.setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, message.pos.toBlockPos());
            cube.setScale(0.35F).tumble().setMotion(
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.027F * (rand.nextBoolean() ? 1 : -1));
        }
    }

}
