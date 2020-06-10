/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopEffectHelper
 * Created by HellFirePvP
 * Date: 31.08.2019 / 13:30
 */
public class TimeStopEffectHelper {

    private static Random rand = new Random();

    @Nonnull
    private BlockPos position;
    private float range;
    private TimeStopZone.EntityTargetController targetController;

    private TimeStopEffectHelper(@Nonnull BlockPos position, float range, TimeStopZone.EntityTargetController targetController) {
        this.position = position;
        this.range = range;
        this.targetController = targetController;
    }

    static TimeStopEffectHelper fromZone(TimeStopZone zone) {
        return new TimeStopEffectHelper(zone.offset, zone.range, zone.targetController);
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

    @OnlyIn(Dist.CLIENT)
    static void playEntityParticles(LivingEntity e) {
        EntitySize size = e.getSize(e.getPose());
        double x = e.getPosX() - size.width + rand.nextFloat() * size.width * 2;
        double y = e.getPosY() + rand.nextFloat() * size.height;
        double z = e.getPosZ() - size.width + rand.nextFloat() * size.width * 2;
        playParticles(x, y, z);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playEntityParticles(PktPlayEffect ev) {
        Vector3 at = ByteBufUtils.readVector(ev.getExtraData());
        playParticles(at.getX(), at.getY(), at.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    static void playParticles(double x, double y, double z) {
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(x, y, z))
                .alpha(VFXAlphaFunction.FADE_OUT)
                .color(VFXColorFunction.WHITE)
                .setScaleMultiplier(0.3F + rand.nextFloat() * 0.5F)
                .setMaxAge(40 + rand.nextInt(20));
    }

    @OnlyIn(Dist.CLIENT)
    public void playClientTickEffect() {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                new AxisAlignedBB(-range, -range, -range, range, range, range).offset(position.getX(), position.getY(), position.getZ()),
                EntityPredicates.withinRange(position.getX(), position.getY(), position.getZ(), range));

        for (LivingEntity e : entities) {
            if (e != null && e.isAlive() && targetController.shouldFreezeEntity(e) && rand.nextInt(3) == 0) {
                playEntityParticles(e);
            }
        }

        int minX = MathHelper.floor((position.getX() - range) / 16.0D);
        int maxX = MathHelper.floor((position.getX() + range) / 16.0D);
        int minZ = MathHelper.floor((position.getZ() - range) / 16.0D);
        int maxZ = MathHelper.floor((position.getZ() + range) / 16.0D);

        for (int xx = minX; xx <= maxX; ++xx) {
            for (int zz = minZ; zz <= maxZ; ++zz) {
                Chunk ch = world.getChunk(xx, zz);
                if (!ch.isEmpty()) {
                    Map<BlockPos, TileEntity> map = ch.getTileEntityMap();
                    for (Map.Entry<BlockPos, TileEntity> teEntry : map.entrySet()) {

                        TileEntity te = teEntry.getValue();
                        if (te instanceof ITickableTileEntity && te.getPos().withinDistance(position, range)) {

                            double x = te.getPos().getX() + rand.nextFloat();
                            double y = te.getPos().getY() + rand.nextFloat();
                            double z = te.getPos().getZ() + rand.nextFloat();

                            playParticles(x, y, z);
                        }
                    }
                }
            }
        }

        Vector3 pos;
        for (int i = 0; i < 10; i++) {
            pos = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            playParticles(pos.getX(), pos.getY(), pos.getZ());
        }

        if (rand.nextInt(4) == 0) {
            Vector3 rand1 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            Vector3 rand2 = Vector3.random().normalize().multiply(rand.nextFloat() * range).add(position);
            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(rand1)
                    .makeDefault(rand2)
                    .color(VFXColorFunction.WHITE);
        }
    }

    @Nonnull
    public CompoundNBT serializeNBT() {
        CompoundNBT out = new CompoundNBT();
        NBTHelper.writeBlockPosToNBT(this.position, out);
        out.putFloat("range", this.range);
        out.put("targetController", this.targetController.serializeNBT());
        return out;
    }

    @Nonnull
    public static TimeStopEffectHelper deserializeNBT(CompoundNBT cmp) {
        BlockPos at = NBTHelper.readBlockPosFromNBT(cmp);
        float range = cmp.getFloat("range");
        return new TimeStopEffectHelper(at, range, TimeStopZone.EntityTargetController.deserializeNBT(cmp.getCompound("targetController")));
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
