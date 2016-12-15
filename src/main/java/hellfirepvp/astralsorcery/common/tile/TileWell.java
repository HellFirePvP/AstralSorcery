package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.base.ItemWellCatalyst;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.UUID;

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

    private double mbStarlightAmount = 0;
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
                starlightBuffer += Math.max(0.0001, ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world));
            }

            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(stack != null) {
                if(!world.isAirBlock(getPos().up())) {
                    breakCatalyst();
                } else {
                    if(stack.getItem() != null && stack.getItem() instanceof ItemWellCatalyst) {
                        ItemWellCatalyst catalyst = (ItemWellCatalyst) stack.getItem();
                        if(catalyst.isCatalyst(stack)) {
                            double gain = Math.sqrt(starlightBuffer) * catalyst.collectionMultiplier(stack);
                            //double gain = 200;
                            if(gain > 0 && mbStarlightAmount <= MAX_CAPACITY) {
                                if (mbStarlightAmount <= MAX_CAPACITY) {
                                    markForUpdate();
                                }
                                fillAndDiscardRest(gain);
                            }
                            starlightBuffer = 0;
                            if(rand.nextInt(1 + (int) (1000 * catalyst.getShatterChanceMultiplier(stack))) == 0) {
                                breakCatalyst();
                            }
                        }
                    }
                }
            } else {
                starlightBuffer = 0;
            }
        } else {
            ItemStack stack = getInventoryHandler().getStackInSlot(0);
            if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemWellCatalyst) {
                Color color = ((ItemWellCatalyst) stack.getItem()).getCatalystColor(stack);
                doCatalystEffect(color);
            }
            if(mbStarlightAmount > 0) {
                doStarlightEffect();
            }
        }
    }

    public void breakCatalyst() {
        getInventoryHandler().setStackInSlot(0, null);
        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.WELL_CATALYST_BREAK, getPos().getX(), getPos().getY(), getPos().getZ());
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPos(), 32));
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

    private void receiveStarlight(IMajorConstellation type, double amount) {
        this.starlightBuffer += amount;
    }

    private void fillAndDiscardRest(double gain) {
        mbStarlightAmount = Math.min(MAX_CAPACITY, mbStarlightAmount + gain);
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
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverWell(at);
    }

    public double getLiquidStarlightAmount() {
        return mbStarlightAmount;
    }

    public float getPercFilled() {
        return (float) (mbStarlightAmount / MAX_CAPACITY);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setDouble("mbAmount", mbStarlightAmount);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.mbStarlightAmount = compound.getDouble("mbAmount");
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
        if(mbStarlightAmount >= 1000) {
            if(doDrain) {
                mbStarlightAmount -= 1000;
            }
            return new FluidStack(BlocksAS.fluidLiquidStarlight, 1000);
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if(mbStarlightAmount >= 1000 && maxDrain >= 1000) {
            if(doDrain) {
                mbStarlightAmount -= 1000;
            }
            return new FluidStack(BlocksAS.fluidLiquidStarlight, 1000);
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return new FluidStack(BlocksAS.fluidLiquidStarlight, MathHelper.floor(mbStarlightAmount));
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
        return fluidStack.getFluid() != null && fluidStack.getFluid() instanceof FluidLiquidStarlight;
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
            Item i = toAdd.getItem();
            return i instanceof ItemWellCatalyst && ((ItemWellCatalyst) i).isCatalyst(toAdd);
        }

    }

    public static class TransmissionReceiverWell extends SimpleTransmissionReceiver {

        public TransmissionReceiverWell(@Nonnull BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IMajorConstellation type, double amount) {
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
