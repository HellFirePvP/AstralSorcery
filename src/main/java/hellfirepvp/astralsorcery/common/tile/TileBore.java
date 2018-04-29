/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.controller.ControllerNoisePlane;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.auxiliary.LiquidStarlightChaliceHandler;
import hellfirepvp.astralsorcery.common.block.BlockBoreHead;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.entities.EntityTechnicalAmbient;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.VerticalConeBlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    private boolean preparationSuccessful = false;
    private float digPercentage = 0;
    private List<BlockPos> digPosResult = null;

    private Object spritePlane = null, facingVortexPlane = null;
    private List ctrlEffectNoise = null;

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
            if(mbStarlight <= 12000 && getCurrentBoreType() != null) {
                TileChalice tc = MiscUtils.getTileAt(world, getPos().up(), TileChalice.class, false);
                if(tc != null) {
                    LiquidStarlightChaliceHandler.requestLiquidStarlightAndTransferTo(this, tc, ticksExisted, 400);
                }
            }
            if(!consumeLiquid()) {
                if(this.operationTicks > 0) {
                    operationTicks -= 10;
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
                switch (getCurrentBoreType()) {
                    case LIQUID:
                        if(coneBlockDiscoverer == null) {
                            coneBlockDiscoverer = new VerticalConeBlockDiscoverer(this.getPos().down(3));
                        }

                        if(!preparationSuccessful) {
                            if((ticksExisted % 8) == 0) {
                                attemptDig();
                            }
                        } else {
                            if((ticksExisted % 32) == 0) {
                                checkDigState();
                            }
                            if(this.preparationSuccessful) {
                                playBoreLiquidEffect();
                            }
                        }
                        break;
                    case VORTEX:
                        if(!preparationSuccessful) {
                            if((ticksExisted % 8) == 0) {
                                attemptDigVortex();
                            }
                        } else {
                            if((ticksExisted % 32) == 0) {
                                checkVortexDigState();
                            }
                            if(this.preparationSuccessful) {
                                playBoreVortexEffect();
                            }
                        }
                        break;
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
                        if(getCurrentBoreType() == BoreType.VORTEX) {
                            playCoreParticles(chance);
                        }
                        break;
                    case PREPARATION:
                        float prepChance = ((float) this.operationTicks - SEGMENT_STARTUP) / ((float) SEGMENT_PREPARATION - SEGMENT_STARTUP);
                        playArcs(prepChance);
                        switch (getCurrentBoreType()) {
                            case LIQUID:
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
                            case VORTEX:
                                playVortex(1F - (prepChance * 0.5F));
                                playCoreParticles(1F - (2 * prepChance));
                                playVortexCore(prepChance);
                                if(operationTicks == SEGMENT_PREPARATION) {
                                    vortexExplosion();
                                }
                                break;
                        }
                        break;
                    case PRE_RUN:
                    case PRODUCTION:
                        switch (getCurrentBoreType()) {
                            case LIQUID:
                                playLightbeam();
                                break;
                            case VORTEX:
                                playVortex(0.5F);
                                playLowVortex();
                                updateNoisePlane();
                                break;
                        }
                        playArcs(1);
                        break;
                }
            }
        }
    }

    private void playBoreVortexEffect() {
        AxisAlignedBB boxVortex = new AxisAlignedBB(-3, -7, -3, 3, -3, 3).offset(getPos());
        AxisAlignedBB drawBox = boxVortex.grow(16);

        double boxSizeX = boxVortex.maxX - boxVortex.minX;
        double boxSizeY = boxVortex.maxY - boxVortex.minY;
        double boxSizeZ = boxVortex.maxZ - boxVortex.minZ;

        double densityMax = boxSizeX * boxSizeY * boxSizeZ;
        double density = densityMax;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, boxVortex);
        for (EntityLivingBase e : entities) {
            if(e == null || e.isDead || e instanceof EntityPlayer || e instanceof EntityTechnicalAmbient) continue;
            if(e.width * e.width * e.height >= boxSizeX * boxSizeY * boxSizeZ) {
                if(e.getPositionVector().distanceTo(new Vec3d(getPos().getX() + 0.5, getPos().getY() - 5.5, getPos().getZ() + 0.5)) >= 0.1) {
                    e.setPositionAndUpdate(
                            getPos().getX() + 0.5,
                            getPos().getY() - 5.5,
                            getPos().getZ() + 0.5
                    );
                    //To move all the dragon-pieces along...
                    if(e instanceof EntityDragon) {
                        String prev = world.getGameRules().getString("mobGriefing");
                        world.getGameRules().setOrCreateGameRule("mobGriefing", "false");
                        e.onLivingUpdate();
                        world.getGameRules().setOrCreateGameRule("mobGriefing", prev);
                    }
                }
            }
            if(e instanceof EntityDragon) {
                e.getActivePotionMap().put(RegistryPotions.potionTimeFreeze, new PotionEffect(RegistryPotions.potionTimeFreeze, 40, 0));
            } else {
                e.addPotionEffect(new PotionEffect(RegistryPotions.potionTimeFreeze, 80, 0));
            }
            density -= (e.width * e.width * e.height);
        }
        consumeLiquid(Math.max(0, MathHelper.ceil(Math.abs(density) / densityMax)));

        List<EntityLivingBase> draws = world.getEntitiesWithinAABB(EntityLivingBase.class, drawBox);
        draws.removeAll(entities);
        for (EntityLivingBase e : draws) {
            if(e == null || e.isDead || e instanceof EntityPlayer || e instanceof EntityTechnicalAmbient) continue;
            if(e instanceof EntityDragon) {
                e.getActivePotionMap().put(RegistryPotions.potionTimeFreeze, new PotionEffect(RegistryPotions.potionTimeFreeze, 80, 0));
            }
            EntityUtils.applyVortexMotion((v) -> Vector3.atEntityCorner(e), (v) -> {
                if(e instanceof EntityDragon) {
                    e.posX += v.getX();
                    e.posY += v.getY();
                    e.posZ += v.getZ();
                    e.motionX = 0;
                    e.motionY = 0;
                    e.motionZ = 0;
                } else {
                    e.motionX += v.getX();
                    e.motionY += (v.getY() * 2.5);
                    e.motionZ += v.getZ();
                }
                return null;
            }, new Vector3(this).addY(-4.5), 48, 3);

            if(e.getDistanceSq(getPos().add(0, -5, 0)) <= (25)) { // 5 * 5
                Vector3 randomBuffer = new Vector3(
                        Math.max(0, (boxSizeX - e.width ) / 2D),
                        Math.max(0, (boxSizeY - e.height) / 2D),
                        Math.max(0, (boxSizeZ - e.width ) / 2D));
                Vector3 randPos = new Vector3(this).addY(-4.5)
                        .add(
                                randomBuffer.getX() * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1),
                                randomBuffer.getY() * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1),
                                randomBuffer.getZ() * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
                e.setPositionAndUpdate(randPos.getX(), randPos.getY(), randPos.getZ());
                if(e instanceof EntityDragon) {
                    e.getActivePotionMap().put(RegistryPotions.potionTimeFreeze, new PotionEffect(RegistryPotions.potionTimeFreeze, 80, 0));
                } else {
                    e.addPotionEffect(new PotionEffect(RegistryPotions.potionTimeFreeze, 80, 0));
                }
                consumeLiquid(2);
            }
        }
    }

    private void playBoreLiquidEffect() {
        if(productionTimeout > 0) {
            productionTimeout--;
        }
        if(productionTimeout <= 0) {
            productionTimeout = rand.nextInt(10) + 20;
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
        }
    }

    @SideOnly(Side.CLIENT)
    private void playLowVortex() {
        for (int i = 0; i < 2; i++) {
            Vector3 dir = new Vector3(
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1)
            );
            Vector3 v = new Vector3(this).add(0.5, -0.65, 0.5);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
            particle.gravity(0.004).scale(0.4F).setAlphaMultiplier(1F);
            particle.motion(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(Color.getHSBColor(rand.nextFloat() * 360F, 1F, 1F));
        }

        for (int i = 0; i < 3; i++) {
            Vector3 particlePos = new Vector3(
                    pos.getX() - 4  + rand.nextFloat() * 9,
                    pos.getY() - 6  + rand.nextFloat() * 9,
                    pos.getZ() - 4  + rand.nextFloat() * 9
            );
            Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() - 3.5, pos.getZ() + 0.5).normalize().divide(-30);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateNoisePlane() {
        EntityFXFacingSprite spr = (EntityFXFacingSprite) facingVortexPlane;
        if((spr == null || spr.canRemove() || spr.isRemoved()) &&
                this.operationTicks > 0) {
            spr = EntityFXFacingSprite.fromSpriteSheet(SpriteLibrary.spriteStar2,
                    getPos().getX() + 0.5, getPos().getY() - 3.5F, getPos().getZ() + 0.5, 2F, 2);
            spr.setRefreshFunc(() -> {
                if(isInvalid() || getCurrentBoreType() == null || this.operationTicks <= 0) {
                    return false;
                }
                if(this.getWorld().provider == null || Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().world.provider == null) {
                    return false;
                }
                return this.getWorld().provider.getDimension() == Minecraft.getMinecraft().world.provider.getDimension();
            });
            EffectHandler.getInstance().registerFX(spr);
            facingVortexPlane = spr;
        }

        if(ctrlEffectNoise == null) {
            ctrlEffectNoise = Lists.newArrayList(
                    new ControllerNoisePlane(1.2F),
                    new ControllerNoisePlane(1.8F),
                    new ControllerNoisePlane(2.4F)
            );
        }

        for (Object ctrl : ctrlEffectNoise) {
            for (int i = 0; i < 3; i++) {
                EntityFXFacingParticle p = ((ControllerNoisePlane) ctrl).setupParticle();
                p.updatePosition(getPos().getX() + 0.5, getPos().getY() - 3.5, getPos().getZ() + 0.5)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .motion(rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1))
                .scale(0.15F + rand.nextFloat() * 0.05F)
                .setMaxAge(30 + rand.nextInt(15));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void vortexExplosion() {
        for (int i = 0; i < 140; i++) {
            Vector3 particlePos = new Vector3(
                    pos.getX() + 0.5 - 0.1F + rand.nextFloat() * 0.2,
                    pos.getY() - 3.5 - 0.1F + rand.nextFloat() * 0.2,
                    pos.getZ() + 0.5 - 0.1F + rand.nextFloat() * 0.2
            );
            Vector3 dir = new Vector3(
                    rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.15 * (rand.nextBoolean() ? 1 : -1)
            );
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.3F + rand.nextFloat() * 0.15F).setColor(Color.WHITE);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playVortexCore(float prepChance) {
        float yOffset = -0.5F - (3.0F * Math.min(1, prepChance * 2F));
        for (int i = 0; i < 15; i++) {
            Vector3 particlePos = new Vector3(
                    pos.getX() + 0.5     - 0.1F + rand.nextFloat() * 0.2,
                    pos.getY() + yOffset - 0.1F + rand.nextFloat() * 0.2,
                    pos.getZ() + 0.5     - 0.1F + rand.nextFloat() * 0.2
            );
            float mul = prepChance <= 0.5F ? 1 : (1F - prepChance);
            Vector3 dir = new Vector3(
                    rand.nextFloat() * 0.035 * mul * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.035 * mul * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.035 * mul * (rand.nextBoolean() ? 1 : -1)
            );
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
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
    private void playCoreParticles(float chance) {
        for (int i = 0; i < 20; i++) {
            if(rand.nextFloat() < chance) {
                Vector3 particlePos = new Vector3(
                        pos.getX() - 1     + rand.nextFloat() * 3,
                        pos.getY() - 1.5   + rand.nextFloat() * 2,
                        pos.getZ() - 1     + rand.nextFloat() * 3
                );
                Vector3 dir = particlePos.clone().subtract(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5).normalize().divide(-30);
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
                p.motion(dir.getX(), dir.getY(), dir.getZ()).setAlphaMultiplier(1F).setMaxAge(rand.nextInt(40) + 20);
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(0.2F + rand.nextFloat() * 0.1F).setColor(Color.WHITE);
            }
        }
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
        return consumeLiquid(1);
    }

    private boolean consumeLiquid(int amt) {
        if(mbStarlight >= amt) {
            mbStarlight -= amt;
            return true;
        }
        mbStarlight = 0;
        return false;
    }

    private void checkVortexDigState() {
        if(digPosResult == null) {
            this.preparationSuccessful = false;
            return;
        }
        List<BlockPos> out = digPosResult.stream().filter((p) -> !world.isAirBlock(p) && world.getTileEntity(p) == null &&
                world.getBlockState(p).getBlockHardness(world, p) >= 0).collect(Collectors.toList());
        if(!out.isEmpty()) {
            this.preparationSuccessful = false;
            this.digPosResult = null;
        } else {
            this.preparationSuccessful = true;
        }
    }

    private void checkDigState() {
        if(digPosResult == null) {
            this.preparationSuccessful = false;
            this.digPercentage = 0;
            return;
        }
        List<BlockPos> out = digPosResult.stream().filter((p) -> !world.isAirBlock(p) && world.getTileEntity(p) == null &&
                world.getBlockState(p).getBlockHardness(world, p) >= 0).collect(Collectors.toList());
        if(!out.isEmpty()) {
            this.preparationSuccessful = false;
            this.digPercentage = 0;
            this.digPosResult = null;
        } else {
            this.preparationSuccessful = true;
            this.digPercentage = 1;
        }
    }

    private void attemptDigVortex() {
        List<BlockPos> pos = Lists.newLinkedList();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                for (int yy = -3; yy >= -7; yy--) {
                    pos.add(getPos().add(xx, yy, zz));
                }
            }
        }
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
        this.digPosResult = pos;
        this.preparationSuccessful = true;
    }

    private void attemptDig() {
        float downPerc = Math.min(1, this.digPercentage + 0.2F);
        float dst = this.getPos().getY() * downPerc;
        List<BlockPos> pos = coneBlockDiscoverer.tryDiscoverBlocksDown(dst, 5F * downPerc);
        List<BlockPos> out = pos.stream().filter((p) -> !world.isAirBlock(p) && world.getTileEntity(p) == null &&
                world.getBlockState(p).getBlockHardness(world, p) >= 0).collect(Collectors.toList());
        if(!out.isEmpty() && world instanceof WorldServer) {
            //TODO check drops again
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
                BlockDropCaptureAssist.getCapturedStacksAndStop();
            }
        }
        this.digPercentage = downPerc;
        this.preparationSuccessful = this.digPercentage >= 1F;
        if(this.preparationSuccessful && this.digPosResult == null) {
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
        if(!preparationSuccessful) {
            return OperationSegment.PRE_RUN;
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
    private TextureSpritePlane updateBoreSprite() {
        TextureSpritePlane spr = (TextureSpritePlane) spritePlane;
        if((spr == null || spr.canRemove() || spr.isRemoved()) &&
                this.operationTicks > 0) {
            SpriteSheetResource srs;
            switch (getCurrentBoreType()) {
                case VORTEX:
                    srs = SpriteLibrary.spriteVortex1;
                    break;
                default:
                case LIQUID:
                    srs = SpriteLibrary.spriteHalo3;
                    break;
            }
            spr = EffectHandler.getInstance().textureSpritePlane(srs, Vector3.RotAxis.Y_AXIS.clone());
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
        compound.setBoolean("digState", this.preparationSuccessful);
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
        this.preparationSuccessful = compound.getBoolean("digState");
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
        PRE_RUN,
        PRODUCTION

    }

    public static enum BoreType implements IStringSerializable {

        LIQUID,
        VORTEX;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockBoreHead, 1, ordinal());
        }

    }

}
