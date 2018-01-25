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

    @Nonnull
    private BlockPos position;
    private float range;

    private boolean hasOwner;
    private int ownerEntityId;

    private TimeStopEffectHelper(@Nonnull BlockPos position, float range, boolean hasOwner, int ownerEntityId) {
        this.position = position;
        this.range = range;
        this.hasOwner = hasOwner;
        this.ownerEntityId = ownerEntityId;
    }

    static TimeStopEffectHelper fromZone(TimeStopZone zone) {
        return new TimeStopEffectHelper(zone.offset, zone.range, zone.hasOwner, zone.ownerId);
    }

    @Nonnull
    public BlockPos getPosition() {
        return position;
    }

    public float getRange() {
        return range;
    }

    public boolean hasOwner() {
        return hasOwner;
    }

    public int getOwnerEntityId() {
        return ownerEntityId;
    }

    @SideOnly(Side.CLIENT)
    public void playClientTickEffect() {
        Color c = new Color(0xDDDDDD);
        Random rand = new Random();
        List<EntityLivingBase> entities = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(-range, -range, -range, range, range, range).offset(position.getX(), position.getY(), position.getZ()),
                EntitySelectors.withinRange(position.getX(), position.getY(), position.getZ(), range));
        for (EntityLivingBase e : entities) {
            if(e != null && !e.isDead && (!hasOwner || e.getEntityId() != ownerEntityId)) {
                if(rand.nextInt(3) != 0) continue;
                double x = e.posX - e.width + rand.nextFloat() * e.width * 2;
                double y = e.posY + rand.nextFloat() * e.height;
                double z = e.posZ - e.width + rand.nextFloat() * e.width * 2;

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
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
                        if(rand.nextInt(3) != 0) continue;
                        TileEntity te = teEntry.getValue();
                        if(te != null &&
                                te instanceof ITickable &&
                                position.getDistance(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()) <= range) {

                            double x = te.getPos().getX() + rand.nextFloat();
                            double y = te.getPos().getY() + rand.nextFloat();
                            double z = te.getPos().getZ() + rand.nextFloat();

                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
                            p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
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

        Vector3 pos = null;
        for (int i = 0; i < 10; i++) {
            pos = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
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
            Vector3 rand1 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            Vector3 rand2 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            AstralSorcery.proxy.fireLightning(Minecraft.getMinecraft().world, rand1, rand2, c);
        }
    }

    @Nonnull
    public NBTTagCompound serializeNBT() {
        NBTTagCompound out = new NBTTagCompound();
        NBTUtils.writeBlockPosToNBT(this.position, out);
        out.setFloat("range", this.range);
        out.setBoolean("hasOwner", this.hasOwner);
        out.setInteger("ownerEntityId", this.ownerEntityId);
        return out;
    }

    @Nonnull
    public static TimeStopEffectHelper deserializeNBT(NBTTagCompound cmp) {
        BlockPos at = NBTUtils.readBlockPosFromNBT(cmp);
        float range = cmp.getFloat("range");
        boolean hasOwner = cmp.getBoolean("hasOwner");
        int ownerId = cmp.getInteger("ownerEntityId");
        return new TimeStopEffectHelper(at, range, hasOwner, ownerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeStopEffectHelper that = (TimeStopEffectHelper) o;

        return Float.compare(that.range, range) == 0 &&
                hasOwner == that.hasOwner &&
                ownerEntityId == that.ownerEntityId &&
                position.equals(that.position);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (hasOwner ? 1 : 0);
        result = 31 * result + ownerEntityId;
        return result;
    }

}
