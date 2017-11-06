/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.auxiliary.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.block.BlockBore;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.ItemBoreUpgrade;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.VerticalConeBlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileBore
 * Created by HellFirePvP
 * Date: 03.11.2017 / 14:07
 */
public class TileBore extends TileInventoryBase implements IMultiblockDependantTile, ILiquidStarlightPowered {

    private static int SEGMENT_STARTUP = 60,
                        SEGMENT_PREPARATION = 200;

    private SimpleSingleFluidCapabilityTank tank;
    private boolean hasMultiblock = false;
    private int operationTicks = 0;
    private int mbStarlight = 0;

    private int productionTimeout = 0;

    private VerticalConeBlockDiscoverer coneBlockDiscoverer;
    private boolean diggingSuccessful = false;
    private float digPercentage = 0;
    private List<BlockPos> digPosResult = null;

    private Object spritePlane = null;

    public TileBore() {
        super(1, EnumFacing.UP);
        tank = new SimpleSingleFluidCapabilityTank(1000, EnumFacing.UP);
        tank.setAllowInput(false);
    }

    @Override
    protected ItemHandlerTile createNewItemHandler() {
        return new TileInventoryBase.ItemHandlerTileFiltered(this) {
            @Override
            public boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
                return false;
            }
        };
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 31) == 0) {
            updateMultiblockState();
        }

        if(!world.isRemote) {
            if(coneBlockDiscoverer == null) {
                coneBlockDiscoverer = new VerticalConeBlockDiscoverer(this.getPos().down(3));
            }

            if(mbStarlight <= 12000 && getCurrentBoreType() != BoreType.NONE) {
                TileChalice tc = MiscUtils.getTileAt(world, getPos().up(), TileChalice.class, false);
                if(tc != null) {
                    LiquidStarlightChaliceHandler.requestLiquidStarlightAndTransferTo(this, tc, ticksExisted, 400);
                }
            }
            if(!consumeLiquid()) {
                if(this.operationTicks > 0) {
                    operationTicks--;
                    markForUpdate();
                }
                return;
            }
            if(getCurrentBoreType() == BoreType.NONE) {
                if(operationTicks > 0) {
                    markForUpdate();
                }
                this.operationTicks = 0;
                return;
            }
            handleSetupProgressTick();
            markForUpdate();
            if(this.operationTicks >= SEGMENT_PREPARATION) {
                if(!diggingSuccessful) {
                    if((ticksExisted % 8) == 0) {
                        attemptDig();
                    }
                } else {
                    if((ticksExisted % 32) == 0) {
                        checkDigState();
                    }
                    if(this.diggingSuccessful) {
                        if(productionTimeout > 0) {
                            productionTimeout--;
                        }
                        if(productionTimeout <= 0) {
                            productionTimeout = rand.nextInt(20) + 40;
                            BoreType type = getCurrentBoreType();
                            switch (type) {
                                case LIQUID:
                                    Chunk ch = world.getChunkFromBlockCoords(getPos());
                                    FluidRarityRegistry.ChunkFluidEntry entry = FluidRarityRegistry.getChunkEntry(ch);
                                    if(entry != null) {
                                        int mbDrain = rand.nextInt(100) + 100;
                                        int actMbDrain = Math.min(entry.getMbRemaining(), mbDrain);
                                        FluidStack drained;
                                        if(entry.isValid() && actMbDrain > 0) {
                                            drained = entry.tryDrain(actMbDrain, false);
                                            if(drained == null || drained.getFluid() == null) {
                                                drained = new FluidStack(FluidRegistry.WATER, mbDrain);
                                            }
                                            List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalices(this, drained);
                                            if(!out.isEmpty()) {
                                                TileChalice target = out.get(rand.nextInt(out.size()));
                                                LiquidStarlightChaliceHandler.doFluidTransfer(this, target, drained.copy());
                                                entry.tryDrain(actMbDrain, true);
                                            }
                                        } else {
                                            drained = new FluidStack(FluidRegistry.WATER, mbDrain);
                                            List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalices(this, drained);
                                            if(!out.isEmpty()) {
                                                TileChalice target = out.get(rand.nextInt(out.size()));
                                                LiquidStarlightChaliceHandler.doFluidTransfer(this, target, drained.copy());
                                            }
                                        }

                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        } else {
            if(hasMultiblock && this.operationTicks > 0) {
                updateBoreSprite();

                if(getCurrentWorkingSegment().ordinal() >= OperationSegment.DIG.ordinal() && ticksExisted % 25 == 0) {
                    float yTarget = this.getPos().getY() * (1 - this.digPercentage);
                    Vector3 origin = new Vector3(getPos()).add(0.5, 0.5, 0.5);
                    Vector3 target = new Vector3(getPos()).add(0.5, 0, 0.5).setY(yTarget);
                    EffectLightbeam beam = EffectHandler.getInstance().lightbeam(target, origin, 9).setAlphaMultiplier(1);
                    beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5);

                    yTarget = this.getPos().getY() * (0.75F - (this.digPercentage / 4F));
                    target = new Vector3(getPos()).add(0.5, 0, 0.5).setY(yTarget);
                    origin = origin.clone().add(
                            rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                            0,
                            rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
                    beam = EffectHandler.getInstance().lightbeam(target, origin, 2).setAlphaMultiplier(1);
                    beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5).setColorOverlay(new Color(0xFF000089));
                }
            }
        }
    }

    private boolean consumeLiquid() {
        if(mbStarlight > 0) {
            mbStarlight--;
            return true;
        }
        return false;
    }

    private void checkDigState() {
        if(digPosResult == null) {
            this.diggingSuccessful = false;
            this.digPercentage = 0;
            return;
        }
        List<BlockPos> out = digPosResult.stream().filter((p) -> !world.isAirBlock(p) && world.getTileEntity(p) == null &&
                world.getBlockState(p).getBlockHardness(world, p) >= 0).collect(Collectors.toList());
        if(!out.isEmpty()) {
            this.diggingSuccessful = false;
            this.digPercentage = 0;
            this.digPosResult = null;
        } else {
            this.diggingSuccessful = true;
            this.digPercentage = 1;
        }
    }

    private void attemptDig() {
        float downPerc = Math.min(1, this.digPercentage + 0.2F);
        float dst = this.getPos().getY() * downPerc;
        List<BlockPos> pos = coneBlockDiscoverer.tryDiscoverBlocksDown(dst, 5F * downPerc);
        List<BlockPos> out = pos.stream().filter((p) -> !world.isAirBlock(p) && world.getTileEntity(p) == null &&
                world.getBlockState(p).getBlockHardness(world, p) >= 0).collect(Collectors.toList());
        if(!out.isEmpty() && world instanceof WorldServer) {
            BlockDropCaptureAssist.startCapturing(false);
            try {
                for (BlockPos p : out) {
                    IBlockState state = world.getBlockState(p);
                    if(!state.getMaterial().isLiquid()) {
                        MiscUtils.breakBlockWithoutPlayer(
                                ((WorldServer) world), p, state, true, true, false);
                    }
                }
            } finally {
                double x = getPos().getX() + 0.5;
                double y = getPos().getY() + 1.5;
                double z = getPos().getZ() + 0.5;
                for (ItemStack stack : BlockDropCaptureAssist.getCapturedStacksAndStop()) {
                    ItemUtils.dropItem(world, x, y, z, stack);
                }
            }
        }
        this.digPercentage = downPerc;
        this.diggingSuccessful = this.digPercentage >= 1F;
        if(this.diggingSuccessful && this.digPosResult == null) {
            this.digPosResult = pos;
        }
    }

    private void handleSetupProgressTick() {
        if(!hasMultiblock || getCurrentBoreType() == BoreType.NONE) {
            this.operationTicks = 0;
            return;
        }
        if(this.operationTicks <= SEGMENT_PREPARATION) {
            this.operationTicks++;
        }
    }

    @Nonnull
    public OperationSegment getCurrentWorkingSegment() {
        if(this.operationTicks == 0) return OperationSegment.INACTIVE;
        if(this.operationTicks <= SEGMENT_STARTUP) {
            return OperationSegment.STARTUP;
        }
        if(this.operationTicks <= SEGMENT_PREPARATION) {
            return OperationSegment.PREPARATION;
        }
        if(!diggingSuccessful) {
            return OperationSegment.DIG;
        }
        return OperationSegment.PRODUCTION;
    }

    @Override
    public boolean canAcceptStarlight(int mbLiquidStarlight) {
        return true;
    }

    @Override
    public void acceptStarlight(int mbLiquidStarlight) {
        this.mbStarlight += mbLiquidStarlight;
        markForUpdate();
    }

    private void updateMultiblockState() {
        boolean found = getRequiredStructure().matches(world, getPos());
        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureSpritePlane updateBoreSprite() {
        TextureSpritePlane spr = (TextureSpritePlane) spritePlane;
        if((spr == null || spr.canRemove() || spr.isRemoved()) &&
                this.operationTicks > 0) {
            spr = EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteHalo3, Vector3.RotAxis.Y_AXIS.clone());
            spr.setPosition(new Vector3(this).add(0.5, 0.5, 0.5));
            spr.setNoRotation(45).setAlphaMultiplier(1F);
            spr.setRefreshFunc(() -> {
                if(isInvalid() || getCurrentBoreType() == BoreType.NONE || this.operationTicks <= 0) {
                    return false;
                }
                if(this.getWorld().provider == null || Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().world.provider == null) {
                    return false;
                }
                return this.getWorld().provider.getDimension() == Minecraft.getMinecraft().world.provider.getDimension();
            });
            spr.setRenderAlphaFunction((fx, a) -> a * Math.min(1, ((float) this.operationTicks) / ((float) SEGMENT_STARTUP)));
            spr.setScale(5.5F);
            spritePlane = spr;
        }
        return spr;
    }

    @Nonnull
    public BoreType getCurrentBoreType() {
        IBlockState parent = world.getBlockState(pos);
        if(parent.getBlock() instanceof BlockBore) {
            return parent.getValue(BlockBore.BORE_TYPE);
        }
        return BoreType.NONE; //Critical state monkaS, world and tile are desynced; shouldn't happen tho FeelsGoodMan
    }

    //Returns previous boreType. null if nothing changed.
    @Nullable
    public BoreType trySetBoreUpgrade(ItemStack stack) {
        if(stack.isEmpty()) {
            if(getCurrentBoreType() == BoreType.NONE) return null;
            BoreType prev = getCurrentBoreType();
            this.world.setBlockState(pos, BlocksAS.blockBore.getDefaultState().withProperty(BlockBore.BORE_TYPE, BoreType.NONE));
            return prev;
        } else {
            if(getCurrentBoreType() != BoreType.NONE) return null;
            BoreType next = ItemBoreUpgrade.getBoreType(stack);
            if(next == BoreType.NONE) return null;
            this.world.setBlockState(pos, BlocksAS.blockBore.getDefaultState().withProperty(BlockBore.BORE_TYPE, next));
            return BoreType.NONE;
        }
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternFountain;
    }

    @Override
    protected void onFirstTick() {}

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("tank", this.tank.writeNBT());
        compound.setInteger("operation", this.operationTicks);
        compound.setBoolean("multiblockState", this.hasMultiblock);
        compound.setBoolean("digState", this.diggingSuccessful);
        compound.setFloat("digPerc", this.digPercentage);
        compound.setInteger("mbStarlight", this.mbStarlight);
    }

    @Override
    public void writeSaveNBT(NBTTagCompound compound) {
        super.writeSaveNBT(compound);

        compound.setInteger("productionTick", this.productionTimeout);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
        if(!tank.hasCapability(EnumFacing.UP)) {
            tank.accessibleSides.add(EnumFacing.UP);
        }
        this.operationTicks = compound.getInteger("operation");
        this.hasMultiblock = compound.getBoolean("multiblockState");
        this.diggingSuccessful = compound.getBoolean("digState");
        this.digPercentage = compound.getFloat("digPerc");
        this.mbStarlight = compound.getInteger("mbStarlight");
    }

    @Override
    public void readSaveNBT(NBTTagCompound compound) {
        super.readSaveNBT(compound);

        this.productionTimeout = compound.getInteger("productionTick");
    }

    public static enum OperationSegment {

        INACTIVE,
        STARTUP,
        PREPARATION,
        DIG,
        PRODUCTION

    }

    public static enum BoreType implements IStringSerializable {

        NONE,
        LIQUID;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
