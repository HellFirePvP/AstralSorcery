package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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
    public void renderTileEntityAt(TileRitualPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack i = te.getStackInSlot(0);
        if(i != null && i.getItem() != null) {
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
                TESRCollectorCrystal.renderTileLightEffects(x, y + 0.5, z, 1F, c, sBase);

                GL11.glTranslated(x + 0.5, y + 0.7, z + 0.5);
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
