/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalConstellationDetail
 * Created by HellFirePvP
 * Date: 04.08.2019 / 10:54
 */
public class ScreenJournalConstellationDetail extends ScreenJournal {

    private final ScreenJournal origin;
    private final IConstellation constellation;
    private final boolean detailed;

    private int doublePageID = 0;
    private int doublePages = 0;
    private List<MoonPhase> activePhases = null;

    private RenderablePage lastFramePage = null;
    private Rectangle rectBack, rectNext, rectPrev;

    private List<String> locTextMain = new LinkedList<>();
    private List<String> locTextRitualEnch = new LinkedList<>();
    private List<String> locTextCapeEffect = new LinkedList<>();

    public ScreenJournalConstellationDetail(ScreenJournal origin, IConstellation cst) {
        super(new TranslationTextComponent(cst.getUnlocalizedName()), NO_BOOKMARK);
        this.origin = origin;
        this.constellation = cst;

        this.font = Minecraft.getInstance().fontRenderer;

        this.detailed = ResearchHelper.getClientProgress().hasConstellationDiscovered(cst);

        PlayerProgress playerProgress = ResearchHelper.getClientProgress();
        if (this.detailed && (GatedKnowledge.CONSTELLATION_RITUAL.canSee(playerProgress) || GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(playerProgress))) {
            this.doublePages++;

            if (GatedKnowledge.CONSTELLATION_CAPE.canSee(playerProgress) && !(constellation instanceof IMinorConstellation)) {
                this.doublePages++;
            }
        }

        testActivePhases();
        buildMainText();
        buildEnchText();
        buildRitualText();
        buildCapeText();
    }

