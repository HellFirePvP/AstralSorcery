/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderUtil {

    public static void draw(VertexFormat.Mode drawMode, VertexFormat format, Supplier<ShaderInstance> shader, Consumer<BufferBuilder> fn) {
        draw(drawMode, format, shader, bufferBuilder -> {
            fn.accept(bufferBuilder);
            return null;
        });
    }

    public static <R> R draw(VertexFormat.Mode drawMode, VertexFormat format, Supplier<ShaderInstance> shader, Function<BufferBuilder, R> fn) {
        ShaderInstance prev = RenderSystem.getShader();
        RenderSystem.setShader(shader);
        BufferBuilder buf = Tesselator.getInstance().begin(drawMode, format);
        R result = fn.apply(buf);
        finishDrawing(buf);
        RenderSystem.setShader(() -> prev);
        return result;
    }

    public static void finishDrawing(BufferBuilder buf) {
        finishDrawing(buf, null);
    }

    public static void finishDrawing(BufferBuilder buf, @Nullable RenderType type) {
        if (type != null) {
            type.draw(buf.buildOrThrow());
        } else {
            BufferUploader.drawWithShader(buf.buildOrThrow());
        }
    }

    public static void finishDrawing(MultiBufferSource src) {
        if (src instanceof MultiBufferSource.BufferSource bufSrc) {
            bufSrc.endBatch();
        } else {
            src.getBuffer(RenderType.solid());
            src.getBuffer(RenderType.cutout());
        }
    }

    public static void withTarget(@Nullable RenderTarget target, Consumer<RenderTarget> fn) {
        fn.accept(Objects.requireNonNullElseGet(target, () -> Minecraft.getInstance().getMainRenderTarget()));
    }

    public static void safeCopyDepth(RenderTarget dst, RenderTarget src) {
        dst.copyDepthFrom(src);
        dst.bindWrite(false);
    }

    public static GuiGraphics copy(GuiGraphics guiGraphics) {
        GuiGraphics copy = new GuiGraphics(Minecraft.getInstance(), guiGraphics.bufferSource());
        copy.pose().mulPose(guiGraphics.pose().last().pose());
        return copy;
    }
}
