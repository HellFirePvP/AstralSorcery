/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalConstellationOverview
 * Created by HellFirePvP
 * Date: 04.08.2019 / 09:36
 */
public class ScreenJournalConstellationOverview extends ScreenJournal {

    private static final int CONSTELLATIONS_PER_PAGE = 4;

    private static final int width = 80, height = 110;
    private static final Map<Integer, Point> offsetMap = new HashMap<>();

    private List<IConstellation> constellations;
    private final int pageId;

    private Map<Rectangle, IConstellation> rectCRenderMap = new HashMap<>();
    private Rectangle rectPrev, rectNext;

    private ScreenJournalConstellationOverview(int pageId, List<IConstellation> constellations) {
        super(new TranslationTextComponent("screen.astralsorcery.tome.constellations"), 20);
        this.constellations = constellations;
        this.pageId = pageId;
    }

    private ScreenJournalConstellationOverview(List<IConstellation> constellations) {
        this(0, constellations);
    }

    public static ScreenJournal getConstellationScreen() {
        PlayerProgress client = ResearchHelper.getClientProgress();
        return new ScreenJournalConstellationOverview(client.getSeenConstellations()
                .stream()
                .map(ConstellationRegistry::getConstellation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        drawConstellationBackground();
        drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_FULL, mouseX, mouseY);

        this.changeZLevel(250);
        drawNavArrows(pTicks, mouseX, mouseY);
        drawConstellations(pTicks, mouseX, mouseY);
        this.changeZLevel(-250);
    }

    private void drawConstellationBackground() {
        TexturesAS.TEX_BLACK.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(guiLeft + 15,            guiTop + guiHeight - 10, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 1).endVertex();
            buf.pos(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 1).endVertex();
            buf.pos(guiLeft + guiWidth - 15, guiTop + 10,             this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 0).endVertex();
            buf.pos(guiLeft + 15,            guiTop + 10,             this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 0).endVertex();
        });

        RenderSystem.enableBlend();
        TexturesAS.TEX_GUI_BACKGROUND_CONSTELLATIONS.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(guiLeft + 15,            guiTop + guiHeight - 10, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.1F, 0.9F).endVertex();
            buf.pos(guiLeft + guiWidth - 15, guiTop + guiHeight - 10, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.9F, 0.9F).endVertex();
            buf.pos(guiLeft + guiWidth - 15, guiTop + 10,             this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.9F, 0.1F).endVertex();
            buf.pos(guiLeft + 15,            guiTop + 10,             this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.1F, 0.1F).endVertex();
        });
        RenderSystem.disableBlend();
    }

    private void drawConstellations(float partial, int mouseX, int mouseY) {
        this.rectCRenderMap.clear();
        List<IConstellation> cs = constellations.subList(pageId * CONSTELLATIONS_PER_PAGE, Math.min((pageId + 1) * CONSTELLATIONS_PER_PAGE, constellations.size()));
        for (int i = 0; i < cs.size(); i++) {
            IConstellation c = cs.get(i);
            Point p = offsetMap.get(i);
            Rectangle cstRct = drawConstellation(c, guiLeft + p.x, guiTop + p.y, this.getGuiZLevel(), partial, mouseX, mouseY);
            rectCRenderMap.put(cstRct, c);
        }
    }

    private Rectangle drawConstellation(IConstellation display, double offsetX, double offsetY, float zLevel, float partial, int mouseX, int mouseY) {
        Rectangle rect = new Rectangle(MathHelper.floor(offsetX), MathHelper.floor(offsetY), width, height);

        RenderSystem.pushMatrix();
        RenderSystem.translated(offsetX + (width / 2F), offsetY + (width / 2F), zLevel);
        if (rect.contains(mouseX, mouseY)) {
            RenderSystem.scaled(1.1, 1.1, 1.1);
        }
        RenderSystem.translated(-(width / 2F), -(width / 2F), zLevel);

        Random rand = new Random(0x4196A15C91A5E199L);
        RenderingConstellationUtils.renderConstellationIntoGUI(display.getConstellationColor(), display,
                0, 0, 0,
                95, 95, 1.6F,
                () -> 0.5F + 0.5F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10)),
                true, false);

        String trName = display.getConstellationName().getFormattedText().toUpperCase();
        float fullLength = (width / 2F) - (((float) font.getStringWidth(trName)) / 2F);
        RenderingDrawUtils.renderStringAtPos(MathHelper.floor(fullLength), 90, font, trName, 0xBBDDDDDD, false);

        RenderSystem.popMatrix();
        return rect;
    }

    private void drawNavArrows(float partialTicks, int mouseX, int mouseY) {
        int cIndex = pageId * CONSTELLATIONS_PER_PAGE;
        rectNext = null;
        rectPrev = null;

        if (cIndex > 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 15, guiTop + 127, width, height);
            RenderSystem.pushMatrix();
            RenderSystem.translated(rectPrev.getX() + (width / 2F), rectPrev.getY() + (height / 2F), 0);
            float uFrom, vFrom = 0.5F;
            if (rectPrev.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                RenderSystem.scaled(1.1, 1.1, 1.1);
            } else {
                uFrom = 0F;
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                RenderSystem.scaled(sin, sin, sin);
            }

            RenderSystem.translated(-(width / 2F), -(height / 2F), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, 0, 0, this.getGuiZLevel(), width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            RenderSystem.popMatrix();
        }
        int nextIndex = cIndex + CONSTELLATIONS_PER_PAGE;
        if (constellations.size() >= (nextIndex + 1)) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 127, width, height);
            RenderSystem.pushMatrix();
            RenderSystem.translated(rectNext.getX() + (width / 2F), rectNext.getY() + (height / 2F), 0);
            float uFrom, vFrom = 0F;
            if (rectNext.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                RenderSystem.scaled(1.1, 1.1, 1.1);
            } else {
                uFrom = 0F;
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                RenderSystem.scaled(sin, sin, sin);
            }
            RenderSystem.translated(-(width / 2F), -(height / 2F), 0);
            TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, 0, 0, this.getGuiZLevel(), width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            RenderSystem.popMatrix();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if (mouseButton != 0) {
            return false;
        }
        if (handleBookmarkClick(mouseX, mouseY)) {
            return true;
        }
        for (Rectangle r : rectCRenderMap.keySet()) {
            if (r.contains(mouseX, mouseY)) {
                IConstellation c = rectCRenderMap.get(r);
                Minecraft.getInstance().displayGuiScreen(new ScreenJournalConstellationDetail(this, c));
            }
        }
        if (rectPrev != null && rectPrev.contains(mouseX, mouseY)) {
            Minecraft.getInstance().displayGuiScreen(new ScreenJournalConstellationOverview(pageId - 1, constellations));
            return true;
        }
        if (rectNext != null && rectNext.contains(mouseX, mouseY)) {
            Minecraft.getInstance().displayGuiScreen(new ScreenJournalConstellationOverview(pageId + 1, constellations));
            return true;
        }
        return false;
    }

    static {
        offsetMap.put(0, new Point(45, 55));
        offsetMap.put(1, new Point(125, 105));
        offsetMap.put(2, new Point(200, 45));
        offsetMap.put(3, new Point(280, 110));
    }
}
