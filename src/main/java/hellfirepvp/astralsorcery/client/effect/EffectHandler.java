/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class EffectHandler {

    private static final EffectHandler INSTANCE = new EffectHandler();

    private final Map<EffectTemplate<?>, List<EntityVisualFX>> effects = new HashMap<>();
    private final List<EntitySourceFX> sources = new ArrayList<>();

    private boolean clear = false;

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return INSTANCE;
    }

    public void displayDebug(CustomizeGuiOverlayEvent.DebugText event) {
        int count = 0;
        for (List<EntityVisualFX> list : this.effects.values()) {
            count += list.size();
        }

        event.getLeft().add("");
        event.getLeft().add(ChatFormatting.BLUE + "[AstralSorcery]" + ChatFormatting.RESET + " Effects:");
        event.getLeft().add(ChatFormatting.BLUE + "[AstralSorcery]" + ChatFormatting.RESET + " > VFX: " + count);
        event.getLeft().add(ChatFormatting.BLUE + "[AstralSorcery]" + ChatFormatting.RESET + " > VFX Sources: " + this.sources.size());
    }

    public void render(Camera renderInfo, @Nullable Frustum cameraFrustum, Predicate<ParticleRenderType> renderTypePredicate, float pTicks) {
        if (AssetLibrary.isReloading()) {
            return;
        }

        MultiBufferSource.BufferSource drawBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        for (EffectTemplate template : EffectTemplatesAS.ALL_EFFECT_TEMPLATES) {
            if (!renderTypePredicate.test(template)) continue;

            List<EntityVisualFX> effects = this.effects.getOrDefault(template, Collections.emptyList()).stream()
                    .filter(e -> cameraFrustum == null || cameraFrustum.isVisible(e.getRenderBoundingBox()))
                    .toList();
            if (!effects.isEmpty()) {
                template.renderAll(effects, renderInfo, drawBuffer, pTicks);
            }
        }
    }

    //Testing lol
    /*
    @Deprecated(forRemoval = true)
    public void onPlayerRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
        float pTicks = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        MultiBufferSource.BufferSource src = Minecraft.getInstance().renderBuffers().bufferSource();
        src.endBatch();

        ColorWrapper wrapper = ColorWrapper.ofHSB((ClientProxy.getClientTick() % 80) / ((float) 80), 1F, 1F);
        ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(wrapper.copyWithAlpha(60));
        transparencyTarget.clear(Minecraft.ON_OSX);
        WrappedBufferSource srcWrap = new WrappedBufferSource(src, type -> DrawChainRenderType.wrap(type, transparencyTarget));
        RenderUtil.safeCopyDepth(transparencyTarget, Minecraft.getInstance().getMainRenderTarget());

        // render setup stuff, ignore
        Quaternionf quaternionf = event.getCamera().rotation().conjugate(new Quaternionf());
        Matrix4f frustumRot = new Matrix4f().rotation(quaternionf);
        Matrix4fStack viewStack = RenderSystem.getModelViewStack();
        viewStack.pushMatrix();
        viewStack.mul(frustumRot);

        Player player = Minecraft.getInstance().player;
        Vec3 pos = player.getPosition(pTicks);
        Vec3 vec3 = event.getCamera().getPosition();
        double cx = pos.x() + 1 - vec3.x();
        double cy = pos.y() + 1.5 - vec3.y();
        double cz = pos.z() - vec3.z();

        PoseStack stack = event.getPoseStack();
        Level level = Minecraft.getInstance().level;
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedmodel = ir.getModel(new ItemStack(Items.CHEST), level, null, 1);
        stack.pushPose();
        stack.translate(cx, cy, cz);
        // ignore end

        ir.render(new ItemStack(Items.CHEST), ItemDisplayContext.GROUND, false, stack, srcWrap,
                LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, bakedmodel);
        srcWrap.end();

        PostChain chain = ShaderProgramsAS.TRANSPARENCY_COLOR.getWrapped().orElseThrow();
        chain.process(pTicks);
        RenderUtil.safeCopyDepth(Minecraft.getInstance().getMainRenderTarget(), transparencyTarget);
        stack.popPose();
        viewStack.popMatrix();
    }
    */

    public void tick(ClientTickEvent.Post event) {
        if (this.clear) {
            this.sources.forEach(EntityFX::setRemoved);
            this.sources.clear();
            this.effects.values().forEach(effects -> effects.forEach(EntityFX::setRemoved));
            this.effects.clear();
            this.clear = false;
            return;
        }

        Entity view = Minecraft.getInstance().getCameraEntity();
        if (view == null) view = Minecraft.getInstance().player;
        if (view == null) {
            clearAllEffects();
            return;
        }

        if (Minecraft.getInstance().isPaused()) return;

        this.effects.values().forEach(l -> {
            Iterator<EntityVisualFX> iterator = l.iterator();
            while (iterator.hasNext()) {
                EntityVisualFX fx = iterator.next();

                fx.tick();
                if (!canEffectPersist(fx) || fx.canRemove() || fx.isRemovalRequested()) {
                    iterator.remove();
                    fx.setRemoved();
                }
            }
        });

        this.sources.removeIf(src -> {
            src.tick();
            if (!Minecraft.getInstance().isPaused()) {
                src.tickSpawnFX();
            }
            if (src.canRemove() || src.isRemovalRequested()) {
                src.setRemoved();
                return true;
            }
            return false;
        });
    }

    void queueParticle(EffectTemplate<?> template, EntityVisualFX effect) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.queueParticle(template, effect));
            return;
        }
        this.effects.computeIfAbsent(template, t -> new ArrayList<>()).add(effect);
        effect.setActive();
    }

    void queueSource(EntitySourceFX source) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.queueSource(source));
            return;
        }
        this.sources.add(source);
        source.setActive();
    }

    public void clearAllEffects() {
        this.clear = true;
    }

    public static boolean canEffectPersist(EntityFX fx) {
        Entity view = Minecraft.getInstance().getCameraEntity();
        if (view == null) view = Minecraft.getInstance().player;
        if (view == null) return false;
        return fx.canEffectPersist(view);
    }
}
