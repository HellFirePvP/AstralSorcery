/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.util.ASDataSerializers;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the BeeBetterAtBees Mod
 * Class: EntityShootingStar
 * Created by HellFirePvP
 * Date: 13.10.2018 / 12:54
 */
public class EntityShootingStar extends EntityThrowable implements EntityTechnicalAmbient {

    private static final DataParameter<Vector3> SHOOT_CONSTANT = EntityDataManager.createKey(EntityShootingStar.class, ASDataSerializers.VECTOR);

    //Not saved or synced value to deny 'capturing' one.
    private boolean removalPending = true;
    private long rSeed;

    public EntityShootingStar(World worldIn) {
        super(worldIn);
        this.rSeed = rand.nextLong();
    }

    public EntityShootingStar(World worldIn, double x, double y, double z, Vector3 shot) {
        super(worldIn, x, y, z);
        this.setSize(0.1F, 0.1F);
        this.dataManager.set(SHOOT_CONSTANT, shot);
        this.removalPending = false;
        this.rSeed = rand.nextLong();
        correctMovement();
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(SHOOT_CONSTANT, new Vector3());
    }

    private void correctMovement() {
        Vector3 shot = this.dataManager.get(SHOOT_CONSTANT);
        this.motionX = shot.getX();
        this.motionZ = shot.getZ();
        if (this.posY >= 500) {
            this.motionY = -0.09;
        } else {
            this.motionY = -0.7F * (1F - (((float) this.posY) / 1000F));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote && (removalPending || ConstellationSkyHandler.getInstance().isDay(world))) {
            setDead();
            return;
        }

        correctMovement();

        if (world.isRemote) {
            spawnEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnEffects() {
        Vector3 shot = this.dataManager.get(SHOOT_CONSTANT);
        float positionDist = 96F;

        EntityComplexFX.RenderOffsetController renderCtrl = (fx, currentRenderPos, currentMotion, pTicks) -> {
            EntityPlayer pl = Minecraft.getMinecraft().player;
            if (pl == null) {
                return currentRenderPos;
            }
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition().clone().subtract(Vector3.atEntityCenter(pl));
            if (v.length() <= positionDist) {
                return currentRenderPos;
            }
            return Vector3.atEntityCenter(pl).add(v.normalize().multiply(positionDist));
        };
        EntityComplexFX.ScaleFunction scaleFct = (fx, pos, pTicks, scaleIn) -> {
            EntityPlayer pl = Minecraft.getMinecraft().player;
            if (pl == null) {
                return scaleIn;
            }
            scaleIn = new EntityComplexFX.ScaleFunction.Shrink<>().getScale((EntityComplexFX) fx, pos, pTicks, scaleIn);
            EntityFXFacingParticle pt = (EntityFXFacingParticle) fx;
            Vector3 v = pt.getPosition().clone().subtract(Vector3.atEntityCenter(pl));
            float mul = v.length() <= positionDist ? 1 : (float) (positionDist / (v.length()));
            return (scaleIn * 0.25F) + ((mul * scaleIn) - (scaleIn * 0.25F));
        };

        for (int i = 0; i < 4; i++) {
            if (rand.nextFloat() > 0.65F) continue;
            Vector3 dir = shot.clone().multiply(-(0.5F + rand.nextFloat() * 0.4F));
            dir.setX(dir.getX() + rand.nextFloat() * 0.015 * (rand.nextBoolean() ? 1 : -1));
            dir.setZ(dir.getZ() + rand.nextFloat() * 0.015 * (rand.nextBoolean() ? 1 : -1));
            //dir.rotate(Math.toRadians((30 + rand.nextInt(15)) * (rand.nextBoolean() ? 1 : -1)), dir.perpendicular());
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
            p.setColor(Color.WHITE)
                    .setDistanceRemovable(false)
                    .scale(1.5F + rand.nextFloat() * 0.7F)
                    .motion(dir.getX(), dir.getY(), dir.getZ())
                    .setAlphaMultiplier(0.85F)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                    .setMaxAge(100 + rand.nextInt(40));
            //Position within view distance
            p.setRenderOffsetController(renderCtrl);
            //Make smaller if further away, not too linearly though.
            p.setScaleFunction(scaleFct);
        }

        float scale = 4F + rand.nextFloat() * 3F;
        int age = 5 + rand.nextInt(2);

        Random seeded = new Random(rSeed);
        EntityFXFacingParticle star = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        star.setColor(Color.getHSBColor(seeded.nextFloat() * 360F, 1F, 1F))
                .setDistanceRemovable(false)
                .scale(scale)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .setMaxAge(age);
        star.setRenderOffsetController(renderCtrl);
        star.setScaleFunction(scaleFct);
        EntityFXFacingParticle st2 = EffectHelper.genericFlareParticle(this.posX, this.posY, this.posZ);
        st2.setColor(Color.WHITE)
                .setDistanceRemovable(false)
                .scale(scale * 0.6F)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .setMaxAge(Math.round(age * 1.5F));
        st2.setRenderOffsetController(renderCtrl);
        st2.setScaleFunction(scaleFct);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (removalPending) {
            return;
        }

        setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }
}
