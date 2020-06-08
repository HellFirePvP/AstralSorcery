package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IDrawRenderTypeBuffer
 * Created by HellFirePvP
 * Date: 06.06.2020 / 09:38
 */
public interface IDrawRenderTypeBuffer extends IRenderTypeBuffer {

    public void draw();

    public void draw(RenderType type);

    public static IDrawRenderTypeBuffer defaultBuffer() {
        return of(IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer()));
    }

    public static IDrawRenderTypeBuffer of(IRenderTypeBuffer.Impl drawBuffer) {
        return new IDrawRenderTypeBuffer() {
            @Override
            public void draw() {
                drawBuffer.finish();
            }

            @Override
            public void draw(RenderType type) {
                drawBuffer.finish(type);
            }

            @Override
            public IVertexBuilder getBuffer(RenderType renderType) {
                return drawBuffer.getBuffer(renderType);
            }
        };
    }

}
