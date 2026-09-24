/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.ShadersAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationPositionLoader;
import hellfirepvp.astralsorcery.client.sky.constellation.SkyConstellationRenderer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.network.play.PktAdjustAstrolabeAngle;
import hellfirepvp.astralsorcery.common.network.play.PktDiscoverConstellation;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.BiDiPair;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import hellfirepvp.astralsorcery.mixin.client.AccessorGameRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderAstrolabeOverlay
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderAstrolabeOverlay {

    private static final RandomSource rand = RandomSource.create();
    private static boolean isDrawing = false;
    private static ItemStack drawingItem = ItemStack.EMPTY;
    private static int capturedFov = -1;

    private static final List<DrawnConstellationLine> drawnLines = new ArrayList<>();
    private static FloatPoint drawLineStart = null;

    /** Mirrors {@link net.minecraft.client.gui.Gui#renderSpyglassOverlay} */
    public static void render(GuiGraphics guiGraphics, float scopeScale) {
        handleMouseState();

        renderOverlay(guiGraphics, scopeScale);
        if (!isDrawing()) return;
        renderDrawnLines(guiGraphics);
        //reverseProjection(mouseX, mouseY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    private static void renderOverlay(GuiGraphics guiGraphics, float scopeScale) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        float minScreenSize = (float) Math.min(guiGraphics.guiWidth(), guiGraphics.guiHeight());
        float scaledSize = Math.min(guiGraphics.guiWidth() / minScreenSize, guiGraphics.guiHeight() / minScreenSize) * scopeScale;
        int size = Mth.floor(minScreenSize * scaledSize);
        int xOffset = (guiGraphics.guiWidth()  - size) / 2;
        int yOffset = (guiGraphics.guiHeight() - size) / 2;
        int maxX = xOffset + size;
        int maxY = yOffset + size;

        RenderSystem.enableBlend();
        TexturesAS.SCREEN_OVERLAY_ASTROLABE.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), xOffset, yOffset, size, size).draw();
        });
        RenderSystem.disableBlend();

        guiGraphics.fill(RenderType.guiOverlay(), 0, maxY, guiGraphics.guiWidth(), guiGraphics.guiHeight(), -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), 0, 0, guiGraphics.guiWidth(), yOffset, -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), 0, yOffset, xOffset, maxY, -90, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), maxX, yOffset, guiGraphics.guiWidth(), maxY, -90, 0xFF000000);

        float ruleHeight = size * 0.85F;
        float rulePxRatio = ruleHeight / 255F;
        float ruleXOffset = xOffset - rulePxRatio * 10F;
        float ruleYOffset = guiGraphics.guiHeight() / 2F - ruleHeight / 2F;

        TexturesAS.SCREEN_OVERLAY_ASTROLABE_RULE.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), ruleXOffset, ruleYOffset, 6 * rulePxRatio, 255 * rulePxRatio).draw();
        });

        float indicatorAngle = 45F;
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isScoping() && player.getUseItem().is(ItemsAS.ASTROLABE)) {
            AstrolabeAngleComponent cmp = player.getUseItem().getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT);
            indicatorAngle = cmp.angle();
        }
        if (isDrawing()) {
            ItemStack usingItem = getDrawingItem();
            AstrolabeAngleComponent cmp = usingItem.getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT);
            indicatorAngle = cmp.angle();
        }

        int indicatorMoveHeight = 247;
        float indicatorRatio = indicatorMoveHeight / 90F;
        float indicatorXOffset = ruleXOffset - rulePxRatio * 5F;
        float indicatorYOffset = ruleYOffset + rulePxRatio + Math.round(indicatorAngle * indicatorRatio) * rulePxRatio;

        TexturesAS.SCREEN_OVERLAY_ASTROLABE_INDICATOR.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ShadersAS::getPositionColorTexAlphaShader, buf -> {
            RenderQuadUtil.rect(buf, guiGraphics.pose(), indicatorXOffset, indicatorYOffset, 4 * rulePxRatio, 6 * rulePxRatio).draw();
        });

        Font font = Minecraft.getInstance().font;
        String angleStr = Math.round(indicatorAngle) + "°";
        int maxTextWidth = font.width("88°");
        float indicatorAngleXOffset = indicatorXOffset - maxTextWidth * rulePxRatio;

        float alpha = Math.max(0F, scopeScale - 1F) * 8F;
        var ct = ScreenEffectTicketManager.getInstance().refreshOrCreate(StaticIdentifierTicket.ASTROLABE_OVERLAY);

        LevelSkyHandler.getContext(level).ifPresent(ctx -> {
            PlayerProgress progress = ResearchManager.getClientProgress();
            progress.getSeenFocalPoints().forEach(cst -> {
                float angle = ctx.getConstellationHandler().getAngle(cst);
                boolean discovered = progress.hasDiscoveredConstellation(cst);

                Component hint = !discovered ? Component.translatable("screen.astralsorcery.element.astrolabe.focal.hint") :
                        Component.translatable("screen.astralsorcery.element.astrolabe.focal", cst.getName());

                ColorWrapper color = cst.getConstellationColor();
                if (!discovered) {
                    float cycle = (ClientProxy.getClientTick() % 150) / 150F;
                    float ratio = (Mth.sin(cycle * 2 * Mth.PI) + 1F) / 2F;

                    color = ColorUtil.blendColors(color, ColorWrapper.WHITE, ratio * 0.5F);
                }

                float hintWidth = font.width(hint);
                float hintXOffset = indicatorXOffset - rulePxRatio * (5F + 2F) - hintWidth;
                float hintYOffset = ruleYOffset + rulePxRatio + Math.round(angle * indicatorRatio) * rulePxRatio - font.lineHeight / 2F;
                float txtAlpha = alpha * (0.3F + Math.min(1F, (Math.abs(hintYOffset - indicatorYOffset) - 8F) / 16F) * 0.7F);

                guiGraphics.drawString(font, hint.getVisualOrderText(),
                        hintXOffset, hintYOffset, color.copyWithAlpha(Math.max(5, Math.round(txtAlpha * 220))).getColor(), true);
            });

            if (ct.canAddEffects()) {
                progress.getSeenFocalPoints().forEach(cst -> {
                    if (!progress.hasDiscoveredConstellation(cst)) return;
                    float angle = ctx.getConstellationHandler().getAngle(cst);

                    ColorWrapper color = cst.getConstellationColor();
                    float hintYOffset = ruleYOffset + rulePxRatio + Math.round(angle * indicatorRatio) * rulePxRatio - font.lineHeight / 2F;

                    if (scopeScale > 0.75F) {
                        for (int i = 0; i < 1; i++) {
                            float randomYOffset = (rand.nextFloat() - rand.nextFloat()) * 6F * rulePxRatio;
                            ct.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, xOffset + rulePxRatio, hintYOffset + randomYOffset + font.lineHeight / 2F)
                                    .color(rand.nextInt(2) == 0 ? FXColorFunction.constant(color) : FXColorFunction.WHITE)
                                    .setAlpha(alpha * 0.4F)
                                    .alpha(FXAlphaFunction.FADE_OUT)
                                    .setScale(7 + rand.nextInt(4))
                                    .setMotion(Vector3.x(-0.5 - rand.nextFloat() * 0.4))
                                    .setGravity(Vector3.x(-0.03F - rand.nextFloat() * 0.05F))
                                    .setMaxAge(30 + rand.nextInt(10));
                        }

                        if (rand.nextInt(25) == 0) {
                            float randomYOffset = (rand.nextFloat() - rand.nextFloat()) * 4F * rulePxRatio;
                            float beamY = hintYOffset + randomYOffset + font.lineHeight / 2F;
                            ct.createParticle(EffectTemplatesAS.SCREEN_LIGHT_BEAM, xOffset + rulePxRatio, beamY)
                                    .setup(new Vector2f(xOffset + rulePxRatio - 180 * rulePxRatio, beamY), 45F, 45F)
                                    .color(rand.nextInt(2) == 0 ? FXColorFunction.constant(color) : FXColorFunction.WHITE)
                                    .setAlpha(alpha * 0.4F);
                        }
                    }
                });
            }
        });

        guiGraphics.pose().translate(0, 0, -100);
        ct.renderAll(guiGraphics, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        guiGraphics.pose().translate(0, 0, 100);

        guiGraphics.drawString(font, angleStr,
                indicatorAngleXOffset, indicatorYOffset, ChatFormatting.GOLD.getColor(), true);
    }

    private static void renderDrawnLines(GuiGraphics guiGraphics) {
        if (drawnLines.isEmpty() && drawLineStart == null) return;

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        if (DayTimeHelper.isDay(level)) {
            drawnLines.clear();
            drawLineStart = null;
            return;
        }

        float starBrightness = level.getStarBrightness(partialTick) * (1.0F - level.getRainLevel(partialTick));
        if (starBrightness <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        float mouseX = (float) (mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
        float mouseY = (float) (mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
        if (drawLineStart != null && !isInsideDrawingBounds(mouseX, mouseY)) {
            drawLineStart = null;
            if (drawnLines.isEmpty()) {
                return; //Don't draw anything
            }
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderSystem.disableCull();
        TexturesAS.STAR_LINE.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            PoseStack.Pose pose = guiGraphics.pose().last();

            drawnLines.forEach(line -> {
                drawLine(buf, pose, line, partialTick, starBrightness);
            });

            if (drawLineStart != null) {
                DrawnConstellationLine line = new DrawnConstellationLine(drawLineStart, new FloatPoint(mouseX, mouseY));

                drawLine(buf, pose, line, partialTick, starBrightness);
            }
        });
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawLine(BufferBuilder buf, PoseStack.Pose pose, DrawnConstellationLine line, float partialTick, float alpha) {
        float lineSize = 10F;

        RandomSource rand = RandomSource.create(line.getLeft().hashCode() * 31L ^ line.getRight().hashCode() * 31L);
        float flickerSpeed = 0.02F + rand.nextFloat() * 0.04F;
        float brightness = EffectUtil.flicker(flickerSpeed, partialTick) * alpha;
        brightness = brightness * 0.4F + 0.6F;
        ColorWrapper drawColor = ColorWrapper.WHITE.copyWithAlpha(Mth.floor(brightness * 255F));

        Vector3 offset = new Vector3(line.getLeft().x(), line.getLeft().y(), 0)
                .subtract(new Vector3(line.getRight().x(), line.getRight().y(), 0))
                .perpendicular()
                .normalize()
                .multiply(lineSize / 2);

        Vector3 v = new Vector3(line.getLeft().x() - offset.getX(), line.getLeft().y() - offset.getY(), 0);
        v.drawPos(pose.pose(), buf).setUv(0, 0).setColor(drawColor.getColor());

        v = new Vector3(line.getRight().x() - offset.getX(), line.getRight().y() - offset.getY(), 0);
        v.drawPos(pose.pose(), buf).setUv(1, 0).setColor(drawColor.getColor());

        v = new Vector3(line.getRight().x() + offset.getX(), line.getRight().y() + offset.getY(), 0);
        v.drawPos(pose.pose(), buf).setUv(1, 1).setColor(drawColor.getColor());

        v = new Vector3(line.getLeft().x() + offset.getX(), line.getLeft().y() + offset.getY(), 0);
        v.drawPos(pose.pose(), buf).setUv(0, 1).setColor(drawColor.getColor());
    }

    private static FloatPoint getProjectedAngles(Player player, double pointX, double pointY) {
        Minecraft mc = Minecraft.getInstance();
        return getProjectedAngles(player, pointX, pointY, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    private static FloatPoint getProjectedAngles(Player player, double pointX, double pointY, double screenWidth, double screenHeight) {
        float pTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        AccessorGameRenderer gameRendererAccess = (AccessorGameRenderer) Minecraft.getInstance().gameRenderer;
        double realFov = gameRendererAccess.callGetFov(camera, pTicks, true);
        Matrix4f projMatrix = Minecraft.getInstance().gameRenderer.getProjectionMatrix(realFov);

        double yawRad = Math.toRadians(Mth.wrapDegrees(player.getViewYRot(pTicks)));
        double pitchRad = Math.toRadians(player.getViewXRot(pTicks));

        double viewportX = (2 * pointX) / screenWidth - 1;
        double viewportY = 1 - (2 * pointY) / screenHeight;
        Vector4f normalizedCoords = new Vector4f((float) viewportX, (float) viewportY, 1, 1);

        Vector4f cameraSpace = projMatrix.invert().transform(normalizedCoords);
        cameraSpace.div(cameraSpace.w);
        Vector3f cameraView = new Vector3f(cameraSpace.x, cameraSpace.y, cameraSpace.z).normalize();
        Vector3f viewDir = new Vector3f(
                (float) (Math.cos(pitchRad) * Math.sin(yawRad)),
                (float) Math.sin(pitchRad),
                (float) (-Math.cos(pitchRad) * Math.cos(yawRad))
        );
        Vector3f worldDir = new Vector3f(0, -1, 0);
        Vector3f right = worldDir.cross(viewDir, new Vector3f()).normalize();
        worldDir = viewDir.cross(right, new Vector3f()).normalize();

        Matrix4f cameraViewRot = new Matrix4f(
                right.x, right.y, right.z, 0,
                worldDir.x, worldDir.y, worldDir.z, 0,
                -viewDir.x, -viewDir.y, -viewDir.z, 0,
                0, 0, 0, 1);

        Vector3f mouseView = cameraViewRot.transformDirection(cameraView).normalize();

        float dirY = mouseView.y;
        float resultPitch = (float) Math.toDegrees(Math.asin(Mth.clamp(dirY, -1F, 1F)));
        float resultYaw = (float) Math.toDegrees(Math.atan2(-mouseView.x, mouseView.z)) + 180;
        resultYaw = ((resultYaw + 180F) % 360F + 360F) % 360F - 180F;

        return new FloatPoint(resultYaw, resultPitch);

        //FloatPoint viewPoint = new FloatPoint(resultYaw, resultPitch);
        //SkyConstellationRenderer.constellationMappings.forEach(mapping -> {
        //    mapping.getPlayerViewRenderedStars().values().forEach(point -> {
        //        if (point.distance(viewPoint) <= 1F) {
        //            player.sendSystemMessage(Component.literal("Looking at " + RegistriesAS.REGISTRY_CONSTELLATIONS.getKey(mapping.getConstellation())));
        //        }
        //    });
        //});
    }

    private static void checkConstellationMatch() {
        if (SkyConstellationPositionLoader.getInstance().renderDebug()) return;
        PlayerProgress clientProgress = ResearchManager.getClientProgress();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        List<DrawnConstellationLine> projectedLines = drawnLines.stream()
                .map(DrawnConstellationLine::project)
                .toList();

        for (SkyConstellationRenderer.ConstellationMapping drawnMapping : SkyConstellationRenderer.getConstellationMappings()) {
            BaseConstellation cst = drawnMapping.getConstellation();
            if (clientProgress.hasDiscoveredConstellation(cst)) continue;
            if (cst.getStarConnections().size() != projectedLines.size()) continue;
            if (!cst.canDiscover(player, clientProgress)) continue;

            boolean match = true;
            for (StarConnection connection : cst.getStarConnections()) {
                StarLocation from = connection.getLeft();
                StarLocation to = connection.getRight();

                FloatPoint drawnFrom = getDrawnPos(drawnMapping, from);
                FloatPoint drawnTo = getDrawnPos(drawnMapping, to);

                if (!hasProjectedLine(projectedLines, drawnFrom, drawnTo)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                drawnLines.clear();
                drawLineStart = null;

                PacketDistributor.sendToServer(PktDiscoverConstellation.discover(cst));
                return;
            }
        }
    }

    private static boolean hasProjectedLine(List<DrawnConstellationLine> projectedLines, FloatPoint drawnFrom, FloatPoint drawnTo) {
        for (DrawnConstellationLine line : projectedLines) {
            if (line.getLeft().distance(drawnFrom) < 0.5F && line.getRight().distance(drawnTo) < 0.5F) {
                return true;
            }
            if (line.getLeft().distance(drawnFrom.add(360, 0)) < 0.5F && line.getRight().distance(drawnTo.add(360, 0)) < 0.5F) {
                return true;
            }
            if (line.getLeft().distance(drawnTo) < 0.5F && line.getRight().distance(drawnFrom) < 0.5F) {
                return true;
            }
            if (line.getLeft().distance(drawnTo.add(360, 0)) < 0.5F && line.getRight().distance(drawnFrom.add(360, 0)) < 0.5F) {
                return true;
            }
        }
        return false;
    }

    private static FloatPoint getDrawnPos(SkyConstellationRenderer.ConstellationMapping drawnMapping, StarLocation star) {
        FloatPoint drawnPos = drawnMapping.getPlayerViewRenderedStars().get(star);
        if (drawnPos == null) {
            AstralSorcery.LOG.error("Constellation drawing did not draw all stars!");
            AstralSorcery.LOG.error("{} missing star: {}", RegistriesAS.REGISTRY_CONSTELLATIONS.getKey(drawnMapping.getConstellation()), star);
            throw new IllegalStateException();
        }
        return drawnPos;

    }

    private static void handleMouseState() {
        if (Screen.hasShiftDown()) {
            if (Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
                Minecraft.getInstance().mouseHandler.releaseMouse();
            }
            startDrawing();
        } else {
            if (!Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
                Minecraft.getInstance().mouseHandler.grabMouse();
            }
            stopDrawing();
        }
    }

    private static void startDrawing() {
        if (!isDrawing()) {
            isDrawing = true;
            drawingItem = Minecraft.getInstance().player.getUseItem().copy();
        }
    }

    private static void stopDrawing() {
        if (isDrawing()) {
            isDrawing = false;
            drawingItem = ItemStack.EMPTY;
            drawnLines.clear();
            drawLineStart = null;

            Player player = Minecraft.getInstance().player;
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            if (player != null && player.isUsingItem() && gameMode != null) {
                if (!Minecraft.getInstance().options.keyUse.isDown()) {
                    gameMode.releaseUsingItem(player);
                }
            }
        }
    }

    public static boolean isDrawing() {
        return isDrawing;
    }

    public static ItemStack getDrawingItem() {
        return drawingItem.copy();
    }

    public static boolean isInsideDrawingBounds(float x, float y) {
        if (!isDrawing()) return false;
        Window window = Minecraft.getInstance().getWindow();
        float maxDistance = (float) Math.min(window.getGuiScaledWidth() / 2, window.getGuiScaledHeight() / 2);
        FloatPoint center = new FloatPoint(window.getGuiScaledWidth() / 2F, window.getGuiScaledHeight() / 2F);
        return Math.abs(center.x() - x) <= maxDistance && Math.abs(center.y() - y) <= maxDistance;
    }

    public static void astrolabeMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        if (isDrawing()) {
            event.setCanceled(true);
            return; // No adjusting angle while drawing
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.isScoping() && player.getUseItem().is(ItemsAS.ASTROLABE)) {
            AstrolabeAngleComponent cmp = player.getUseItem().getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT);
            float adjustedAngle = cmp.angle() + (float) event.getScrollDeltaY();
            player.getUseItem().set(DataComponentsAS.ASTROLABE_ANGLE, new AstrolabeAngleComponent(adjustedAngle, cmp.matchesAll()));
            PacketDistributor.sendToServer(PktAdjustAstrolabeAngle.adjustAngle(adjustedAngle));
            event.setCanceled(true);
        }
    }

    public static void overrideFov(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            if (capturedFov > 0) {
                Minecraft.getInstance().options.fov().set(capturedFov);
                capturedFov = -1;
            }
            return;
        }

        if (AstrolabeItem.isUsingAstrolabe(player)) {
            if (capturedFov < 0) {
                capturedFov = Minecraft.getInstance().options.fov().get();
                Minecraft.getInstance().options.fov().set(70); //Min fov for drawing
            }
        } else {
            if (capturedFov > 0) {
                Minecraft.getInstance().options.fov().set(capturedFov);
                capturedFov = -1;
            }
        }
    }

    public static void preventScreenOpen(ScreenEvent.Opening event) {
        if (isDrawing()) {
            event.setCanceled(true);
        }
    }

    public static void overrideMouseClickDuringDrawing(InputEvent.MouseButton.Pre event) {
        if (isDrawing() && event.getButton() == 0) {
            Minecraft mc = Minecraft.getInstance();
            float mouseX = (float) (mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
            float mouseY = (float) (mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
            if (isInsideDrawingBounds(mouseX, mouseY)) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    drawLineStart = new FloatPoint(mouseX, mouseY);
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    if (drawLineStart != null) {
                        drawnLines.add(new DrawnConstellationLine(drawLineStart, new FloatPoint(mouseX, mouseY)));
                        drawLineStart = null;

                        checkConstellationMatch();
                    }
                }
            } else {
                drawLineStart = null;
            }
            event.setCanceled(true);
        }
    }

    public static class DrawnConstellationLine extends BiDiPair<FloatPoint, FloatPoint> {

        public DrawnConstellationLine(FloatPoint left, FloatPoint right) {
            super(left, right);
        }

        public DrawnConstellationLine project() {
            Player player = Minecraft.getInstance().player;
            if (player == null) return this; // Well, fck any checks i guess.

            FloatPoint projectedLeft = getProjectedAngles(player, getLeft().x(), getLeft().y());
            FloatPoint projectedRight = getProjectedAngles(player, getRight().x(), getRight().y());
            return new DrawnConstellationLine(projectedLeft, projectedRight);
        }
    }
}
