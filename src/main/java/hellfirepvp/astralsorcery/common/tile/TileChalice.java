/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.block.PrecisionSingleFluidCapabilityTank;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 21:58
 */
public class TileChalice extends TileEntityTick {

    private static final int TANK_SIZE = 32000;
    private SimpleSingleFluidCapabilityTank tank;

    public Vector3 rotationDegreeAxis = new Vector3();
    public Vector3 prevRotationDegreeAxis = new Vector3();

    private Vector3 rotationVecAxis = null;

    private Vector3 rotationVec = null;

    public TileChalice() {
        tank = new SimpleSingleFluidCapabilityTank(TANK_SIZE, EnumFacing.DOWN);
    }

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            if(rotationVecAxis == null) {
                rotationVecAxis = Vector3.positiveYRandom().multiply(360);
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

        //Vector3 perp = rotationVecAxis.clone().perpendicular().normalize();
        //perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 100D) / 100D)), rotationVecAxis);
        //perp.add(getPos()).add(0.5, 0.5, 0.5).addY(1);
        //ResourceLocation res = fs.getFluid().getFlowing(fs);
        //TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(res.toString());
        //RenderingUtils.spawnBlockBreakParticle(perp, tas);
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
        if(perc >= 0.99) {
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
