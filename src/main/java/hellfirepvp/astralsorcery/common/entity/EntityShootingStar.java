/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXScaleFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.data.ASDataSerializers;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 26.11.2020 / 19:30
 */
public class EntityShootingStar extends ThrowableEntity {

    private static final DataParameter<Long> EFFECT_SEED = EntityDataManager.createKey(EntityShootingStar.class, ASDataSerializers.LONG);

    protected EntityShootingStar(World worldIn) {
        super(EntityTypesAS.SHOOTING_STAR, worldIn);
        this.dataManager.set(EFFECT_SEED, rand.nextLong());
    }

    protected EntityShootingStar(double x, double y, double z, World worldIn) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public static EntityType.IFactory<EntityShootingStar> factory() {
        return (type, world) -> new EntityShootingStar(world);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(EFFECT_SEED, 0L);
    }

    public long getEffectSeed() {
        return this.dataManager.get(EFFECT_SEED);
    }

    @Override
    public void tick() {
        adjustMotion();

        super.tick();

        if (world.isRemote()) {
            spawnEffects();
        }
    }

    private void adjustMotion() {
        Vector3d motion = getMotion();
        double y = Math.min(-0.7F, motion.getY());
        setMotion(new Vector3d(motion.getX(), y, motion.getZ()));
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnEffects() {
        float maxRenderPosDist = 96F;

        VFXRenderOffsetFunction<FXFacingParticle> renderFn = (fx, iPos, pTicks) -> {
            PlayerEntity pl = Minecraft.getInstance().player;
            if (pl == null) {
                return iPos;
            }
            Vector3 v = fx.getPosition().clone().subtract(Vector3.atEntityCorner(pl));
            if (v.length() <= maxRenderPosDist) {
                return iPos;
            }
            return Vector3.atEntityCorner(pl).add(v.normalize().multiply(maxRenderPosDist));
        };
        VFXScaleFunction<EntityVisualFX> scaleFn = (fx, scaleIn, pTicks) -> {
            PlayerEntity pl = Minecraft.getInstance().player;
            if (pl == null) {
                return scaleIn;
            }
            Vector3 v = fx.getPosition().clone().subtract(Vector3.atEntityCorner(pl));
            float mul = v.length() <= maxRenderPosDist ? 1 : (float) (maxRenderPosDist / (v.length()));
            return (scaleIn * 0.25F) + ((mul * scaleIn) - (scaleIn * 0.25F));
        };

        Vector3 thisPosition = Vector3.atEntityCorner(this);
        for (int i = 0; i < 4; i++) {
            if (rand.nextFloat() > 0.75F) continue;
            Vector3 dir = new Vector3(this.getMotion()).clone().multiply(rand.nextFloat() * -0.6F);
            dir.setX(dir.getX() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));
            dir.setZ(dir.getZ() + rand.nextFloat() * 0.008 * (rand.nextBoolean() ? 1 : -1));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(thisPosition)
                    .color(VFXColorFunction.WHITE)
                    .setMotion(dir)
                    .setAlphaMultiplier(0.85F)
                    .setScaleMultiplier(1.2F + rand.nextFloat() * 0.5F)
                    .scale(VFXScaleFunction.SHRINK.andThen(scaleFn))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .renderOffset(renderFn)
                    .setMaxAge(90 + rand.nextInt(40));
        }

        float scale = 4F + rand.nextFloat() * 3F;
        int age = 5 + rand.nextInt(2);
        Random effectSeed = new Random(this.getEffectSeed());

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(thisPosition)
                .color(VFXColorFunction.constant(Color.getHSBColor(effectSeed.nextFloat() * 360F, 1F, 1F)))
                .setScaleMultiplier(scale)
                .scale(VFXScaleFunction.SHRINK.andThen(scaleFn))
                .renderOffset(renderFn)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setMaxAge(age);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(thisPosition)
                .color(VFXColorFunction.WHITE)
                .setScaleMultiplier(scale * 0.6F)
                .scale(VFXScaleFunction.SHRINK.andThen(scaleFn))
                .renderOffset(renderFn)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setMaxAge(Math.round(age * 1.5F));
    }

    @Override
    public void setPosition(double x, double y, double z) {
        int chunkX = MathHelper.floor(this.getPosX() / 16.0D);
        int chunkZ = MathHelper.floor(this.getPosZ() / 16.0D);
        int newChunkX = MathHelper.floor(x / 16.0D);
        int newChunkZ = MathHelper.floor(z / 16.0D);
        if (chunkX != newChunkX || chunkZ != newChunkZ) {
            if (!this.getEntityWorld().chunkExists(newChunkX, newChunkZ)) {
                this.remove();
                return;
            }
        }
        super.setPosition(x, y, z);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
