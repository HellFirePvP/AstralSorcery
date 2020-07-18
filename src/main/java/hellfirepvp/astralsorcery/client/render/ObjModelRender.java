/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObjModelRender
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:59
 */
public class ObjModelRender {

    private static WavefrontObject crystalModel;
    //private static VertexBuffer vboCrystal;

    private static WavefrontObject celestialWingsModel;
    private static VertexBuffer vboCelestialWings;

    private static WavefrontObject wraithWingsModel;
    private static VertexBuffer wraithWingsBones, wraithWingsWing;

    public static void renderCrystal(MatrixStack renderStack, IVertexBuilder buf, Runnable drawFn) {
        if (crystalModel == null) {
            crystalModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "crystal");
        }
        //if (vboCrystal == null) {
        //    int[] transparent = new int[] { 255, 255, 255, 65 };
        //    BufferDecoratorBuilder.withColor((r, g, b, a) -> transparent)
        //            .decorate(Tessellator.getInstance().getBuffer(),
        //                    (BufferBuilder decorated) -> vboCrystal = crystalModel.batch(decorated));
        //}

        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(renderStack.getLast().getMatrix());
        crystalModel.render(buf);
        drawFn.run();
        RenderSystem.popMatrix();

        //vboCrystal.bindBuffer();
        //DefaultVertexFormats.POSITION_COLOR_TEX.setupBufferState(0L);
        //vboCrystal.draw(renderStack.getLast().getMatrix(), crystalModel.getGLDrawingMode());
        //DefaultVertexFormats.POSITION_COLOR_TEX.clearBufferState();
        //VertexBuffer.unbindBuffer();
    }

    public static void renderCelestialWings(MatrixStack renderStack) {
        if (celestialWingsModel == null) {
            celestialWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "celestial_wings");
        }
        if (vboCelestialWings == null) {
            int[] lightGray = new int[] { 178, 178, 178, 255 };
            BufferDecoratorBuilder.withColor((r, g, b, a) -> lightGray)
                    .decorate(Tessellator.getInstance().getBuffer(),
                            (BufferBuilder decorated) -> vboCelestialWings = celestialWingsModel.batch(decorated));
        }
        vboCelestialWings.bindBuffer();
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.setupBufferState(0L);
        vboCelestialWings.draw(renderStack.getLast().getMatrix(), celestialWingsModel.getGLDrawingMode());
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.clearBufferState();
        VertexBuffer.unbindBuffer();
    }

    public static void renderWraithWings(MatrixStack renderStack) {
        if (wraithWingsModel == null) {
            wraithWingsModel = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "wraith_wings");
        }

        if (wraithWingsBones == null) {
            int[] gray = new int[] { 77, 77, 77, 255 };
            BufferDecoratorBuilder.withColor((r, g, b, a) -> gray)
                    .decorate(Tessellator.getInstance().getBuffer(),
                            (BufferBuilder decorated) -> wraithWingsBones = wraithWingsModel.batchOnly(decorated, "Bones"));
        }
        if (wraithWingsWing == null) {
            int[] black = new int[] { 0, 0, 0, 255 };
            BufferDecoratorBuilder.withColor((r, g, b, a) -> black)
                    .decorate(Tessellator.getInstance().getBuffer(),
                            (BufferBuilder decorated) -> wraithWingsWing = wraithWingsModel.batchOnly(decorated, "Wing"));
        }

        wraithWingsBones.bindBuffer();
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.setupBufferState(0L);
        wraithWingsBones.draw(renderStack.getLast().getMatrix(), wraithWingsModel.getGLDrawingMode());
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.clearBufferState();
        VertexBuffer.unbindBuffer();

        wraithWingsWing.bindBuffer();
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.setupBufferState(0L);
        wraithWingsWing.draw(renderStack.getLast().getMatrix(), wraithWingsModel.getGLDrawingMode());
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.clearBufferState();
        VertexBuffer.unbindBuffer();
    }
}
