/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.block.BlockBoreHead;
import hellfirepvp.astralsorcery.common.data.config.Config;
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
    public void update() {
        super.update();

        if((ticksExisted & 31) == 0) {
            updateMultiblockState();
        }

        if(!world.isRemote) {
            if(coneBlockDiscoverer == null) {
                coneBlockDiscoverer = new VerticalConeBlockDiscoverer(this.getPos().down(3));
            }

            if(mbStarlight <= 12000 && getCurrentBoreType() != null) {
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
            if(getCurrentBoreType() == null) {
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
                            productionTimeout = rand.nextInt(10) + 20;
                            BoreType type = getCurrentBoreType();
                            if(type != null) {
                                switch (type) {
                                    case LIQUID:
                                        Chunk ch = world.getChunkFromBlockCoords(getPos());
                                        FluidRarityRegistry.ChunkFluidEntry entry = FluidRarityRegistry.getChunkEntry(ch);
                                        if(entry != null) {
                                            int mbDrain = rand.nextInt(300) + 300;
                                            int actMbDrain = Math.min(entry.getMbRemaining(), mbDrain);
                                            FluidStack drained;
                                            if(entry.isValid() && actMbDrain > 0) {
                                                drained = entry.tryDrain(actMbDrain, false);
                                                if(drained == null || drained.getFluid() == null) {
                                                    drained = new FluidStack(FluidRegistry.WATER, mbDrain);
                                                }
                                                List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalicesWithSpaceFor(this, drained);
                                                out.removeIf((t) -> t.getPos().equals(getPos().up()));
                                                if(!out.isEmpty()) {
                                                    TileChalice target = out.get(rand.nextInt(out.size()));
                                                    LiquidStarlightChaliceHandler.doFluidTransfer(this, target, drained.copy());
                                                    entry.tryDrain(actMbDrain, true);
                                                }
                                            } else {
                                                drained = new FluidStack(FluidRegistry.WATER, mbDrain);
                                                List<TileChalice> out = LiquidStarlightChaliceHandler.findNearbyChalicesWithSpaceFor(this, drained);
                                                out.removeIf((t) -> t.getPos().equals(getPos().up()));
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
            }
        } else {
            if(hasMultiblock && this.operationTicks > 0 && getCurrentBoreType() != null) {
                updateBoreSprite();

                switch (getCurrentWorkingSegment()) {
                    case STARTUP:
                        float chance = ((float) this.operationTicks) / ((float) SEGMENT_STARTUP);
                        playVortex(chance);
                        playArcs(chance);
                        break;
                    case PREPARATION:
                        float prepChance = ((float) this.operationTicks - SEGMENT_STARTUP) / ((float) SEGMENT_PREPARATION - SEGMENT_STARTUP);
                        playArcs(prepChance);
                        playVortex(1F - prepChance);
                        if(operationTicks == SEGMENT_PREPARATION) {
                            markDigProcess();
                        }
                        if(prepChance <= 0.85) {
                            playInnerVortex(Math.max(0, (-0.35 + prepChance) * 2F));
                        } else {
                            double ch = (prepChance - 0.85);
                            ch /= 0.15;
                            playInnerVortex(1 - ch);
                        }
                        break;
                    case DIG:
                    case PRODUCTION:
                        playLightbeam();
                        playArcs(1);
                        break;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playInnerVortex(double chance) {
        for (int i = 0; i < 12; i++) {
            if(rand.nextFloat() < chance) {
                Vector3 particlePos = new Vector3(
                        pos.getX() - 0.4 + rand.nextFloat() * 1.8,
                        pos.getY()       - rand.nextFloat() * 3,
                        pos.getZ() - 0.4 + rand.nextFloat() * 1.8
                );
                Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().divide(-30);
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
                p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
                if(rand.nextBoolean()) {
                    p.setColor(new Color(0x5865FF));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void markDigProcess() {
        int x = getPos().getX();
        int z = getPos().getZ();
        for (int yy = 0; yy < getPos().getY(); yy++) {
            BlockPos pos = new BlockPos(x, yy, z);
            IBlockState at = world.getBlockState(pos);
            if(at.isTranslucent() || at.getBlock().isAir(at, world, pos)) {
                for (int i = 0; i < 20; i++) {
                    Vector3 v = new Vector3(
                            x + 0.2 + rand.nextFloat() * 0.6,
                            yy + rand.nextFloat(),
                            z + 0.2 + rand.nextFloat() * 0.6
                    );
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
                    p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
                }
            }
        }

        Vector3 origin = new Vector3(getPos()).add(0.5, 0.5, 0.5);
        Vector3 target = new Vector3(getPos()).add(0.5, 0, 0.5).setY(0);
        EffectLightbeam beam = EffectHandler.getInstance().lightbeam(target, origin, 1.5).setAlphaMultiplier(1);
        beam.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT);
        beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5).setColorOverlay(new Color(0x5865FF));
    }

    @SideOnly(Side.CLIENT)
    private void playVortex(float chance) {
        for (int i = 0; i < 20; i++) {
            if(rand.nextFloat() < chance) {
                Vector3 particlePos = new Vector3(
                        pos.getX() - 3   + rand.nextFloat() * 7,
                        pos.getY()       + rand.nextFloat(),
                        pos.getZ() - 3   + rand.nextFloat() * 7
                );
                Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().divide(-30);
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
                p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playArcs(float chance) {
        if((chance == 1 || rand.nextFloat() < chance) && rand.nextInt(10) == 0) {
            Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
            Vector3 dir = new Vector3(1, 0, 0).rotate(Math.toRadians(rand.nextFloat() * 360), Vector3.RotAxis.Y_AXIS);
            dir.normalize().multiply(4);
            Vector3 pos1 = pos.clone().add(dir);
            dir = new Vector3(1, 0, 0).rotate(Math.toRadians(rand.nextFloat() * 360), Vector3.RotAxis.Y_AXIS);
            dir.normalize().multiply(4);
            Vector3 pos2 = pos.clone().add(dir);

            EffectHandler.getInstance().lightning(pos1, pos2);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playLightbeam() {
        Vector3 particlePos = new Vector3(
                pos.getX() - 2.5 + rand.nextFloat() * 6,
                pos.getY() - 1.2 + rand.nextFloat() * 3.4,
                pos.getZ() - 2.5 + rand.nextFloat() * 6
        );
        Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).normalize().divide(-30);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
        p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);

        for (int i = 0; i < 5; i++) {
            Vector3 v = new Vector3(this).add(0.3 + rand.nextFloat() * 0.4, -rand.nextFloat() * 1.7, 0.3 + rand.nextFloat() * 0.4);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
            particle.gravity(0.004).scale(0.4F).setAlphaMultiplier(1F);
            particle.motion(0, -rand.nextFloat() * 0.015, 0);
            particle.setColor(Color.getHSBColor(rand.nextFloat() * 360F, 1F, 1F));
        }

        if(ticksExisted % 25 != 0) return;

        float yTarget = this.getPos().getY() * (1 - this.digPercentage);
        Vector3 origin = new Vector3(getPos()).add(0.5, 0.5, 0.5);
        Vector3 target = new Vector3(getPos()).add(0.5, 0, 0.5).setY(yTarget);
        EffectLightbeam beam = EffectHandler.getInstance().lightbeam(target, origin, 9).setAlphaMultiplier(1);
        beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5);

        yTarget = this.getPos().getY() * (0.75F - (this.digPercentage / 4F));
        target = new Vector3(getPos()).add(0.5, 0, 0.5).setY(yTarget);
        origin = origin.clone().add(
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                0,
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        beam = EffectHandler.getInstance().lightbeam(target, origin, 0.8).setAlphaMultiplier(1);
        beam.setDistanceCapSq(Config.maxEffectRenderDistanceSq * 5).setColorOverlay(new Color(0x6A9EFF));
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
            BlockDropCaptureAssist.startCapturing();
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
        if(!hasMultiblock || getCurrentBoreType() == null) {
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
        this.mbStarlight += mbLiquidStarlight * 2;
        markForUpdate();
    }

    private void updateMultiblockState() {
        boolean found = getRequiredStructure().matches(world, getPos());
        if(found) {
            found = doEmptyCheck();
        }
        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
        }
    }

    private boolean doEmptyCheck() {
        for (int yy = -2; yy <= 2; yy++) {
            for (int xx = -3; xx <= 3; xx++) {
                for (int zz = -3; zz <= 3; zz++) {
                    if(Math.abs(xx) == 3 && Math.abs(zz) == 3) continue; //corners
                    BlockPos at = getPos().add(xx, yy, zz);
                    if(xx == 0 && zz == 0) {
                        switch (yy) {
                            case -2: {
                                if(!world.isAirBlock(at)) {
                                    return false;
                                }
                                break;
                            }
                        }
                    } else {
                        if(!world.isAirBlock(at)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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
                if(isInvalid() || getCurrentBoreType() == null || this.operationTicks <= 0) {
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

    @Nullable
    public BoreType getCurrentBoreType() {
        IBlockState parent = world.getBlockState(pos.down());
        if(parent.getBlock() instanceof BlockBoreHead) {
            return parent.getValue(BlockBoreHead.BORE_TYPE);
        }
        return null;
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

        LIQUID,
        VORTEX;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
