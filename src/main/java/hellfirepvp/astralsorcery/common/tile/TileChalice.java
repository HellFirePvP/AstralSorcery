/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 21:58
 */
public class TileChalice extends TileEntityTick {

    private static final int TANK_SIZE = 24000;
    private SimpleSingleFluidCapabilityTank tank;

    public Vector3 rotationDegreeAxis = new Vector3();
    public Vector3 prevRotationDegreeAxis = new Vector3();

    private Vector3 rotationVecAxis1 = null, rotationVecAxis2 = null;

    private Vector3 rotationVec = null;

    public TileChalice() {
        tank = new SimpleSingleFluidCapabilityTank(TANK_SIZE, EnumFacing.DOWN);
    }

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            if(rotationVecAxis1 == null) {
                rotationVecAxis1 = Vector3.random().multiply(360);
            }
            if(rotationVecAxis2 == null) {
                rotationVecAxis2 = Vector3.random().multiply(360);
            }
            if(rotationVec == null) {
                rotationVec = Vector3.random().normalize().multiply(1.5F);
            }

            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationVec);

            playFluidEffect();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playFluidEffect() {
        FluidStack fs = getTank().getFluid();
        if(fs == null || fs.getFluid() == null) return;

        ResourceLocation res = fs.getFluid().getFlowing(fs);
        TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(res.toString());
        if(tas == null) tas = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

        EntityFXFloatingCube cube;
        if(rand.nextInt(2 * (DrawSize.values().length - getDrawSize().ordinal()) * 4) == 0) {
            Vector3 at = new Vector3(this).add(0.5, 1.4, 0.5);
            at.add( getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                    getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                    getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            cube = RenderingUtils.spawnFloatingBlockCubeParticle(at, tas);
            cube.setBlendMode(null).setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, at.toBlockPos());
            cube.setScale(0.08F * (getDrawSize().ordinal() + 1)).tumble().setMotion(
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1));
        }

        Vector3 perp = rotationVecAxis1.clone().perpendicular().normalize();
        perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 140D) / 140D)), rotationVecAxis1);
        perp.add(getPos()).add(0.5, 0.5, 0.5).addY(1);

        cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
        cube.setBlendMode(null).setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
        cube.setWorldLightCoord(Minecraft.getMinecraft().world, perp.toBlockPos());
        cube.setScale(rand.nextFloat() * 0.1F + 0.2F).tumble().setMotion(
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(perp);
        p.setColor(Color.WHITE).scale(0.1F + rand.nextFloat() * 0.05F).setMaxAge(15 + rand.nextInt(10));

        if(rand.nextInt(5) == 0) {
            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE).scale(0.1F + rand.nextFloat() * 0.1F).setMaxAge(20 + rand.nextInt(20));
            p.motion(rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
        }

        if(getDrawSize().ordinal() > 1) {
            perp = rotationVecAxis2.clone().perpendicular().normalize();
            perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 170D) / 170D)), rotationVecAxis2);
            perp.add(getPos()).add(0.5, 0.5, 0.5).addY(1);

            cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
            cube.setBlendMode(null).setTextureSubSizePercentage(1F / 16F).setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, perp.toBlockPos());
            cube.setScale(rand.nextFloat() * 0.1F + 0.2F).tumble().setMotion(
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE).scale(0.05F + rand.nextFloat() * 0.05F).setMaxAge(15 + rand.nextInt(5));

            if(rand.nextInt(5) == 0) {
                p = EffectHelper.genericFlareParticle(perp);
                p.setColor(Color.WHITE).scale(0.1F + rand.nextFloat() * 0.1F).setMaxAge(20 + rand.nextInt(20));
                p.motion(rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
            }
        }
    }

    public SimpleSingleFluidCapabilityTank getTank() {
        return tank;
    }

    @Override
    protected void onFirstTick() {}

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        return tank.getTankFluid();
    }

    public float getPercFilled() {
        return tank.getPercentageFilled();
    }

    public DrawSize getDrawSize() {
        float perc = getPercFilled();
        if(perc >= 0.75) {
            return DrawSize.FULL;
        }
        if(perc >= 0.5) {
            return DrawSize.BIG;
        }
        if(perc >= 0.25) {
            return DrawSize.MEDIUM;
        }
        return DrawSize.SMALL;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("tank", tank.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
        if(!tank.hasCapability(EnumFacing.DOWN)) {
            tank.accessibleSides.add(EnumFacing.DOWN);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tank.hasCapability(facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || !hasCapability(capability, facing)) return null;
        return (T) tank.getCapability(facing);
    }

    public static enum DrawSize {

        SMALL(1),
        MEDIUM(2),
        BIG(4),
        FULL(8);

        public final float partTexture;
        public final int mulSize;

        DrawSize(int mulSize) {
            this.partTexture = ((float) mulSize) / 16F;
            this.mulSize = mulSize;
        }
    }

}
