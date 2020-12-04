/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.technical;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityGrapplingHook
 * Created by HellFirePvP
 * Date: 29.02.2020 / 18:17
 */
public class EntityGrapplingHook extends ThrowableEntity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Integer> PULLING_ENTITY = EntityDataManager.createKey(EntityGrapplingHook.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> PULLING = EntityDataManager.createKey(EntityGrapplingHook.class, DataSerializers.BOOLEAN);

    private boolean launchedThrower = false;

    //Non-moving handling
    private int timeout = 0;
    private int previousDist = 0;

    public int despawning = -1;
    public float pullFactor = 0.0F;

    private LivingEntity throwingEntity;

    public EntityGrapplingHook(World world) {
        super(EntityTypesAS.GRAPPLING_HOOK, world);
    }

    public EntityGrapplingHook(LivingEntity thrower, World world) {
        super(EntityTypesAS.GRAPPLING_HOOK, thrower, world);
        this.shoot(Vector3.directionFromYawPitch(thrower.rotationYaw, thrower.rotationPitch), 1.5F);
        this.throwingEntity = thrower;
    }

    public static EntityType.IFactory<EntityGrapplingHook> factory() {
        return (spawnEntity, world) -> new EntityGrapplingHook(world);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(PULLING, false);
        this.dataManager.register(PULLING_ENTITY, -1);
    }

    public void setPulling(boolean pull, @Nullable LivingEntity hit) {
        this.dataManager.set(PULLING, pull);
        this.dataManager.set(PULLING_ENTITY, hit == null ? -1 : hit.getEntityId());
    }

    public boolean isPulling() {
        return this.dataManager.get(PULLING);
    }

    @Nullable
    public LivingEntity getPulling() {
        int idPull = this.dataManager.get(PULLING_ENTITY);
        if (idPull > 0) {
            try {
                return (LivingEntity) this.world.getEntityByID(idPull);
            } catch (Exception exc) {}
        }
        return null;
    }

    //0 = none, 1=basically gone
    public float despawnPercentage(float partial) {
        float p = despawning - (1 - partial);
        p /= 10;
        return MathHelper.clamp(p, 0, 1);
    }

    public boolean isDespawning() {
        return despawning != -1;
    }

    private void setDespawning() {
        if (despawning == -1) {
            despawning = 0;
        }
    }

    private void despawnTick() {
        despawning++;
        if (despawning > 10) {
            remove();
        }
    }

    @Nullable
    @Override
    public Entity func_234616_v_() {
        return this.throwingEntity != null ? this.throwingEntity : super.func_234616_v_();
    }

    @Override
    protected float getGravityVelocity() {
        return this.isPulling() ? 0 : 0.03F;
    }

    @Override
    public void tick() {
        super.tick();

        if (func_234616_v_() == null || !func_234616_v_().isAlive()) {
            setDespawning();
        }
        if (!isPulling() && ticksExisted >= 30) {
            setDespawning();
        }

        if (world.isRemote()) {
            if (!isPulling()) {
                this.pullFactor += 0.02F;
            } else {
                this.pullFactor *= 0.7F;
            }
        }

        if (isDespawning()) {
            despawnTick();

            if (world.isRemote() && this.despawning == 3) {
                this.playDespawnSparkles();
            }
        } else {
            Entity thrower = func_234616_v_();
            double dist = Math.max(0.01, thrower.getDistance(this));
            if (isAlive() && isPulling()) {
                if (getPulling() != null) {
                    LivingEntity at = getPulling();
                    this.setPosition(at.getPosX(), at.getPosY(), at.getPosZ());
                }

                if (((getPulling() != null && ticksExisted > 60 && dist < 2) || (getPulling() == null && ticksExisted > 15 && dist < 2)) || timeout > 15) {
                    setDespawning();
                } else {
                    thrower.fallDistance = -5F;

                    double mx = this.getPosX() - thrower.getPosX();
                    double my = this.getPosY() - thrower.getPosY();
                    double mz = this.getPosZ() - thrower.getPosZ();
                    mx /= dist * 5.0D;
                    my /= dist * 5.0D;
                    mz /= dist * 5.0D;
                    Vector3d v2 = new Vector3d(mx, my, mz);
                    if (v2.length() > 0.25D) {
                        v2 = v2.normalize();
                        mx = v2.x / 4.0D;
                        my = v2.y / 4.0D;
                        mz = v2.z / 4.0D;
                    }
                    Vector3d motion = thrower.getMotion();
                    motion = motion.add(mx, my + 0.04F, mz);
                    if (!launchedThrower) {
                        motion = motion.add(0, 0.4F, 0);
                        launchedThrower = true;
                    }
                    thrower.setMotion(motion);

                    int roughDst = (int) (dist / 2.5D);
                    if (roughDst >= this.previousDist) {
                        this.timeout += 1;
                    } else {
                        this.timeout = 0;
                    }
                    this.previousDist = roughDst;
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playDespawnSparkles() {
        if (!isPulling()) {
            Vector3 ePos = RenderingVectorUtils.interpolatePosition(this, 1F);
            List<Vector3> positions = buildLine(1F);
            for (Vector3 pos : positions) {
                if (rand.nextBoolean()) {
                    Vector3 motion = Vector3.random().multiply(0.005F);
                    Vector3 at = pos.add(ePos);
                    FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(at)
                            .setScaleMultiplier(0.3F + rand.nextFloat() * 0.3F)
                            .alpha(VFXAlphaFunction.FADE_OUT)
                            .color(VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE))
                            .setMotion(motion)
                            .setMaxAge(25 + rand.nextInt(20));
                    if (rand.nextBoolean()) {
                        p.color(VFXColorFunction.WHITE);
                    }
                }
            }
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        int id = -1;
        if(this.throwingEntity != null) {
            id = this.throwingEntity.getEntityId();
        }
        buffer.writeInt(id);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int id = additionalData.readInt();
        try {
            if (id > 0) {
                this.throwingEntity = (LivingEntity) world.getEntityByID(id);
            }
        } catch (Exception ignored) {}
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 64D;
        if (Double.isNaN(d0)) {
            d0 = 64D;
        }
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    public List<Vector3> buildLine(float partial) {
        Entity thrower = func_234616_v_();
        if (thrower == null) {
            return Collections.emptyList();
        }

        List<Vector3> list = Lists.newLinkedList();
        Vector3 interpThrower = RenderingVectorUtils.interpolatePosition(thrower, partial);
        Vector3 interpHook = RenderingVectorUtils.interpolatePosition(this, partial);
        Vector3 origin = new Vector3();
        Vector3 to = interpThrower.clone().subtract(interpHook).addY(thrower.getHeight() / 4);
        float lineLength = (float) (to.length() * 5);
        list.add(origin.clone());
        int iter = (int) lineLength;
        for (int xx = 1; xx < iter - 1; xx++) {
            float dist = xx * (lineLength / iter);
            double dx = (interpThrower.getX() - interpHook.getX())                            / iter * xx + MathHelper.sin(dist / 10.0F) * pullFactor;
            double dy = (interpThrower.getY() - interpHook.getY() + thrower.getHeight() / 2F) / iter * xx + MathHelper.sin(dist / 7.0F)  * pullFactor;
            double dz = (interpThrower.getZ() - interpHook.getZ())                            / iter * xx + MathHelper.sin(dist / 2.0F)  * pullFactor;
            list.add(new Vector3(dx, dy, dz));
        }
        list.add(to.clone());

        return list;
    }

    public void shoot(Vector3 dir, float velocity) {
        super.shoot(dir.getX(), dir.getY(), dir.getZ(), velocity, 0F);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, 0F);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        Vector3d hit = result.getHitVec();
        switch (result.getType()) {
            case BLOCK:
                setPulling(true, null);
                break;
            case ENTITY:
                Entity e = ((EntityRayTraceResult) result).getEntity();
                if (!(e instanceof LivingEntity) || (func_234616_v_() != null && e.equals(func_234616_v_()))) {
                    return;
                }
                setPulling(true, (LivingEntity) ((EntityRayTraceResult) result).getEntity());
                hit = new Vector3d(hit.x, hit.y + ((EntityRayTraceResult) result).getEntity().getHeight() * 3 / 4, hit.z);
                break;
            default:
                break;
        }
        this.setMotion(0, 0, 0);
        this.setPosition(hit.x, hit.y, hit.z);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
