/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.block.PrecisionSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:28
 */
public class TileWell extends TileReceiverBaseInventory {

    private static final Random rand = new Random();
    private static final int MAX_CAPACITY = 2000;

    private WellLiquefaction.LiquefactionEntry running = null;

    private PrecisionSingleFluidCapabilityTank tank;
    private double starlightBuffer = 0;

    public TileWell() {
        super(1, EnumFacing.UP);
        this.tank = new PrecisionSingleFluidCapabilityTank(MAX_CAPACITY, EnumFacing.DOWN);
        this.tank.setAllowInput(false);
    }

    @Override
    protected ItemHandlerTile createNewItemHandler() {
        return new CatalystItemHandler(this);
    }

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if(world.canSeeSky(getPos())) {
                double sbDayDistribution = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
                int yLevel = getPos().getY();
                float dstr;
                if(yLevel > 120) {
                    dstr = 1F;
                } else {
                    dstr = yLevel / 120F;
                }
                sbDayDistribution *= dstr;
                starlightBuffer += Math.max(0.0001, sbDayDistribution);
            }

            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(!stack.isEmpty()) {
                if(!world.isAirBlock(getPos().up())) {
                    breakCatalyst();
                } else {
                    running = WellLiquefaction.getLiquefactionEntry(stack);

                    if(running != null) {
                        double gain = Math.sqrt(starlightBuffer) * running.productionMultiplier;
                        if(gain > 0 && tank.getFluidAmount() <= MAX_CAPACITY) {
                            if (tank.getFluidAmount() <= MAX_CAPACITY) {
                                markForUpdate();
                            }
                            fillAndDiscardRest(running, gain);
                            if(rand.nextInt(2000) == 0) {
                                EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                            }
                        }
                        starlightBuffer = 0;
                        if(rand.nextInt(1 + (int) (1000 * running.shatterMultiplier)) == 0) {
                            breakCatalyst();
                            EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                        }
                    }
                }
                starlightBuffer = 0;
            } else {
                starlightBuffer = 0;
            }

            if((ticksExisted % 100 == 0) && getHeldFluid() != null && getFluidAmount() > 100) {
                int mb = Math.min(400, getFluidAmount());
                FluidStack fluidStack = new FluidStack(getHeldFluid(), mb);
                java.util.List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalicesWithSpaceFor(this, fluidStack);
                if(!out.isEmpty()) {
                    TileChalice target = out.get(rand.nextInt(out.size()));
                    LiquidStarlightChaliceHandler.doFluidTransfer(this, target, fluidStack.copy());
                    this.tank.drain(mb, true);
                    markForUpdate();
                }
            }
        } else {
            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(!stack.isEmpty()) {
                running = WellLiquefaction.getLiquefactionEntry(stack);

                if(running != null) {
                    Color color = Color.WHITE;
                    if(running.catalystColor != null) {
                        color = running.catalystColor;
                    }
                    doCatalystEffect(color);
                }
            }
            if(tank.getFluidAmount() > 0 && tank.getTankFluid() != null && tank.getTankFluid() instanceof FluidLiquidStarlight) {
                doStarlightEffect();
            }
        }
    }

    public void breakCatalyst() {
        getInventoryHandler().setStackInSlot(0, ItemStack.EMPTY);
        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.WELL_CATALYST_BREAK, getPos().getX(), getPos().getY(), getPos().getZ());
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPos(), 32));
        SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, getWorld(), getPos(), 1F, 1F);
    }

    @SideOnly(Side.CLIENT)
    private void doStarlightEffect() {
        if(rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.4, getPos().getZ() + 0.5);
            p.offset(0, getPercFilled() * 0.5, 0);
            p.offset(rand.nextFloat() * 0.35 * (rand.nextBoolean() ? 1 : -1), 0, rand.nextFloat() * 0.35 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.16F).gravity(0.006).setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        }
    }

    @SideOnly(Side.CLIENT)
    private void doCatalystEffect(Color color) {
        if(rand.nextInt(6) == 0) {
            Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
            if(rView == null) rView = Minecraft.getMinecraft().player;
            if(rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 1.3, getPos().getZ() + 0.5);
            p.offset(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1, rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.2F).gravity(-0.004).setAlphaMultiplier(1F).setColor(color);
        }
    }

    private void receiveStarlight(IWeakConstellation type, double amount) {
        this.starlightBuffer += amount;
    }

    private void fillAndDiscardRest(WellLiquefaction.LiquefactionEntry entry, double gain) {
        if(tank.getTankFluid() == null) {
            tank.setFluid(entry.producing);
        } else if(!entry.producing.equals(tank.getTankFluid())) {
            return;
        }
        tank.addAmount(gain);
    }

    @SideOnly(Side.CLIENT)
    public static void catalystBurst(PktParticleEvent event) {
        BlockPos at = event.getVec().toBlockPos();
        EffectHandler.getInstance().registerFX(new EntityFXCrystalBurst(rand.nextInt(), at.getX() + 0.5, at.getY() + 1.3, at.getZ() + 0.5, 1.5F));
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockwell.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverWell(at);
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

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("tank", tank.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.tank = PrecisionSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
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

    public static class CatalystItemHandler extends ItemHandlerTileFiltered {

        public CatalystItemHandler(TileReceiverBaseInventory inv) {
            super(inv);
        }

        @Override
        public int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        public boolean canInsertItem(int slot, @Nonnull ItemStack toAdd, @Nonnull ItemStack existing) {
            if(toAdd.isEmpty()) return true;
            return WellLiquefaction.getLiquefactionEntry(toAdd) != null && existing.isEmpty();
        }

    }

    public static class TransmissionReceiverWell extends SimpleTransmissionReceiver {

        public TransmissionReceiverWell(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if(isChunkLoaded) {
                TileWell tw = MiscUtils.getTileAt(world, getPos(), TileWell.class, false);
                if(tw != null) {
                    tw.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new WellReceiverProvider();
        }

    }

    public static class WellReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverWell provideEmptyNode() {
            return new TransmissionReceiverWell(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverWell";
        }

    }

}
