/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 20:07
 */
public class TESRRitualPedestal extends TileEntitySpecialRenderer<TileRitualPedestal> {

    @Override
    public void render(TileRitualPedestal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        renderCrystalStack(te, x, y, z);

        if(te.shouldDoAdditionalEffects()) {
            renderEffects(te);

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_ALPHA_TEST);

            IConstellation c = te.getDisplayConstellation();
            if(c != null) {
                float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(te.getWorld());
                alphaDaytime *= 0.8F;

                int max = 5000;
                int t = (int) (ClientScheduler.getClientTick() % max);
                float halfAge = max / 2F;
                float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                tr *= 2;

                int tick = te.getEffectWorkTick();
                float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);

                RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

                float br = 0.6F * (alphaDaytime * percRunning);

                RenderConstellation.renderConstellationIntoWorldFlat(c, c.getConstellationColor(), new Vector3(te).add(0.5, 0.04, 0.5), 3 + tr, 2, 0.1F + br);
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopMatrix();
        }
        GL11.glPopAttrib();
    }

    private void renderEffects(TileRitualPedestal te) {
        int tick = te.getEffectWorkTick();
        float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);
        if(percRunning > 1E-4) {
            TextureSpritePlane sprite = te.getHaloEffectSprite();
            float alphaMul = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
            sprite.setAlphaMultiplier(percRunning * alphaMul);
        }
    }

    private void renderCrystalStack(TileRitualPedestal te, double x, double y, double z) {
        ItemStack i = te.getInventoryHandler().getStackInSlot(0);
        if(!i.isEmpty()) {
            Item it = i.getItem();
            if(it instanceof ItemTunedCrystalBase) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPushMatrix();
                boolean celestial = it instanceof ItemTunedCelestialCrystal;
                Color c = celestial ? BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor : BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor;
                long sBase = 1553015L;
                sBase ^= (long) te.getPos().getX();
                sBase ^= (long) te.getPos().getY();
                sBase ^= (long) te.getPos().getZ();
                RenderingUtils.renderLightRayEffects(x + 0.5, y + 1.3, z + 0.5, c, sBase, ClientScheduler.getClientTick(), 20, 50, 25);

                GL11.glTranslated(x + 0.5, y + 1, z + 0.5);
                GL11.glScaled(0.6, 0.6, 0.6);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                TESRCollectorCrystal.renderCrystal(celestial, true);

                GL11.glPopMatrix();
                GL11.glPopAttrib();
            }
        }
    }

}
