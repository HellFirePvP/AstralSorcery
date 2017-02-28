/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:28
 */
public class TileWell extends TileReceiverBaseInventory implements IFluidHandler, IFluidTankProperties {

    private static final Random rand = new Random();
    private static final int MAX_CAPACITY = 2000;

    private WellLiquefaction.LiquefactionEntry running = null;

    private Fluid heldFluid = null;
    private double mbFluidAmount = 0;
    private double starlightBuffer = 0;

    public TileWell() {
        super(1, EnumFacing.UP);
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
                if(yLevel > 140) {
                    dstr = 1F;
                } else {
                    dstr = yLevel / 140F;
                }
                sbDayDistribution *= dstr;
                starlightBuffer += Math.max(0.0001, sbDayDistribution);
            }

            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(stack != null) {
                if(!world.isAirBlock(getPos().up())) {
                    breakCatalyst();
                } else {
                    running = WellLiquefaction.getLiquefactionEntry(stack);

                    if(running != null && stack.getItem() != null) {
                        double gain = Math.sqrt(starlightBuffer) * running.productionMultiplier;
                        if(gain > 0 && mbFluidAmount <= MAX_CAPACITY) {
                            if (mbFluidAmount <= MAX_CAPACITY) {
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
        } else {
            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(stack != null && stack.getItem() != null) {
                running = WellLiquefaction.getLiquefactionEntry(stack);

                if(running != null) {
                    Color color = Color.WHITE;
                    if(running.catalystColor != null) {
                        color = running.catalystColor;
                    }
                    doCatalystEffect(color);
                }
            }
            if(mbFluidAmount > 0 && heldFluid != null && heldFluid instanceof FluidLiquidStarlight) {
                doStarlightEffect();
            }
        }
    }

    public void breakCatalyst() {
        getInventoryHandler().setStackInSlot(0, null);
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
        if(heldFluid == null) {
            heldFluid = entry.producing;
        } else if(!entry.producing.equals(heldFluid)) {
            return;
        }
        mbFluidAmount = Math.min(MAX_CAPACITY, mbFluidAmount + gain);
    }

    @SideOnly(Side.CLIENT)
    public static void catalystBurst(PktParticleEvent event) {
        BlockPos at = event.getVec().toBlockPos();
        EffectHandler.getInstance().registerFX(new EntityFXCrystalBurst(rand.nextInt(), at.getX() + 0.5, at.getY() + 1.3, at.getZ() + 0.5, 1.5F));
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockWell.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverWell(at);
    }

    public double getFluidAmount() {
        return mbFluidAmount;
    }

    @Nullable
    public Fluid getHeldFluid() {
        return heldFluid;
    }

    public float getPercFilled() {
        return (float) (mbFluidAmount / MAX_CAPACITY);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setDouble("mbAmount", mbFluidAmount);
        if (heldFluid != null) {
            compound.setString("rFluid", FluidRegistry.getFluidName(heldFluid));
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.mbFluidAmount = compound.getDouble("mbAmount");
        if(compound.hasKey("rFluid")) {
            this.heldFluid = FluidRegistry.getFluid(compound.getString("rFluid"));
        } else {
            this.heldFluid = null;
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { this };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (heldFluid == null) return null;
        int drainPotential = MathHelper.floor(Math.min(1000D, mbFluidAmount));
        if(doDrain) {
            mbFluidAmount -= drainPotential;
        }
        return new FluidStack(heldFluid, drainPotential);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (heldFluid == null) return null;

        int drainPotential = MathHelper.floor(MathHelper.clamp(mbFluidAmount, 0, Math.min(1000D, maxDrain)));
        if(doDrain) {
            mbFluidAmount -= drainPotential;
        }
        return new FluidStack(heldFluid, drainPotential);
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        if(heldFluid == null) return null;
        return new FluidStack(heldFluid, MathHelper.floor(mbFluidAmount));
    }

    @Override
    public int getCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canFill() {
        return false;
    }

    @Override
    public boolean canDrain() {
        return true;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return false;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return heldFluid != null && fluidStack.getFluid() != null && fluidStack.getFluid().equals(heldFluid);
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
        public boolean canInsertItem(int slot, ItemStack toAdd, @Nullable ItemStack existing) {
            if(toAdd == null) return true;
            return WellLiquefaction.getLiquefactionEntry(toAdd) != null && existing == null;
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
