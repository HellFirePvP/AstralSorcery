/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopEffectHelper
 * Created by HellFirePvP
 * Date: 17.10.2017 / 22:55
 */
//Shell class for rendering and syncing over network.
public class TimeStopEffectHelper {

    private static Random rand = new Random();

    @Nonnull
    private BlockPos position;
    private float range;
    private TimeStopZone.EntityTargetController targetController;
    private boolean reducedParticles;

    private TimeStopEffectHelper(@Nonnull BlockPos position, float range, TimeStopZone.EntityTargetController targetController, boolean reducedParticles) {
        this.position = position;
        this.range = range;
        this.targetController = targetController;
        this.reducedParticles = reducedParticles;
    }

    static TimeStopEffectHelper fromZone(TimeStopZone zone) {
        return new TimeStopEffectHelper(zone.offset, zone.range, zone.targetController, zone.reducedParticles);
    }

    @Nonnull
    public BlockPos getPosition() {
        return position;
    }

    public float getRange() {
        return range;
    }

    public TimeStopZone.EntityTargetController getTargetController() {
        return targetController;
    }

    @SideOnly(Side.CLIENT)
    static void playEntityParticles(EntityLivingBase e) {
        double x = e.posX - e.width + rand.nextFloat() * e.width * 2;
        double y = e.posY + rand.nextFloat() * e.height;
        double z = e.posZ - e.width + rand.nextFloat() * e.width * 2;
        playEntityParticles(x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public static void playEntityParticles(PktParticleEvent ev) {
        playEntityParticles(ev.getVec().getX(), ev.getVec().getY(), ev.getVec().getZ());
    }

    @SideOnly(Side.CLIENT)
    static void playEntityParticles(double x, double y, double z) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
        p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.scale(rand.nextFloat() * 0.5F + 0.3F).gravity(0.004);
        p.setMaxAge(40 + rand.nextInt(20));

        if(rand.nextFloat() < 0.9F) {
            p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.2F + 0.1F).gravity(0.004);
            p.setMaxAge(30 + rand.nextInt(10));
        }
    }

    @SideOnly(Side.CLIENT)
    public void playClientTickEffect() {
        Random rand = new Random();
        List<EntityLivingBase> entities = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(-range, -range, -range, range, range, range).offset(position.getX(), position.getY(), position.getZ()),
                EntitySelectors.withinRange(position.getX(), position.getY(), position.getZ(), range));
        for (EntityLivingBase e : entities) {
            if(e != null && !e.isDead && targetController.shouldFreezeEntity(e)) {
                if(reducedParticles && rand.nextInt(5) == 0) continue;
                playEntityParticles(e);
            }
        }

        int minX = MathHelper.floor((position.getX() - range) / 16.0D);
        int maxX = MathHelper.floor((position.getX() + range) / 16.0D);
        int minZ = MathHelper.floor((position.getZ() - range) / 16.0D);
        int maxZ = MathHelper.floor((position.getZ() + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = Minecraft.getMinecraft().world.getChunkFromChunkCoords(xx, zz);
                if(!ch.isEmpty()) {
                    Map<BlockPos, TileEntity> map = ch.getTileEntityMap();
                    for (Map.Entry<BlockPos, TileEntity> teEntry : map.entrySet()) {
                        if(reducedParticles && rand.nextInt(5) == 0) continue;
                        TileEntity te = teEntry.getValue();
                        if(te != null &&
                                te instanceof ITickable &&
                                position.getDistance(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()) <= range) {

                            double x = te.getPos().getX() + rand.nextFloat();
                            double y = te.getPos().getY() + rand.nextFloat();
                            double z = te.getPos().getZ() + rand.nextFloat();

                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
                            p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                            p.scale(rand.nextFloat() * 0.5F + 0.3F).gravity(0.004);
                            p.setMaxAge(40 + rand.nextInt(20));

                            if(rand.nextFloat() < 0.9F) {
                                p = EffectHelper.genericFlareParticle(x, y, z);
                                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                                p.scale(rand.nextFloat() * 0.2F + 0.1F).gravity(0.004);
                                p.setMaxAge(30 + rand.nextInt(10));
                            }
                        }
                    }
                }
            }
        }

        Vector3 pos;
        for (int i = 0; i < 10; i++) {
            if(reducedParticles && rand.nextInt(5) == 0) continue;
            pos = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.5F + 0.3F).gravity(0.004);
            p.setMaxAge(40 + rand.nextInt(20));

            if(rand.nextFloat() < 0.9F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.2F + 0.1F).gravity(0.004);
                p.setMaxAge(30 + rand.nextInt(10));
            }
        }
        if(rand.nextInt(4) == 0) {
            if(!reducedParticles || rand.nextInt(5) == 0) {
                Vector3 rand1 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
                Vector3 rand2 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
                AstralSorcery.proxy.fireLightning(Minecraft.getMinecraft().world, rand1, rand2, Color.WHITE);
            }
        }
    }

    @Nonnull
    public NBTTagCompound serializeNBT() {
        NBTTagCompound out = new NBTTagCompound();
        NBTUtils.writeBlockPosToNBT(this.position, out);
        out.setFloat("range", this.range);
        out.setTag("targetController", this.targetController.serializeNBT());
        out.setBoolean("reducedParticles", this.reducedParticles);
        return out;
    }

    @Nonnull
    public static TimeStopEffectHelper deserializeNBT(NBTTagCompound cmp) {
        BlockPos at = NBTUtils.readBlockPosFromNBT(cmp);
        float range = cmp.getFloat("range");
        boolean reducedParticles = cmp.getBoolean("reducedParticles");
        return new TimeStopEffectHelper(at, range, TimeStopZone.EntityTargetController.deserializeNBT(cmp.getCompoundTag("targetController")), reducedParticles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeStopEffectHelper that = (TimeStopEffectHelper) o;

        return Float.compare(that.range, range) == 0 &&
                position.equals(that.position);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        return result;
    }

}
