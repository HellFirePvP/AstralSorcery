/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFlare
 * Created by HellFirePvP
 * Date: 07.02.2017 / 12:15
 */
public class EntityFlare extends EntityFlying {

    private static final int strollRange = 31;

    public Object texSprite = null;
    private BlockPos moveTarget = null;

    public EntityFlare(World worldIn) {
        super(worldIn);
        setSize(0.7F, 0.7F);
    }

    public EntityFlare(World worldIn, double x, double y, double z) {
        super(worldIn);
        this.setPosition(x, y, z);
        setSize(0.7F, 0.7F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote) {
            if(texSprite == null) {
                setupSprite();
            }
            clientUpdate();
        } else {
            if(moveTarget == null || getDistanceSq(moveTarget) < 5D || rand.nextInt(70) == 0) {
                moveTarget = getPosition().add(-strollRange / 2, -strollRange / 2, -strollRange / 2).add(rand.nextInt(strollRange), rand.nextInt(strollRange), rand.nextInt(strollRange));
            }
            if(moveTarget != null && (moveTarget.getY() <= 1 || !world.isAirBlock(moveTarget))) {
                moveTarget = null;
            }
            if(moveTarget != null) {
                this.motionX += (Math.signum(moveTarget.getX() + 0.5D - this.posX) * 0.5D - this.motionX) * 0.01D;
                this.motionY += (Math.signum(moveTarget.getY() + 0.5D - this.posY) * 0.7D - this.motionY) * 0.01D;
                this.motionZ += (Math.signum(moveTarget.getZ() + 0.5D - this.posZ) * 0.5D - this.motionZ) * 0.01D;
                this.moveForward = 0.2F;
            }
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    protected void onDeathUpdate() {
        ++this.deathTime;

        if(deathTime == 20) {

            setDead();

            if(world.isRemote) {
                deathEffectsEnd();
            }
        }
        if(world.isRemote) {
            deathEffectsOngoing();
        }
    }

    @SideOnly(Side.CLIENT)
    private void deathEffectsOngoing() {
        for (int i = 0; i < 3; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.motion(-0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1, -0.05 + rand.nextFloat() * 0.1);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void deathEffectsEnd() {
        EntityFXFacingSprite p = (EntityFXFacingSprite) texSprite;
        p.requestRemoval();
        for (int i = 0; i < 29; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.motion(-0.1 + rand.nextFloat() * 0.2, -0.1 + rand.nextFloat() * 0.2, -0.1 + rand.nextFloat() * 0.2);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
        for (int i = 0; i < 15; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.offset(-0.2 + rand.nextFloat() * 0.4, -0.2 + rand.nextFloat() * 0.4, -0.2 + rand.nextFloat() * 0.4);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(0.004);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientUpdate() {
        EntityFXFacingSprite p = (EntityFXFacingSprite) texSprite;
        if(p.isRemoved()) {
            EffectHandler.getInstance().registerFX(p);
        }

        if(rand.nextBoolean()) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(posX, posY, posZ);
            particle.offset(-0.3 + rand.nextFloat() * 0.6, -0.3 + rand.nextFloat() * 0.6, -0.3 + rand.nextFloat() * 0.6);
            particle.scale(0.1F + rand.nextFloat() * 0.2F).gravity(-0.02);
            if(rand.nextBoolean()) {
                particle.setColor(Color.WHITE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void setupSprite() {
        EntityFXFacingSprite p = EntityFXFacingSprite.fromSpriteSheet(SpriteLibrary.spriteFlare1, posX, posY, posZ, 1F, 2);
        p.setPositionUpdateFunction((v) -> new Vector3(this));
        p.setRefreshFunc(() -> !isDead);
        EffectHandler.getInstance().registerFX(p);
        this.texSprite = p;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return Math.max(127, super.getBrightnessForRender(partialTicks));
    }

}
