/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightning;
import hellfirepvp.astralsorcery.client.effect.texture.TexturePlane;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialStrike
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:48
 */
public class CelestialStrike {

    public static void play(@Nullable EntityLivingBase attacker, World world, Vector3 position, Vector3 displayPosition) {
        double radius = 16D;
        List<EntityLivingBase> livingEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(0, 0, 0, 0, 0, 0).expand(radius, radius / 2, radius).offset(position.toBlockPos()), EntitySelectors.IS_ALIVE);
        if(attacker != null) {
            livingEntities.remove(attacker);
        }

        DamageSource ds = CommonProxy.dmgSourceStellar;
        if(attacker != null) {
            ds = DamageSource.causeMobDamage(attacker);
            if(attacker instanceof EntityPlayer) {
                ds = DamageSource.causePlayerDamage((EntityPlayer) attacker);
            }
        }
        float dmg = 10;
        dmg += ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world) * 40F;
        dmg += SkyCollectionHelper.getSkyNoiseDistribution(world, position.toBlockPos()) * 20F;
        for (EntityLivingBase living : livingEntities) {
            if ((living instanceof EntityPlayer) &&
                    (((EntityPlayer) living).isSpectator() || ((EntityPlayer) living).isCreative() ||
                            (attacker != null && living.isOnSameTeam(attacker)))) {
                continue;
            }
            float dstPerc = (float) (Vector3.atEntityCenter(living).distance(position) / radius);
            dstPerc = 1F - MathHelper.clamp(dstPerc, 0F, 1F);
            float dmgDealt = dstPerc * dmg;
            if(dmgDealt > 0.5) {
                living.attackEntityFrom(ds, dmgDealt);
            }
        }
        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CEL_STRIKE, displayPosition);
        ev.setAdditionalData(radius);
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, position.toBlockPos(), 96));
    }

    @SideOnly(Side.CLIENT)
    public static void playEffects(PktParticleEvent pktParticleEvent) {
        Random r = new Random();
        Vector3 origin = pktParticleEvent.getVec().clone();
        EffectLightbeam beam = EffectHandler.getInstance().lightbeam(origin.clone().add(0, 16, 0), origin.clone().add(0, -4, 0), 6, 9);
        beam.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT).setAlphaMultiplier(1F).setMaxAge(25);
        origin.add(r.nextFloat() - r.nextFloat(), 0, r.nextFloat() - r.nextFloat());
        beam = EffectHandler.getInstance().lightbeam(origin.clone().add(0, 16 + r.nextFloat() * 2, 0), origin.clone().add(0, -4, 0), 6, 9);
        beam.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT).setAlphaMultiplier(1F).setColorOverlay(new Color(0x5D98D8)).setMaxAge(24 + r.nextInt( 6));
        origin.add(r.nextFloat() - r.nextFloat(), 0, r.nextFloat() - r.nextFloat());
        beam = EffectHandler.getInstance().lightbeam(origin.clone().add(0, 16, 0), origin.clone().add(0, -4, 0), 6, 9);
        beam.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT).setAlphaMultiplier(1F).setColorOverlay(new Color(0x005ABE)).setMaxAge(24 + r.nextInt( 6));

        origin = pktParticleEvent.getVec().clone();

        for (int i = 0; i < 43; i++) {
            Vector3 randTo = new Vector3((r.nextDouble() * 9) - (r.nextDouble() * 9), r.nextDouble() * 2, (r.nextDouble() * 9) - (r.nextDouble() * 9));
            EffectLightning lightning = EffectHandler.getInstance().lightning(origin, origin.clone().add(randTo));
            switch (r.nextInt(3)) {
                case 0:
                    lightning.setOverlayColor(Color.WHITE);
                    break;
                case 1:
                    lightning.setOverlayColor(new Color(0x528EC8));
                    break;
                case 2:
                    lightning.setOverlayColor(new Color(0x004899));
                    break;
            }
        }

        origin = pktParticleEvent.getVec().clone();

        TexturePlane tex = EffectHandler.getInstance().texturePlane(AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "smoke"), Vector3.RotAxis.Y_AXIS.clone().negate());
        tex.setAlphaOverDistance(false);
        tex.setAlphaMultiplier(0.6F);
        tex.setAlphaFunction(EntityComplexFX.AlphaFunction.FADE_OUT);
        tex.setMaxAge(35);
        tex.setStaticUVOffset(r.nextBoolean() ? 0 : 0.5, r.nextBoolean() ? 0 : 0.5);
        tex.setUVLength(0.5D, 0.5D);
        tex.setColorOverlay(Color.WHITE);
        tex.setPosition(origin.add(0, 0.1, 0));
        tex.setScale(17);
        tex.setNoRotation(r.nextFloat());

        for (int i = 0; i < 40; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(
                    origin.getX() + r.nextDouble() * 4 - r.nextDouble() * 4,
                    origin.getY() + r.nextDouble() * 9,
                    origin.getZ() + r.nextDouble() * 4 - r.nextDouble() * 4);
            particle.gravity(0.08).scale(0.85F).setMaxAge(15);
            switch (r.nextInt(3)) {
                case 0:
                    particle.setColor(Color.WHITE);
                    break;
                case 1:
                    particle.setColor(new Color(0x528EC8));
                    break;
                case 2:
                    particle.setColor(new Color(0x004899));
                    break;
            }
        }

        Vector3 perp = Vector3.RotAxis.Y_AXIS.clone().perpendicular().normalize();
        for (double i = 0; i <= 360; i += 1.7) {
            Vector3 dir = perp.clone().rotate(Math.toRadians(i), Vector3.RotAxis.Y_AXIS).normalize();
            Vector3 pos = dir.clone().multiply(7 + r.nextDouble() * 2).add(origin);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            dir.multiply(0.1);
            particle.motion(dir.getX() + r.nextDouble() * 0.01, dir.getY() + 0.001 + r.nextDouble() * 0.01, dir.getZ() + r.nextDouble() * 0.01).setColor(Color.BLUE).scale(1.2F).setMaxAge(15);
            particle.setAlphaMultiplier(0.4F);
        }
        for (double i = 0; i <= 360; i += 3.7) {
            Vector3 dir = perp.clone().rotate(Math.toRadians(i), Vector3.RotAxis.Y_AXIS).normalize();
            Vector3 pos = dir.clone().multiply(3 + r.nextDouble() * 2).add(origin);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            dir.multiply(0.1);
            particle.motion(dir.getX() + r.nextDouble() * 0.01, dir.getY() + 0.001 + r.nextDouble() * 0.01, dir.getZ() + r.nextDouble() * 0.01).setColor(Color.BLUE.brighter().brighter()).scale(1.5F).setMaxAge(15);
            particle.setAlphaMultiplier(0.4F);
        }

    }

}
