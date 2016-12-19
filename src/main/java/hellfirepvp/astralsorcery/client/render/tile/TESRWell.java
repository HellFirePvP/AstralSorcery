package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 16:25
 */
public class TESRWell extends TileEntitySpecialRenderer<TileWell> {

    @Override
    public void renderTileEntityAt(TileWell te, double x, double y, double z, float partialTicks, int destroyStage) {
        ItemStack catalyst = te.getInventoryHandler().getStackInSlot(0);
        if(catalyst != null) {
            EntityItem ei = new EntityItem(Minecraft.getMinecraft().world, 0, 0, 0, catalyst);
            ei.age = te.getTicksExisted();
            ei.hoverStart = 0;
            Minecraft.getMinecraft().getRenderManager().doRenderEntity(ei, x + 0.5, y + 0.8, z + 0.5, 0, partialTicks, true);
        }
        if(te.getLiquidStarlightAmount() > 0) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            RenderHelper.disableStandardItemLighting();
            Vector3 offset = new Vector3(te).add(0.5D, 0.32D, 0.5D);
            offset.addY(te.getPercFilled() * 0.6);
            SpriteSheetResource sprite = SpriteLibrary.spriteLiquidStarlight;
            sprite.getResource().bind();
            Tuple<Double, Double> uvOffset = sprite.getUVOffset(ClientScheduler.getClientTick());
            RenderingUtils.renderAngleRotatedTexturedRect(offset, Vector3.RotAxis.Y_AXIS.clone(), Math.toRadians(45), 0.54, uvOffset.key, uvOffset.value, sprite.getULength(), sprite.getVLength(), partialTicks);
            TextureHelper.refreshTextureBindState();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }
    }

}
