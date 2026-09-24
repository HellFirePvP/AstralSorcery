/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXScaleFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AtlasSpriteTexture;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.base.ScalingPoint;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.LumenOverviewSizeHandler;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.data.LumenDisplayPosition;
import hellfirepvp.astralsorcery.client.screen.tome.lumen.data.LumenDisplayPositionLoader;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import hellfirepvp.astralsorcery.common.util.data.FloatRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeLumenScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeLumenScreen extends TomeScreen implements TomeStarParallaxLayer {

    public static final BookmarkProvider BOOKMARK = new BookmarkProvider("lumen", 40,
            () -> Minecraft.getInstance().setScreen(new TomeLumenScreen()),
            () -> ResearchManager.getClientProgress().getTierReached().isThisLaterOrEqual(ResearchTier.RESONANCE) ||
                    ResearchManager.getClientProgress().getDiscoveredLumen().stream().anyMatch(lumen -> !lumen.isElementary()));
    private static final List<Connection> lumenConnections = new ArrayList<>();

    private final LumenOverviewSizeHandler sizeHandler;

    private final ScalingPoint mousePoint;
    private ScalingPoint previousMousePoint;

    private final Map<Lumen, FloatRectangle> thisFrameLumen = new HashMap<>();
    private Lumen hoveredLumen = null;

    protected TomeLumenScreen() {
        super(BOOKMARK.getBookmarkIndex());

        this.sizeHandler = new LumenOverviewSizeHandler();
        this.sizeHandler.setWidthHeightNodes(16F);
        this.sizeHandler.setSpaceBetweenNodes(10F);
        this.sizeHandler.setMaxScale(1.2F);
        this.sizeHandler.setMinScale(0.5F);
        this.sizeHandler.setScaleSpeed(0.1F);
        this.sizeHandler.updateSize();

        this.mousePoint = ScalingPoint.createPoint(
                this.sizeHandler.clampX(this.sizeHandler.getScaledWidth() / 2F),
                this.sizeHandler.clampY(this.sizeHandler.getScaledHeight() / 2F),
                this.sizeHandler.getScalingFactor(),
                false);
        this.previousMousePoint = ScalingPoint.copy(this.mousePoint);
    }

    public static void recipesSyncedFromServer(RecipesUpdatedEvent event) {
        lumenConnections.clear();
    }

    private static void buildLumenConnections() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        connection.getRecipeManager().getAllRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get()).forEach(recipeHolder -> {
            LumenGenerationRecipe recipe = recipeHolder.value();
            Lumen generated = recipe.getProducedLumen();
            recipe.getLumenCombinationInputs().keySet().forEach(lumenInput -> {
                lumenConnections.add(new Connection(lumenInput, generated));
            });
        });
    }

    @Override
    protected void init() {
        super.init();

        this.initBookmarks();

        this.moveMouse(0, 0);
        this.applyMovedMouseOffset();
    }

    @Override
    public void tick() {
        super.tick();

        var ct = ScreenEffectTicketManager.getInstance().refreshOrCreate(StaticIdentifierTicket.TOME_LUMEN_OVERVIEW);
        this.createLumenParticles(ct);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.thisFrameLumen.clear();
        this.renderTransparentBackground(guiGraphics);

        ScreenRectangle rect = this.getScreenRectangle();
        guiGraphics.enableScissor(rect.left() + 20, rect.top() + 20, rect.right() - 20, rect.bottom() - 20);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_TOME_BACKGROUND_LUMEN, ColorWrapper.opaque(0x444444), rect);
        RenderSystem.disableBlend();
        this.drawLumenTree(guiGraphics, partialTick);
        this.drawStarParallaxLayers(guiGraphics, this.sizeHandler.getScalingFactor() * 0.5F, this.getScreenRectangle(),
                this.mousePoint.getPosX(), this.mousePoint.getPosY());

        guiGraphics.disableScissor();

        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_FRAME_CUTOUT, this.getScreenRectangle());
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.drawHoveredTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawLumenTree(GuiGraphics guiGraphics, float partialTick) {
        if (lumenConnections.isEmpty()) buildLumenConnections();

        var ct = ScreenEffectTicketManager.getInstance().refreshOrCreate(StaticIdentifierTicket.TOME_LUMEN_OVERVIEW);
        ct.renderAll(guiGraphics, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));

        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TexturesAS.ATLAS_LUMEN);
        float size = this.sizeHandler.getScaledNodeSize() * 2.2F;

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        RenderSystem.enableBlend();
        LumenDisplayPositionLoader.getInstance().getPositions().forEach((lumen, position) -> {
            if (!lumen.maySee(level, ResearchManager.getClientProgress())) return;

            lumen.getRegistryKey().map(ResourceKey::location).ifPresent(id -> {
                FloatPoint offset = this.sizeHandler.scalePointToGui(this, this.mousePoint, position.asPoint());
                FloatRectangle lumenBox = new FloatRectangle(offset.x() - size / 2F, offset.y() - size / 2F, size, size);
                this.thisFrameLumen.put(lumen, lumenBox);

                List<Lumen> connectedLumen = new ArrayList<>();
                for (Connection lumenConnection : lumenConnections) {
                    if (lumenConnection.source() == lumen) connectedLumen.add(lumenConnection.target());
                    if (lumenConnection.target() == lumen) connectedLumen.add(lumenConnection.source());
                }
                boolean hovered = this.hoveredLumen == null ||
                        (this.hoveredLumen == lumen || connectedLumen.contains(this.hoveredLumen));

                float adjustedSize = size * 0.6F;

                TextureAtlasSprite tas = atlas.getSprite(id);
                ColorWrapper color = lumen.getColor(ClientProxy.getClientTick());
                if (!hovered) {
                    color = color.copyWithAlpha(0x33);
                }
                RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), AtlasSpriteTexture.fromAtlasSprite(tas), color,
                        offset.x() - adjustedSize / 2F, offset.y() - adjustedSize / 2F,
                        adjustedSize, adjustedSize, UVFrame.fromAtlasSprite(tas));
            });
        });
        RenderSystem.disableBlend();
    }

    private void createLumenParticles(StaticIdentifierTicket.Container ct) {
        RandomSource rand = RandomSource.create();
        FXScaleFunction<?> sizeHandlerScale = (FXScaleFunction<EntityVisualFX>) (fx, scaleIn, pTicks) -> {
            return scaleIn * this.sizeHandler.getScalingFactor();
        };

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        LumenDisplayPositionLoader.getInstance().getPositions().forEach((lumen, position) -> {
            if (!lumen.maySee(level, ResearchManager.getClientProgress())) return;
            long tick = ClientProxy.getClientTick();

            float scale = 15F + rand.nextFloat() * 10F;
            int age = 30 + rand.nextInt(10);
            float rX = (rand.nextFloat() * 2F - 1F) * 10F;
            float rY = (rand.nextFloat() * 2F - 1F) * 10F;

            FXRenderOffsetFunction<?> renderOffset = (fx, renderPosition, pTicks) -> {
                FloatPoint rPos = new FloatPoint((float) renderPosition.getX(), (float) renderPosition.getY())
                        .add(this.sizeHandler.scalePointToGui(this, this.mousePoint, position.asPoint()));
                rPos = rPos.add(rX * this.sizeHandler.getScalingFactor(), rY * this.sizeHandler.getScalingFactor());
                return new Vector3(rPos.x(), rPos.y(), renderPosition.getZ());
            };
            FXAlphaFunction<?> hoverAlpha = (fx, alphaIn, pTicks) -> {
                if (this.hoveredLumen == null || this.hoveredLumen == lumen) return alphaIn;
                return alphaIn * 0.2F;
            };

            ct.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, 0, 0)
                    .color(rand.nextInt(5) == 0 ? FXColorFunction.WHITE : FXColorFunction.constant(lumen.getColor(tick)))
                    .setAlpha(0.75F)
                    .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT).andThen(hoverAlpha))
                    .setScale(scale)
                    .scale(sizeHandlerScale)
                    .renderOffset(renderOffset)
                    .setMaxAge(age);

            ct.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, 0, 0)
                    .setAlpha(0.75F)
                    .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT).andThen(hoverAlpha))
                    .setScale(scale * 0.3F)
                    .scale(sizeHandlerScale)
                    .renderOffset(renderOffset)
                    .setMaxAge(Mth.ceil(age * 0.7F));

            /*if (rand.nextInt(14) == 0 && false) {
                Vector3 rPos = Vector3.random(rand).setZ(0).multiply(20F);
                ct.createParticle(EffectTemplatesAS.SCREEN_LUMEN_PARTICLE, rPos.getX(), rPos.getY())
                        .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(lumen))
                        .alpha(FXAlphaFunction.PYRAMID.andThen(hoverAlpha))
                        .color(FXColorFunction.constant(lumen.getColor(tick)))
                        .renderOffset(renderOffset)
                        .setMaxAge(40 + rand.nextInt(20));
            }*/
        });

        long tick = ClientProxy.getClientTick();
        lumenConnections.forEach(connection -> {
            if (!connection.maySee()) return;

            LumenDisplayPosition srcPos = LumenDisplayPositionLoader.getInstance().getPosition(connection.source());
            LumenDisplayPosition dstPos = LumenDisplayPositionLoader.getInstance().getPosition(connection.target());
            if (srcPos == null || dstPos == null) return;

            int cycleLength = 40;
            float cycleTravel = cycleLength - 15;
            long cycle = tick % cycleLength;
            if (cycle >= cycleTravel) return;

            float spawnX = Mth.lerp(cycle / cycleTravel, srcPos.x(), dstPos.x());
            float spawnY = Mth.lerp(cycle / cycleTravel, srcPos.y(), dstPos.y());
            FloatPoint spawn = new FloatPoint(spawnX, spawnY);

            FXRenderOffsetFunction<?> renderOffset = (fx, renderPosition, pTicks) -> {
                FloatPoint rPos = new FloatPoint((float) renderPosition.getX(), (float) renderPosition.getY())
                        .add(this.sizeHandler.scalePointToGui(this, this.mousePoint, spawn));
                return new Vector3(rPos.x(), rPos.y(), renderPosition.getZ());
            };

            FXAlphaFunction<?> hoverAlpha = (FXAlphaFunction<EntityVisualFX>) (fx, alphaIn, pTicks) -> {
                if (this.hoveredLumen == null ||
                    this.hoveredLumen == connection.source() ||
                    this.hoveredLumen == connection.target()) return alphaIn;
                return alphaIn * 0.1F;
            };

            for (int i = 0; i < 3; i++) {
                float scale = 14F + rand.nextFloat() * 3F;
                int age = 30 + rand.nextInt(15);
                Vector3 mot = Vector3.random(rand).setZ(0).normalize().multiply(0.08F + rand.nextFloat() * 0.02F);
                float x = rand.nextFloat();
                float y = rand.nextFloat();

                ct.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, x, y)
                        .color(FXColorFunction.constant(connection.source().getColor(tick)))
                        .alpha(FXAlphaFunction.fadeIn(6).andThen(FXAlphaFunction.FADE_OUT).andThen(hoverAlpha))
                        .renderOffset(renderOffset)
                        .setScale(scale)
                        .scale(sizeHandlerScale)
                        .setMotion(mot)
                        .setMaxAge(age);

                ct.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, x, y)
                        .alpha(FXAlphaFunction.fadeIn(6).andThen(FXAlphaFunction.FADE_OUT).andThen(hoverAlpha))
                        .renderOffset(renderOffset)
                        .setScale(scale * 0.3F)
                        .scale(sizeHandlerScale)
                        .setMotion(mot)
                        .setMaxAge(Mth.ceil(age * 0.7F));
            }
        });
    }

    private void drawHoveredTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.hoveredLumen = null;

        for (Lumen lumen : this.thisFrameLumen.keySet()) {
            if (this.thisFrameLumen.get(lumen).contains(mouseX, mouseY)) {
                this.hoveredLumen = lumen;
                break;
            }
        }

        if (this.hoveredLumen != null) {
            ColorWrapper color = this.hoveredLumen.getColor(ClientProxy.getClientTick());
            MutableComponent name = this.hoveredLumen.getHoverName().copy()
                    .withColor(color.getColor());

            color = color.darker();
            TooltipUtil.changeColor(0xFF000011, 0xFF000011, color.getColor(), color.getColor(), () -> {
                guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(name), mouseX, mouseY, ItemStack.EMPTY);
            });
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {
        super.mouseDragTick(mouseX, mouseY, mouseDiffX, mouseDiffY, mouseOffsetX, mouseOffsetY);
        this.moveMouse((float) mouseDiffX, (float) mouseDiffY);
    }

    @Override
    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {
        super.mouseDragStop(mouseX, mouseY, mouseDiffX, mouseDiffY);
        this.applyMovedMouseOffset();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0) {
            this.sizeHandler.handleZoomOut();
            this.rescaleMouse();
            return true;
        }
        if (scrollY > 0)  {
            this.sizeHandler.handleZoomIn();
            this.rescaleMouse();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Lumen lumen : this.thisFrameLumen.keySet()) {
                FloatRectangle lumenBox = this.thisFrameLumen.get(lumen);
                if (lumenBox.contains(mouseX, mouseY)) {
                    PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
                    Minecraft.getInstance().setScreen(TomePagesScreen.fromLumen(this, lumen));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void moveMouse(float changeX, float changeY) {
        this.mousePoint.updateScaledPos(
                this.sizeHandler.clampX(this.previousMousePoint.getScaledPosX() + changeX),
                this.sizeHandler.clampY(this.previousMousePoint.getScaledPosY() + changeY),
                this.sizeHandler.getScalingFactor());
    }

    private void applyMovedMouseOffset() {
        this.previousMousePoint = ScalingPoint.createPoint(
                this.mousePoint.getScaledPosX(),
                this.mousePoint.getScaledPosY(),
                this.sizeHandler.getScalingFactor(),
                true);
    }

    private void rescaleMouse() {
        this.mousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.previousMousePoint.rescale(this.sizeHandler.getScalingFactor());
        this.moveMouse(0, 0);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    protected boolean shouldInventoryKeyCloseScreen() {
        return true;
    }

    public record Connection(Lumen source, Lumen target) {

        public boolean maySee() {
            Level level = Minecraft.getInstance().level;
            if (level == null) return false;
            return this.source.maySee(level, ResearchManager.getClientProgress()) &&
                    this.target.maySee(level, ResearchManager.getClientProgress());
        }
    }
}
