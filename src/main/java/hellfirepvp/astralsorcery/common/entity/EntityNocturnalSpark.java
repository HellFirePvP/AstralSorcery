/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityNocturnalSpark
 * Created by HellFirePvP
 * Date: 17.08.2019 / 08:59
 */
public class EntityNocturnalSpark extends ThrowableEntity {

    private static final AxisAlignedBB NO_DUPE_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1).grow(15);

    private static final DataParameter<Boolean> SPAWNING = EntityDataManager.createKey(EntityNocturnalSpark.class, DataSerializers.BOOLEAN);
    private int ticksSpawning = 0;

    public EntityNocturnalSpark(World world) {
        super(EntityTypesAS.NOCTURNAL_SPARK, world);
    }

    public EntityNocturnalSpark(double x, double y, double z, World world) {
        super(EntityTypesAS.NOCTURNAL_SPARK, x, y, z, world);
    }

    public EntityNocturnalSpark(LivingEntity thrower, World world) {
        super(EntityTypesAS.NOCTURNAL_SPARK, thrower, world);
        this.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0F, 0.7F, 0.9F);
    }

    public static EntityType.IFactory<EntityNocturnalSpark> factory() {
        return (type, world) -> new EntityNocturnalSpark(world);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(SPAWNING, false);
    }

    public void setSpawning() {
        this.setMotion(Vec3d.ZERO);
        this.dataManager.set(SPAWNING, true);
    }

    public boolean isSpawning() {
        return this.dataManager.get(SPAWNING);
    }

    @Override
    public void tick() {
        super.tick();

        if (!isAlive()) {
            return;
        }

        if (!world.isRemote()) {
            removeLights();
            if (isSpawning()) {
                ticksSpawning++;
                spawnCycle();
                removeDuplicates();

                if (ticksSpawning > 200) {
                    remove();
                }
            }
        } else {
            spawnEffects();
        }
    }

    private void removeLights() {
        if (this.getEntityWorld() instanceof ServerWorld) {
            ServerWorld sWorld = (ServerWorld) this.getEntityWorld();
            if (this.ticksExisted % 5 == 0) {
                List<BlockPos> lightPositions = BlockDiscoverer.searchForBlocksAround(
                        sWorld, this.getPosition(), 8,
                        (world, pos, state) -> !(state.getBlock() instanceof AirBlock) && state.getLightValue(world, pos) > 3);
                for (BlockPos light : lightPositions) {
                    if (!BlockUtils.breakBlockWithoutPlayer(sWorld, light, sWorld.getBlockState(light), ItemStack.EMPTY, true, true, true)) {
                        sWorld.removeBlock(light, false);
                    }
                }
            }
        }
    }

    private void removeDuplicates() {
        List<EntityNocturnalSpark> sparks = world.getEntitiesWithinAABB(EntityNocturnalSpark.class, NO_DUPE_BOX.offset(getPosition()));
        for (EntityNocturnalSpark spark : sparks) {
            if (this.equals(spark)) {
                continue;
            }
            if (!spark.isAlive() || !spark.isSpawning()) {
                continue;
            }
            spark.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnEffects() {
        if (isSpawning()) {
            for (int i = 0; i < 15; i++) {
                Vector3 thisPos = Vector3.atEntityCorner(this).addY(1);
                MiscUtils.applyRandomOffset(thisPos, rand, 2 + rand.nextInt(4));
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(thisPos)
                        .setScaleMultiplier(4)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .setAlphaMultiplier(0.7F)
                        .color(VFXColorFunction.constant(Color.BLACK));
                if (rand.nextInt(5) == 0) {
                    randomizeColor(p);
                }
                if (rand.nextInt(3) == 0) {
                    Vector3 target = Vector3.atEntityCorner(this);
                    MiscUtils.applyRandomOffset(target, rand, 4);
                    EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                            .spawn(Vector3.atEntityCorner(this))
                            .makeDefault(target)
                            .color(VFXColorFunction.constant(Color.BLACK));
                }
            }
        } else {
            FXFacingParticle p;
            for (int i = 0; i < 6; i++) {
                p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(Vector3.atEntityCorner(this))
                        .setMotion(new Vector3(
                            0.04F - rand.nextFloat() * 0.08F,
                            0.04F - rand.nextFloat() * 0.08F,
                            0.04F - rand.nextFloat() * 0.08F
                        ))
                        .setScaleMultiplier(0.25F);
                randomizeColor(p);
            }

            p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this));
            p.setScaleMultiplier(0.6F);
            randomizeColor(p);

            p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this).add(getMotion().mul(0.5, 0.5, 0.5)));
            p.setScaleMultiplier(0.6F);
            randomizeColor(p);
        }
    }

    private void spawnCycle() {
        if (rand.nextInt(12) == 0 && world instanceof ServerWorld) {
            BlockPos pos = getPosition().up();
            pos.add(rand.nextInt(2) - rand.nextInt(2),
                    rand.nextInt(1) - rand.nextInt(1),
                    rand.nextInt(2) - rand.nextInt(2));

            EntityUtils.performWorldSpawningAt((ServerWorld) world, pos, EntityClassification.MONSTER, SpawnReason.SPAWNER, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void randomizeColor(FXFacingParticle p) {
        switch (rand.nextInt(3)) {
            case 0:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_1));
                break;
            case 1:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_2));
                break;
            case 2:
                p.color(VFXColorFunction.constant(ColorsAS.NOCTURNAL_POWDER_3));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (RayTraceResult.Type.ENTITY.equals(result.getType())) {
            return;
        }
        Vec3d hit = result.getHitVec();
        this.setSpawning();
        this.setPosition(hit.x, hit.y, hit.z);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
