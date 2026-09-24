/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.element.TomeNavArrowElement;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderConstellationUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.DebugConstellation;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeConstellationScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeConstellationScreen extends TomeScreen {

    public static final BookmarkProvider BOOKMARK = new BookmarkProvider("constellations", 20,
            () -> Minecraft.getInstance().setScreen(TomeConstellationScreen.getScreen()),
            () -> !ResearchManager.getClientProgress().getSeenConstellations().isEmpty());
    private static final List<IntPoint> OFFSETS = List.of(
            new IntPoint(45, 50),
            new IntPoint(125, 115),
            new IntPoint(200, 45),
            new IntPoint(285, 120)
    );

    private final List<BaseConstellation> constellations;
    private int page = 0;

    private final Map<IntRectangle, BaseConstellation> thisFrameConstellations = new HashMap<>();
    private TomeNavArrowElement pageLeft, pageRight;

    private TomeConstellationScreen(List<BaseConstellation> constellations) {
        super(BOOKMARK.getBookmarkIndex());
        this.constellations = new ArrayList<>(constellations);
        this.constellations.sort(Comparator.comparing(BaseConstellation::getSortingId));
    }

    public static TomeConstellationScreen getScreen() {
        return new TomeConstellationScreen(ResearchManager.getClientProgress().getSeenConstellations().stream().toList());
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        this.initBookmarks();
        this.initNavArrows();
    }

    private void initNavArrows() {
        this.pageLeft = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) - 170, this.screenTop + 230, false, () -> {
            this.page = Math.max(0, this.page - 1);
        });
        this.pageRight = new TomeNavArrowElement(this.screenLeft + (this.screenWidth / 2) + 170, this.screenTop + 230, true, () -> {
            this.page++;
        });
        this.pageLeft.visible = false;
        this.pageRight.visible = false;
        this.addRenderableWidget(this.pageLeft);
        this.addRenderableWidget(this.pageRight);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.thisFrameConstellations.clear();
        this.renderTransparentBackground(guiGraphics);
        this.pageLeft.visible = this.page > 0;
        this.pageRight.visible = (this.page + 1) * 4 < this.constellations.size();

        ScreenRectangle rect = this.getScreenRectangle();
        guiGraphics.enableScissor(rect.left() + 20, rect.top() + 20, rect.right() - 20, rect.bottom() - 20);
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_TOME_BACKGROUND_CONSTELLATION, ColorWrapper.opaque(0xAAAAAA), rect);
        RenderSystem.disableBlend();
        guiGraphics.disableScissor();

        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_FRAME_CUTOUT, this.getScreenRectangle());
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        this.drawConstellations(guiGraphics, partialTick, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    private void drawConstellations(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        int size = 90;
        PlayerProgress progress = ResearchManager.getClientProgress();

        List<BaseConstellation> pageConstellations = this.constellations.subList(this.page * 4, Math.min((this.page + 1) * 4, this.constellations.size()));
        for (int i = 0; i < pageConstellations.size(); i++) {
            BaseConstellation cst = pageConstellations.get(i);
            IntPoint offset = OFFSETS.get(i);
            IntRectangle rct = new IntRectangle(this.getScreenLeft() + offset.x(), this.getScreenTop() + offset.y(), size, size);
            this.thisFrameConstellations.put(rct, cst);
            boolean hovered = rct.contains(mouseX, mouseY);
            boolean discovered = progress.hasDiscoveredConstellation(cst);
            ColorWrapper color = discovered ? cst.getConstellationColor() : ColorsAS.CONSTELLATION_TYPE_BLANK;

            pose.pushPose();
            pose.translate(rct.x(), rct.y(), 0F);
            pose.translate(size / 2F, size / 2F, 0F);
            if (hovered) {
                pose.scale(1.15F, 1.15F, 1F);
                color = ColorUtil.blendColors(color, ColorWrapper.WHITE, 0.95F);
            }
            pose.translate(-size / 2F, -size / 2F, 0F);

            RandomSource rand = RandomSource.create(cst.getName().hashCode());
            RenderConstellationUtil.drawConstellationUI(color, cst, pose,
                    0, 0,
                    size, size,
                    2.4F, () -> 0.7F + 0.3F * EffectUtil.flicker(0.04F + rand.nextFloat() * 0.02F, partialTick),
                    true, false);
            pose.popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (IntRectangle rct : this.thisFrameConstellations.keySet()) {
                if (rct.contains(mouseX, mouseY)) {
                    BaseConstellation cst = this.thisFrameConstellations.get(rct);
                    PlayableSoundInstance.of(SoundsAS.SCREEN_TOME_PAGE).forUI().play();
                    Minecraft.getInstance().setScreen(TomePagesScreen.fromConstellation(this, cst));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    protected boolean shouldInventoryKeyCloseScreen() {
        return true;
    }
}