    private void buildCapeText() {
        if (GatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchHelper.getClientProgress())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".capeeffect";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.capeeffect");
                locTextCapeEffect.add(head);
                locTextCapeEffect.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextCapeEffect.addAll(lines);
                locTextCapeEffect.add("");
            }
        }
    }

    private void buildEnchText() {
        if (GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(ResearchHelper.getClientProgress())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".enchantments";
            String textEnch = I18n.format(unlocEnch);
            if (!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.enchantments");
                locTextRitualEnch.add(head);
                locTextRitualEnch.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitualEnch.addAll(lines);
                locTextRitualEnch.add("");
            }
        }
    }

    private void buildRitualText() {
        if (GatedKnowledge.CONSTELLATION_RITUAL.canSee(ResearchHelper.getClientProgress())) {
            if (constellation instanceof IMinorConstellation) {
                String unlocRitual = constellation.getUnlocalizedName() + ".trait";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual.trait");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            } else {
                String unlocRitual = constellation.getUnlocalizedName() + ".ritual";
                String textRitual = I18n.format(unlocRitual);
                if (!unlocRitual.equals(textRitual)) {
                    String head = I18n.format("gui.journal.cst.ritual");
                    locTextRitualEnch.add(head);
                    locTextRitualEnch.add("");

                    List<String> lines = new LinkedList<>();
                    for (String segment : textRitual.split("<NL>")) {
                        lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                        lines.add("");
                    }
                    locTextRitualEnch.addAll(lines);
                }
            }
        }
    }

    private void buildMainText() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if (unloc.equals(text)) {
            return;
        }

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        locTextMain.addAll(lines);
    }

    private void testActivePhases() {
        WorldContext ctx = SkyHandler.getContext(Minecraft.getInstance().world, Dist.CLIENT);
        if (ctx == null) {
            return;
        }
        this.activePhases = new LinkedList<>();
        for (MoonPhase phase : MoonPhase.values()) {
            if (ctx.getConstellationHandler().isActive(this.constellation, phase)) {
                this.activePhases.add(phase);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        this.lastFramePage = null;

        GlStateManager.enableBlend();

        if (this.doublePageID == 0) {
            drawCstBackground();
            drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_LEFT, mouseX, mouseY);
        } else {
            drawDefault(TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        }

        this.blitOffset += 250;
        GlStateManager.color4f(1F, 1F, 1F, 0.8F);
        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
        drawBackArrow(pTicks, mouseX, mouseY);
        drawNavArrows(pTicks, mouseX, mouseY);
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        switch (doublePageID) {
            case 0:
                drawPageConstellation(pTicks);
                drawPagePhaseInformation();
                drawPageExtendedInformation();
                break;
            case 1:
                drawEnchantingPotionPaperPageInformation(mouseX, mouseY, pTicks);
                break;
            case 2:
                drawCapeInformationPages(mouseX, mouseY, pTicks);
                break;
            default:
                break;
        }
        this.blitOffset -= 250;

        GlStateManager.disableBlend();
    }

    private void drawCapeInformationPages(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(0.86F, 0.86F, 0.86F, 0.8F);
        GlStateManager.disableDepthTest();

        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + 30, guiTop + 30, 0);
        for (int i = 0; i < locTextCapeEffect.size(); i++) {
            String line = locTextCapeEffect.get(i);
            RenderingDrawUtils.renderStringAtPos(0, i * 10, font, line, 0xCCDDDDDD, true);
        }
        GlStateManager.popMatrix();

        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        //TODO cape recipe page
        //CapeAttunementRecipe recipe = RecipesAS.capeCraftingRecipes.get(this.constellation);
        //if (recipe != null) {
        //    lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();
        //    GlStateManager.pushMatrix();
        //    lastFramePage.render    (guiLeft + 220, guiTop + 20, partialTicks, this.blitOffset, mouseX, mouseY);
        //    lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, this.blitOffset, mouseX, mouseY);
        //    GlStateManager.popMatrix();
        //}
    }

    private void drawEnchantingPotionPaperPageInformation(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color4f(0.86F, 0.86F, 0.86F, 0.8F);
        GlStateManager.disableDepthTest();

        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + 30, guiTop + 30, 0);
        for (int i = 0; i < locTextRitualEnch.size(); i++) {
            String line = locTextRitualEnch.get(i);
            RenderingDrawUtils.renderStringAtPos(0, i * 10, font, line, 0xCCDDDDDD, true);
        }
        GlStateManager.popMatrix();

        GlStateManager.enableDepthTest();
        GlStateManager.color4f(1F, 1F, 1F, 1F);

        if (GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(ResearchHelper.getClientProgress())) {
            //TODO altar recipe pages
            //ConstellationPaperRecipe recipe = RecipesAS.paperCraftingRecipes.get(this.constellation);
            //if (recipe != null) {
            //    lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();
            //    GlStateManager.pushMatrix();
            //    lastFramePage.render    (guiLeft + 220, guiTop + 20, partialTicks, this.blitOffset, mouseX, mouseY);
            //    lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, this.blitOffset, mouseX, mouseY);
            //    GlStateManager.popMatrix();
            //}
        }
    }

    private void drawPageExtendedInformation() {
        float brightness = 0.85F;
        GlStateManager.color4f(brightness, brightness, brightness, 0.85F);
        GlStateManager.disableDepthTest();

        String info = I18n.format(this.constellation.getUnlocalizedInfo()).toUpperCase();
        info = detailed ? info : "? ? ?";

        double width = font.getStringWidth(info);
        double chX = 305 - (width / 2);
        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + chX, guiTop + 44, 0);
        RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, info, 0xCCDDDDDD);
        GlStateManager.popMatrix();

        if (detailed && !locTextMain.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            for (String s : locTextMain) {
                RenderingDrawUtils.renderStringAtPos(guiLeft + offsetX, guiTop + offsetY, font, s, 0xCCDDDDDD, true);
                offsetY += 13;
            }
        }

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.enableDepthTest();
    }

    private void drawPagePhaseInformation() {
        if (this.activePhases == null) {
            this.testActivePhases();
            if (this.activePhases == null) {
                return;
            }
        }

        List<MoonPhase> phases = this.activePhases;
        if (phases.isEmpty()) {

            String none = "? ? ?";
            double scale = 1.8;
            double length = font.getStringWidth(none) * scale;
            double offsetLeft = guiLeft + 296 - length / 2;
            int offsetTop = guiTop + 199;

            GlStateManager.disableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translated(offsetLeft + 10, offsetTop, 0);
            GlStateManager.scaled(scale, scale, scale);
            RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, none, 0xCCDDDDDD);
            GlStateManager.popMatrix();
            GlStateManager.enableDepthTest();
        } else {
            boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(this.constellation);

            int size = 19;
            int offsetX = 95 + (width / 2) - (MoonPhase.values().length * (size + 2)) / 2;
            int offsetY = 199 + guiTop;

            Blending.DEFAULT.applyStateManager();
            MoonPhase[] mPhases = MoonPhase.values();
            for (int i = 0; i < mPhases.length; i++) {
                MoonPhase phase = mPhases[i];

                phase.getTexture().bindTexture();
                if (known && this.activePhases.contains(phase)) {
                    Blending.PREALPHA.applyStateManager();
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                } else {
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.color4f(0.7F, 0.7F, 0.7F, 0.6F);
                }
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }

            GlStateManager.color4f(1F, 1F, 1F, 1F);
        }
    }

    private void drawPageConstellation(float partial) {
        float br = 0.866F;
        GlStateManager.color4f(br, br, br, 0.8F);
        GlStateManager.disableDepthTest();

        String name = I18n.format(constellation.getUnlocalizedName()).toUpperCase();
        double width = font.getStringWidth(name);
        double offsetX = 305 - (width * 1.8 / 2);

        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + offsetX, guiTop + 26, 0);
        GlStateManager.scaled(1.8, 1.8, 1.8);
        RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, name, 0xEEDDDDDD);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();

        String dstInfo = I18n.format(constellation.getUnlocalizedType());
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = font.getStringWidth(dstInfo);
        offsetX = 305 - (width / 2);

        GlStateManager.pushMatrix();
        GlStateManager.translated(guiLeft + offsetX, guiTop + 219, 0);
        RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, dstInfo, 0x99DDDDDD);
        GlStateManager.popMatrix();

        GlStateManager.enableDepthTest();

        Random rand = new Random(0x4196A15C91A5E199L);

        Blending.DEFAULT.applyStateManager();

        boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(constellation);
        RenderingConstellationUtils.renderConstellationIntoGUI(known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation,
                guiLeft + 40, guiTop + 60, this.blitOffset,
                150, 150, 2F,
                () -> 0.3F + 0.7F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10)),
                true, false);

        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }

    private void drawBackArrow(float partialTicks, int mouseX, int mouseY) {
        int width = 30;
        int height = 15;
        rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
        GlStateManager.pushMatrix();
        GlStateManager.translated(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);
        float uFrom = 0F, vFrom = 0.5F;
        if(rectBack.contains(mouseX, mouseY)) {
            uFrom = 0.5F;
            GlStateManager.scaled(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GlStateManager.scaled(sin, sin, sin);
        }
        GlStateManager.translated(-(width / 2), -(height / 2), 0);
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GlStateManager.popMatrix();
    }

    private void drawNavArrows(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.disableDepthTest();

        rectNext = null;
        rectPrev = null;

        if (doublePageID - 1 >= 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translated(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if (rectPrev.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                GlStateManager.scaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scaled(sin, sin, sin);
            }

            GlStateManager.translated(-(width / 2), -(height / 2), 0);
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }

        if (doublePageID + 1 <= doublePages) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translated(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if (rectNext.contains(mouseX, mouseY)) {
                uFrom = 0.5F;
                GlStateManager.scaled(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scaled(sin, sin, sin);
            }
            GlStateManager.translated(-(width / 2), -(height / 2), 0);
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableDepthTest();
    }

    private void drawCstBackground() {
        TexturesAS.TEX_BLACK.bindTexture();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 15,  guiTop + 240, this.blitOffset).tex(0, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 240, this.blitOffset).tex(1, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 10,  this.blitOffset).tex(1, 0).endVertex();
        bb.pos(guiLeft + 15,  guiTop + 10,  this.blitOffset).tex(0, 0).endVertex();
        tes.draw();

        GlStateManager.color4f(0.8F, 0.8F, 1F, 0.7F);

        TexturesAS.TEX_GUI_BACKGROUND_CONSTELLATIONS.bindTexture();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 35,       guiTop + guiHeight - 10, this.blitOffset).tex(0.3, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + guiHeight - 10, this.blitOffset).tex(0.7, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + 10,             this.blitOffset).tex(0.7, 0.1).endVertex();
        bb.pos(guiLeft + 35,       guiTop + 10,             this.blitOffset).tex(0.3, 0.1).endVertex();
        tes.draw();

        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }

    @Override
    protected boolean handleRightClickClose(double mouseX, double mouseY) {
        Minecraft.getInstance().displayGuiScreen(origin);
        return true;
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

        if (rectBack != null && rectBack.contains(mouseX, mouseY)) {
            Minecraft.getInstance().displayGuiScreen(origin);
            return true;
        }
        if (rectPrev != null && rectPrev.contains(mouseX, mouseY)) {
            if (doublePageID >= 1) {
                this.doublePageID--;
            }
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        if (rectNext != null && rectNext.contains(mouseX, mouseY)) {
            if (doublePageID <= doublePages - 1) {
                this.doublePageID++;
            }
            SoundHelper.playSoundClient(SoundsAS.GUI_JOURNAL_PAGE, 1F, 1F);
            return true;
        }
        if (doublePageID != 0 && lastFramePage != null) {
            if (lastFramePage.propagateMouseClick(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

}
