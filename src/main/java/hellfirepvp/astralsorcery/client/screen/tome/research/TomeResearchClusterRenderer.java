/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.research;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.screen.base.FixedSizeScreen;
import hellfirepvp.astralsorcery.client.screen.base.ResearchTierClusterSizeHandler;
import hellfirepvp.astralsorcery.client.screen.base.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.tome.TomePagesScreen;
import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.network.play.PktRequestLearnedTomeNavigation;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeVisibility;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeLoader;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeResearchClusterRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeResearchClusterRenderer {

    private final ResearchTier tier;
    private final Map<ResourceLocation, ResearchNode> nodes = new LinkedHashMap<>();
    private final ResearchTierClusterSizeHandler sizeHandler;
    private final ScalingPoint mousePoint;
    private final ScalingPoint prevMousePoint;

    private final Map<FloatRectangle, ResearchNode> renderedNodes = new HashMap<>();

    public TomeResearchClusterRenderer(ResearchTier tier) {
        this.tier = tier;
        ResearchNodeLoader.getInstance().getNodes().stream()
                .filter(node -> node.getTier() == tier)
                .forEach(node -> this.nodes.put(node.getKey(), node));
        this.sizeHandler = new ResearchTierClusterSizeHandler(tier);
        this.sizeHandler.updateSize();
        this.sizeHandler.setScale(0.1F);

        FloatPoint center = this.sizeHandler.getRelativeCenter();
        this.mousePoint = ScalingPoint.createPoint(0, 0, this.sizeHandler.getScalingFactor(), false);
        this.prevMousePoint = ScalingPoint.copy(this.mousePoint);
        this.moveMouse(center.x(), center.y());
        this.applyMouseMove();
    }

    public final ResearchTier getTier() {
        return this.tier;
    }

    public void zoomOut() {
        this.sizeHandler.handleZoomOut();
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.prevMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);
    }

    public void zoomIn() {
        this.sizeHandler.handleZoomIn();
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.prevMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);
    }

    public void moveMouse(float changeX, float changeY) {
        this.mousePoint.updateScaledPos(
                this.sizeHandler.clampX(this.prevMousePoint.getScaledPosX() + changeX),
                this.sizeHandler.clampY(this.prevMousePoint.getScaledPosY() + changeY),
                this.sizeHandler.getScalingFactor()
        );
    }

    public void applyMouseMove() {
        this.prevMousePoint.updateScaledPos(this.mousePoint.getScaledPosX(), this.mousePoint.getScaledPosY(), this.sizeHandler.getScalingFactor());
    }

    public float getMouseX() {
        return this.mousePoint.getPosX();
    }

    public float getMouseY() {
        return this.mousePoint.getPosY();
    }

    public boolean mouseClick(TomeResearchScreen screen, double mouseX, double mouseY) {
        for (FloatRectangle nodeBox : this.renderedNodes.keySet()) {
            if (nodeBox.contains(mouseX, mouseY)) {
                ResearchNode node = this.renderedNodes.get(nodeBox);
                Minecraft.getInstance().setScreen(TomePagesScreen.fromProgressNode(screen, node));
                PacketDistributor.sendToServer(PktRequestLearnedTomeNavigation.request());
                return true;
            }
        }
        return false;
    }

    public void drawClusterView(FixedSizeScreen parentGui, GuiGraphics guiGraphics, float pTicks) {
        this.renderedNodes.clear();
        float zoomAlpha = Mth.clamp((this.sizeHandler.getScalingFactor() - 0.3F) / 0.7F, 0F, 1F);

        //Collect connections, nodes, backgrounds
        Map<AbstractRenderTexture, List<ResearchNode>> batchedBackground = new HashMap<>();
        PlayerProgress progress = ResearchManager.getClientProgress();
        Set<Pair<ResearchNode, ResearchNode>> connections = new HashSet<>();
        for (ResearchNode node : this.nodes.values()) {
            if (!node.getVisibility(progress).getType().shouldRender()) {
                continue;
            }
            for (ResourceLocation connectionKey : node.getConnections()) {
                ResearchNode connectedNode = this.nodes.get(connectionKey);
                if (connectedNode != null && connectedNode.getVisibility(progress).getType().shouldRender()) {
                    connections.add(new Pair<>(node, connectedNode));
                }
            }
            batchedBackground.computeIfAbsent(node.getBackgroundTexture().resolve(), tex -> new ArrayList<>())
                    .add(node);
        }

        float size = this.sizeHandler.getScaledNodeSize();
        float nodeDistanceFactor = 0.45F;
        this.drawConnections(parentGui, guiGraphics, connections, zoomAlpha, size, nodeDistanceFactor, pTicks);

        //Draw batched backgrounds
        for (AbstractRenderTexture background : batchedBackground.keySet()) {
            background.bindTexture();
            List<ResearchNode> nodes = batchedBackground.get(background);

            RenderSystem.enableBlend();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                nodes.forEach(node -> {
                    FloatPoint nodeOffset = this.sizeHandler.scalePointToGui(parentGui, this.mousePoint,
                            new FloatPoint(node.getPosX(), node.getPosY()).multiply(nodeDistanceFactor));
                    FloatRectangle nodeBox = new FloatRectangle(nodeOffset.x() - size / 2F, nodeOffset.y() - size / 2F, size, size);

                    if (this.sizeHandler.getScalingFactor() > 0.7) {
                        this.renderedNodes.put(nodeBox, node);
                    }

                    PoseStack.Pose pose = guiGraphics.pose().last();
                    buf.addVertex(pose, nodeBox.x(),    nodeBox.maxY(), 0).setColor(zoomAlpha, zoomAlpha, zoomAlpha, zoomAlpha).setUv(0, 1);
                    buf.addVertex(pose, nodeBox.maxX(), nodeBox.maxY(), 0).setColor(zoomAlpha, zoomAlpha, zoomAlpha, zoomAlpha).setUv(1, 1);
                    buf.addVertex(pose, nodeBox.maxX(), nodeBox.y(),    0).setColor(zoomAlpha, zoomAlpha, zoomAlpha, zoomAlpha).setUv(1, 0);
                    buf.addVertex(pose, nodeBox.x(),    nodeBox.y(),    0).setColor(zoomAlpha, zoomAlpha, zoomAlpha, zoomAlpha).setUv(0, 0);
                });
            });
            RenderSystem.disableBlend();
        }

        //Setup transparency shader
        RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
        transparencyTarget.clear(Minecraft.ON_OSX);
        ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(ColorWrapper.WHITE.copyWithAlpha(Mth.floor(zoomAlpha * 255F)));
        WrappedBufferSource chainBuffers = new WrappedBufferSource(guiGraphics.bufferSource(),
                renderType -> DrawChainRenderType.wrap("tome_overlay_", renderType, transparencyTarget));
        chainBuffers.endBatch();
        RenderUtil.safeCopyDepth(transparencyTarget, Minecraft.getInstance().getMainRenderTarget());
        GuiGraphics wrappedGraphics = new GuiGraphics(Minecraft.getInstance(), chainBuffers);
        wrappedGraphics.pose().mulPose(guiGraphics.pose().last().pose());

        //Draw batched items with shader transparency
        for (ResearchNode node : this.nodes.values()) {
            ResearchNodeVisibility visibility = node.getVisibility(progress);
            if (!visibility.getType().shouldRender()) {
                continue;
            }
            FloatPoint offset = this.sizeHandler.scalePointToGui(parentGui, this.mousePoint,
                    new FloatPoint(node.getPosX(), node.getPosY()).multiply(nodeDistanceFactor));
            ItemStack renderItem = node.getRenderItemStack(ClientProxy.getClientTick());
            PoseStack poseStack = wrappedGraphics.pose();

            poseStack.pushPose();
            poseStack.translate(offset.x() - size / 2F, offset.y() - size / 2F, 0);
            poseStack.scale(this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor(), 1.0F);
            poseStack.translate(3, 3, 0);
            wrappedGraphics.renderFakeItem(renderItem, 0, 0);
            poseStack.popPose();
        }

        chainBuffers.end();
        PostChain chain = ShaderProgramsAS.TRANSPARENCY_COLOR.getWrapped().orElseThrow();
        chain.process(pTicks);
        RenderUtil.safeCopyDepth(Minecraft.getInstance().getMainRenderTarget(), transparencyTarget);
    }

    private void drawConnections(FixedSizeScreen parentGui, GuiGraphics graphics, Set<Pair<ResearchNode, ResearchNode>> connections, float zoomAlpha, float size, float nodeDistanceFactor, float pTicks) {
        if (connections.isEmpty()) return;

        Matrix4f matr = graphics.pose().last().pose();
        float width = 0.3F * size;
        float alpha = zoomAlpha * 0.75F;

        int travelPeriod = 60;
        int totalPeriod  = 80;

        // I could have done this with a shader, you're so right
        // Why not? idk. am lazy or smth
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        TexturesAS.SCREEN_ELEMENT_LINE_CONNECTION.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            connections.forEach(connection -> {
                ResearchNode from = connection.getFirst();
                ResearchNode to = connection.getSecond();

                long tick = ClientProxy.getClientTick() + Mth.floor((from.getPosX() + from.getPosY() + to.getPosX() + to.getPosY()) * 10);
                float animTick = (tick % totalPeriod) - 10 + pTicks;
                float wavePos = animTick / travelPeriod;

                FloatPoint fromOffset = this.sizeHandler.scalePointToGui(parentGui, this.mousePoint, new FloatPoint(from.getPosX(), from.getPosY()).multiply(nodeDistanceFactor));
                FloatPoint toOffset = this.sizeHandler.scalePointToGui(parentGui, this.mousePoint, new FloatPoint(to.getPosX(), to.getPosY()).multiply(nodeDistanceFactor));

                Vector3 dir = fromOffset.toVector().vectorFromHereTo(toOffset.toVector());
                Vector3 widthVec = dir.copy().rotate(Math.toRadians(90), Vector3.RotAxis.Z_AXIS).normalize().multiply(width);

                List<Vector3> points = VectorUtil.iteratePoints(fromOffset.toVector(), toOffset.toVector(), 1.5F);
                if (points.size() < 2) return;

                int segmentCount = points.size() - 1;
                for (int i = 0; i < segmentCount; i++) {
                    Vector3 pos = points.get(i);
                    Vector3 next = points.get(i + 1);

                    float srcFromWave = ((float) i / (segmentCount - 1)) - wavePos;
                    float srcPart = (float) Math.exp(-(srcFromWave * srcFromWave) / 0.018F);
                    float srcAlpha = Mth.clamp(alpha * 0.4F + srcPart * alpha, 0F, 1F);
                    float srcBr = Mth.clamp(0.7F + srcPart * 0.3F, 0F, 1F);

                    float dstFromWave = ((float) (i + 1) / (segmentCount - 1)) - wavePos;
                    float dstPart = (float) Math.exp(-(dstFromWave * dstFromWave) / 0.018F);
                    float dstAlpha = Mth.clamp(alpha * 0.4F + dstPart * alpha, 0F, 1F);
                    float dstBr = Mth.clamp(0.7F + dstPart * 0.3F, 0F, 1F);

                    Vector3 posDir = pos.vectorFromHereTo(next);
                    Vector3 origin = pos.copy().subtract(widthVec.copy().divide(2));
                    origin.drawPos(matr, buf)
                            .setColor(srcBr, srcBr, srcBr, srcAlpha)
                            .setUv(0, 0);
                    origin.copy().add(widthVec).drawPos(matr, buf)
                            .setColor(srcBr, srcBr, srcBr, srcAlpha)
                            .setUv(0, 1);
                    origin.copy().add(posDir).add(widthVec).drawPos(matr, buf)
                            .setColor(dstBr, dstBr, dstBr, dstAlpha)
                            .setUv(1, 1);
                    origin.copy().add(posDir).drawPos(matr, buf)
                            .setColor(dstBr, dstBr, dstBr, dstAlpha)
                            .setUv(1, 0);
                }
            });
        });
        RenderSystem.disableBlend();
    }

    public void drawHoverHighlight(GuiGraphics guiGraphics, float mouseX, float mouseY) {
        for (FloatRectangle nodeBox : this.renderedNodes.keySet()) {
            if (nodeBox.contains(mouseX, mouseY)) {
                ResearchNode node = this.renderedNodes.get(nodeBox);

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(node.getName());
                if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
                    tooltip.add(Component.empty());
                    tooltip.add(Component.translatable("screen.astralsorcery.element.research_debug.registry_name", node.getKey().toString())
                            .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
                }

                List<FormattedCharSequence> tooltipLines = tooltip.stream()
                        .map(Component::getVisualOrderText)
                        .toList();
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(nodeBox.x(), nodeBox.y(), 200);
                poseStack.scale(this.sizeHandler.getScalingFactor(), this.sizeHandler.getScalingFactor(), 1.0F);
                TooltipUtil.blueColor(() -> guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipLines, 0, 0));
                poseStack.popPose();
            }
        }
    }
}
