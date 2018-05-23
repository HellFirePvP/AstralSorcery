/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: UISextantTarget
 * Created by HellFirePvP
 * Date: 23.04.2018 / 17:40
 */
public class UISextantTarget {

    private World world;
    private BlockPos pos;
    private SextantFinder.TargetObject sextantTarget;

    private UISextantTarget(World world, BlockPos target, SextantFinder.TargetObject sextantTarget) {
        this.world = world;
        this.pos = target;
        this.sextantTarget = sextantTarget;
    }

    public static UISextantTarget initialize(World world, BlockPos actualTarget, SextantFinder.TargetObject sextantTarget) {
        UISextantTarget target = new UISextantTarget(world, actualTarget, sextantTarget);
        return target;
    }

    public void renderStar(float pTicks) {
        if(Minecraft.getMinecraft().world == null) {
            return;
        }
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().player;
        }
        if(e == null) {
            return;
        }
        float dayMultiplier = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
        if(dayMultiplier <= 0.1F) {
            EffectHandler.getInstance().resetSextantTarget();
            return;
        }
        //Flattened distance
        Vector3 dir = new Vector3(pos).setY(0).subtract(Vector3.atEntityCenter(e).setY(0));
        //length, yaw, pitch
        Vector3 polar = dir.clone().copyToPolar();
        if(polar.getX() <= 20D) {
            EffectHandler.getInstance().resetSextantTarget();
            return;
        }
        double yaw = 180D - polar.getZ();
        double pitch = polar.getX() >= 350D ? -20D : Math.min(-20D, -20D - (70D - (70D * (polar.getX() / 350D))));
        Vector3 act = new Vector3(Vec3d.fromPitchYaw((float) pitch, (float) yaw)).normalize().multiply(200);
        act.add(Vector3.atEntityCenter(e));

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableAlpha();
        float alpha = RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), pTicks, 16);
        alpha = (0.4F + 0.6F * alpha) * dayMultiplier;
        Color c = new Color(sextantTarget.getColorTheme(), false);
        GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, alpha);
        RenderAstralSkybox.TEX_STAR_1.bind();
        RenderingUtils.renderFacingFullQuad(act.getX(), act.getY(), act.getZ(), pTicks, 7F, 0);
        TextureHelper.refreshTextureBindState();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public World getWorld() {
        return world;
    }
}
