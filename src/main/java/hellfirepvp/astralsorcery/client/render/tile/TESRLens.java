package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASlens;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:07
 */
public class TESRLens extends TileEntitySpecialRenderer<TileCrystalLens> {

    private static final ASlens modelLensPart = new ASlens();
    private static final BindableResource texLensPart = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/lens");

    @Override
    public void renderTileEntityAt(TileCrystalLens te, double x, double y, double z, float partialTicks, int destroyStage) {
        List<BlockPos> linked = te.getLinkedPositions();
        float yaw = 0; //Degree
        float pitch = 0; //Degree
        if(!linked.isEmpty() && linked.size() == 1) {
            BlockPos to = linked.get(0);
            BlockPos from = te.getTrPos();
            Vector3 dir = new Vector3(to).subtract(new Vector3(from));

            pitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

            yaw = (float) Math.atan2(dir.getX(), dir.getZ());

            yaw = 180F + (float) Math.toDegrees(-yaw);
            pitch = (float) Math.toDegrees(pitch);
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(x + 0.5, y + 1.335, z + 0.5);
        GL11.glScaled(0.055, 0.055, 0.055);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glRotated(yaw % 360, 0, 1, 0);
        renderHandle(yaw, pitch);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderHandle(float yaw, float pitch) {
        texLensPart.bind();
        modelLensPart.render(null, yaw, pitch, 0, 0, 0, 1);
    }

}
