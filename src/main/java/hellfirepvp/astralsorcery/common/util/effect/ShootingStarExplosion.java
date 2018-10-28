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
import hellfirepvp.astralsorcery.client.effect.light.EffectLightning;
import hellfirepvp.astralsorcery.common.base.ShootingStarHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ShootingStarExplosion
 * Created by HellFirePvP
 * Date: 21.10.2018 / 10:12
 */
public class ShootingStarExplosion {

    public static void play(World world, double x, double y, double z, boolean extinguished, long seed) {
        play(world, new Vector3(x, y, z), extinguished, seed);
    }

    public static void play(World world, Vector3 pos, boolean extinguished, long seed) {
        if (!extinguished && ShootingStarHandler.StarConfigEntry.doExplosion) {
            boolean doDamage = world.getGameRules().getBoolean("mobGriefing");
            world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.5F, false, doDamage);
        }

        PktParticleEvent ev;
        if (extinguished) {
            ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.SH_STAR_EX, pos);
        } else {
            ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.SH_STAR, pos);
        }
        ev.setAdditionalDataLong(seed);
        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, pos.toBlockPos(), 128));
    }

    @SideOnly(Side.CLIENT)
    public static void playEffects(PktParticleEvent event) {
        playParticles(event.getVec(), false, event.getAdditionalDataLong());
    }

    @SideOnly(Side.CLIENT)
    public static void playExEffects(PktParticleEvent event) {
        playParticles(event.getVec(), true, event.getAdditionalDataLong());
    }

    @SideOnly(Side.CLIENT)
    private static void playParticles(Vector3 pos, boolean extinguished, long seed) {
        Random r = new Random(seed);
        Color c = Color.getHSBColor(r.nextFloat() * 360F, 1F, 1F);
        if (!extinguished) {
            for (int i = 0; i < 14; i++) {
                Vector3 randTo = new Vector3(
                        r.nextFloat() * 8 * (r.nextBoolean() ? 1 : -1),
                        r.nextFloat() * 5,
                        r.nextFloat() * 8 * (r.nextBoolean() ? 1 : -1));
                EffectLightning lightning = EffectHandler.getInstance().lightning(pos, pos.clone().add(randTo));
                lightning.setBuildSpeed(0.12F);
                switch (r.nextInt(3)) {
                    case 0:
                        lightning.setOverlayColor(c);
                        break;
                    case 1:
                        lightning.setOverlayColor(c.brighter().brighter());
                        break;
                    case 2:
                        lightning.setOverlayColor(Color.WHITE);
                        break;
                }
            }

            Vector3 perp = Vector3.RotAxis.Y_AXIS.clone().perpendicular().normalize();
            for (double i = 0; i <= 360; i += 0.9) {
                Vector3 dir = perp.clone().rotate(Math.toRadians(i), Vector3.RotAxis.Y_AXIS).normalize();
                Vector3 p = dir.clone().multiply(3 + r.nextDouble() * 4).add(pos);
                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(p.getX(), p.getY(), p.getZ());
                dir.multiply(0.06);
                particle
                        .motion(
                                dir.getX() + r.nextDouble() * 0.005,
                                dir.getY() + 0.01 + r.nextDouble() * 0.01,
                                dir.getZ() + r.nextDouble() * 0.005)
                        .setColor(Color.BLUE)
                        .scale(1.5F)
                        .gravity(0.004)
                        .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                        .setMaxAge(55 + r.nextInt(20));
                particle.setAlphaMultiplier(0.4F).setColor(r.nextBoolean() ? Color.WHITE : c);
            }
        }

        for (int i = 0; i < 70; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos);
            particle
                    .offset(
                            r.nextFloat() * 0.5 * (r.nextBoolean() ? 1 : -1),
                            r.nextFloat() * 0.5 * (r.nextBoolean() ? 1 : -1),
                            r.nextFloat() * 0.5 * (r.nextBoolean() ? 1 : -1))
                    .motion(
                            (r.nextBoolean() ? 0.2 : 1) * r.nextFloat() * 0.07 * (r.nextBoolean() ? 1 : -1),
                            (r.nextBoolean() ? 0.2 : 1) * r.nextFloat() * 0.07 * (r.nextBoolean() ? 1 : -1),
                            (r.nextBoolean() ? 0.2 : 1) * r.nextFloat() * 0.07 * (r.nextBoolean() ? 1 : -1))
                    .scale(0.8F + r.nextFloat() * 0.4F)
                    .gravity(0.004)
                    .setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                    .setMaxAge(80 + r.nextInt(20));
            if(!extinguished && r.nextInt(3) == 0) {
                particle.setColor(c);
            }
        }
    }

}
