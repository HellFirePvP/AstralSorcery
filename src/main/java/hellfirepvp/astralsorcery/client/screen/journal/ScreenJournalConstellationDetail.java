/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageAltarRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.awt.*;
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
    private List<String> locTextMantleCorruption = new LinkedList<>();

    public ScreenJournalConstellationDetail(ScreenJournal origin, IConstellation cst) {
        super(cst.getConstellationName(), NO_BOOKMARK);
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

    public IConstellation getConstellation() {
        return constellation;
    }

    private void buildCapeText() {
        if (this.constellation instanceof IWeakConstellation) {
            if (GatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchHelper.getClientProgress())) {
                String textMantle = ((IWeakConstellation) this.constellation).getInfoMantleEffect().getFormattedText();

                String head = I18n.format("astralsorcery.journal.constellation.mantle");
                locTextMantleCorruption.add(head);
                locTextMantleCorruption.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textMantle.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextMantleCorruption.addAll(lines);
                locTextMantleCorruption.add("");
            }
            if (GatedKnowledge.CONSTELLATION_CORRUPTION.canSee(ResearchHelper.getClientProgress())) {
                String textCorruptedRitual = ((IWeakConstellation) this.constellation).getInfoCorruptedRitualEffect().getFormattedText();

                String head = I18n.format("astralsorcery.journal.constellation.corruption");
                locTextMantleCorruption.add(head);
                locTextMantleCorruption.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textCorruptedRitual.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextMantleCorruption.addAll(lines);
                locTextMantleCorruption.add("");
            }
        }
    }

    private void buildEnchText() {
        if (GatedKnowledge.CONSTELLATION_ENCH_POTION.canSee(ResearchHelper.getClientProgress())) {
            String textEnchantments = this.constellation.getConstellationEnchantmentDescription().getFormattedText();
            String head = I18n.format("astralsorcery.journal.constellation.enchantments");
            locTextRitualEnch.add(head);
            locTextRitualEnch.add("");

            List<String> lines = new LinkedList<>();
            for (String segment : textEnchantments.split("<NL>")) {
                lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                lines.add("");
            }
            locTextRitualEnch.addAll(lines);
            locTextRitualEnch.add("");
        }
    }

    private void buildRitualText() {
        if (GatedKnowledge.CONSTELLATION_RITUAL.canSee(ResearchHelper.getClientProgress())) {
            if (this.constellation instanceof IMinorConstellation) {
                String textRitual = ((IMinorConstellation) this.constellation).getInfoTraitEffect().getFormattedText();
                String head = I18n.format("astralsorcery.journal.constellation.ritual.trait");
                locTextRitualEnch.add(head);
                locTextRitualEnch.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textRitual.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitualEnch.addAll(lines);
            } else if (this.constellation instanceof IWeakConstellation) {
                String textRitual = ((IWeakConstellation) this.constellation).getInfoRitualEffect().getFormattedText();
                String head = I18n.format("astralsorcery.journal.constellation.ritual");
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

    private void buildMainText() {
        String description = this.constellation.getConstellationDescription().getFormattedText();

        List<String> lines = new LinkedList<>();
        for (String segment : description.split("<NL>")) {
            lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        locTextMain.addAll(lines);
    }

    private void testActivePhases() {
        WorldContext ctx = SkyHandler.getContext(Minecraft.getInstance().world, LogicalSide.CLIENT);
        if (ctx == null) {
            return;
        }
        this.activePhases = new LinkedList<>();
        for (MoonPhase phase : MoonPhase.values()) {
            if (ctx.getConstellationHandler().isActiveInPhase(this.constellation, phase)) {
                this.activePhases.add(phase);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        GlStateManager.enableBlend();
        this.lastFramePage = null;

        if (this.doublePageID == 0) {
            drawCstBackground();
            drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_LEFT, mouseX, mouseY);
        } else {
            drawDefault(TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        }

        this.changeZLevel(250);
        TexturesAS.TEX_GUI_BOOK_ARROWS.bindTexture();
        drawBackArrow(pTicks, mouseX, mouseY);
        drawNavArrows(pTicks, mouseX, mouseY);

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
        this.changeZLevel(-250);
    }

    private void drawCapeInformationPages(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < locTextMantleCorruption.size(); i++) {
            String line = locTextMantleCorruption.get(i);
            RenderingDrawUtils.renderStringAtPos(guiLeft + 30, guiTop + 30 + i * 10, font, line, 0xBBCCCCCC, true);
        }

        if (GatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchHelper.getClientProgress())) {
            SimpleAltarRecipe recipe = RecipeHelper.findAltarRecipeResult(stack ->
                    stack.getItem() instanceof ItemMantle &&
                            this.constellation.equals(ItemsAS.MANTLE.getConstellation(stack)));

            if (recipe != null) {
                lastFramePage = new RenderPageAltarRecipe(null, -1, recipe);
                lastFramePage.render    (guiLeft + 220, guiTop + 20, partialTicks, this.getGuiZLevel(), mouseX, mouseY);
                lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, this.getGuiZLevel(), mouseX, mouseY);
            }
        }
    }

    private void drawEnchantingPotionPaperPageInformation(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < locTextRitualEnch.size(); i++) {
            String line = locTextRitualEnch.get(i);
            RenderingDrawUtils.renderStringAtPos(guiLeft + 30, guiTop + 30 + i * 10, font, line, 0xBBCCCCCC, true);
        }

        if (GatedKnowledge.CONSTELLATION_PAPER_CRAFT.canSee(ResearchHelper.getClientProgress())) {
            SimpleAltarRecipe recipe = RecipeHelper.findAltarRecipeResult(stack ->
                    stack.getItem() instanceof ItemConstellationPaper &&
                    this.constellation.equals(ItemsAS.CONSTELLATION_PAPER.getConstellation(stack)));

            if (recipe != null) {
                lastFramePage = new RenderPageAltarRecipe(null, -1, recipe);
                lastFramePage.render    (guiLeft + 220, guiTop + 20, partialTicks, this.getGuiZLevel(), mouseX, mouseY);
                lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, this.getGuiZLevel(), mouseX, mouseY);
            }
        }
    }

    private void drawPageExtendedInformation() {
        String info = this.constellation.getConstellationTag().getFormattedText().toUpperCase();
        info = detailed ? info : "? ? ?";

        int width = font.getStringWidth(info);
        int chX = 305 - (width / 2);
        RenderingDrawUtils.renderStringAtPos(guiLeft + chX, guiTop + 44, font, info, 0xBBCCCCCC, true);

        if (detailed && !locTextMain.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            for (String s : locTextMain) {
                RenderingDrawUtils.renderStringAtPos(guiLeft + offsetX, guiTop + offsetY, font, s, 0xBBCCCCCC, true);
                offsetY += 13;
            }
        }
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

            RenderSystem.pushMatrix();
            RenderSystem.translated(offsetLeft + 10, offsetTop, 0);
            RenderSystem.scaled(scale, scale, scale);
            RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, none, 0xCCDDDDDD);
            RenderSystem.popMatrix();
        } else {
            boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(this.constellation);

            int size = 19;
            int offsetX = 95 + (width / 2) - (MoonPhase.values().length * (size + 2)) / 2;
            int offsetY = 199 + guiTop;

            RenderSystem.enableBlend();
            Blending.DEFAULT.apply();
            MoonPhase[] mPhases = MoonPhase.values();
            for (int i = 0; i < mPhases.length; i++) {
                MoonPhase phase = mPhases[i];
                int index = i;

                float brightness;
                phase.getTexture().bindTexture();
                if (known && this.activePhases.contains(phase)) {
                    Blending.PREALPHA.apply();
                    brightness = 1F;
                } else {
                    Blending.DEFAULT.apply();
                    brightness = 0.7F;
                }
                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                    RenderingGuiUtils.rect(buf, offsetX + (index * (size + 2)), offsetY, this.getGuiZLevel(), size, size)
                            .color(brightness, brightness, brightness, brightness)
                            .draw();
                });
            }
            Blending.DEFAULT.apply();
            RenderSystem.disableBlend();
        }
    }

    private void drawPageConstellation(float partial) {
        String name = this.constellation.getConstellationName().getFormattedText().toUpperCase();
        int width = font.getStringWidth(name);

        RenderSystem.pushMatrix();
        RenderSystem.translated(guiLeft + (305 - (width * 1.8F / 2F)), guiTop + 26, 0);
        RenderSystem.scaled(1.8, 1.8, 1.8);
        RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, name, 0xBBC3C3C3);
        RenderSystem.popMatrix();

        String dstInfo = constellation.getConstellationTypeDescription().getFormattedText();
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = font.getStringWidth(dstInfo);

        RenderingDrawUtils.renderStringAtPos(guiLeft + (305 - (width / 2)), guiTop + 219, font, dstInfo, 0x99DDDDDD, true);

        Random rand = new Random(0x4196A15C91A5E199L);
        boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(constellation);
        RenderingConstellationUtils.renderConstellationIntoGUI(known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation,
                guiLeft + 40, guiTop + 60, this.getGuiZLevel(),
                150, 150, 2F,
                () -> 0.6F + 0.4F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10)),
                true, false);
    }

    private void drawBackArrow(float partialTicks, int mouseX, int mouseY) {
        int width = 30;
        int height = 15;
        rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
        RenderSystem.pushMatrix();
        RenderSystem.translated(rectBack.getX() + (width / 2F), rectBack.getY() + (height / 2F), 0);
        float uFrom, vFrom = 0.5F;
        if (rectBack.contains(mouseX, mouseY)) {
            uFrom = 0.5F;
            RenderSystem.scaled(1.1, 1.1, 1.1);
        } else {
            uFrom = 0F;
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            RenderSystem.scaled(sin, sin, sin);
        }
        RenderSystem.translated(-(width / 2F), -(height / 2F), 0);
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, 0, 0, this.getGuiZLevel(), width, height)
                    .tex(uFrom, vFrom, 0.5F, 0.5F)
                    .color(1F, 1F, 1F, 0.8F)
                    .draw();
        });
        RenderSystem.popMatrix();
    }

    private void drawNavArrows(float partialTicks, int mouseX, int mouseY) {
        rectNext = null;
        rectPrev = null;

        if (doublePageID - 1 >= 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
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
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, 0, 0, this.getGuiZLevel(), width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            RenderSystem.popMatrix();
        }

        if (doublePageID + 1 <= doublePages) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
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
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                RenderingGuiUtils.rect(buf, 0, 0, this.getGuiZLevel(), width, height)
                        .tex(uFrom, vFrom, 0.5F, 0.5F)
                        .color(1F, 1F, 1F, 0.8F)
                        .draw();
            });
            RenderSystem.popMatrix();
        }
    }

    private void drawCstBackground() {
        TexturesAS.TEX_BLACK.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(guiLeft + 15,  guiTop + 240, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 1).endVertex();
            buf.pos(guiLeft + 200, guiTop + 240, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 1).endVertex();
            buf.pos(guiLeft + 200, guiTop + 10,  this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 0).endVertex();
            buf.pos(guiLeft + 15,  guiTop + 10,  this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 0).endVertex();
        });

        RenderSystem.enableBlend();
        TexturesAS.TEX_GUI_BACKGROUND_CONSTELLATIONS.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            buf.pos(guiLeft + 15,  guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0, 1).endVertex();
            buf.pos(guiLeft + 200, guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(1, 1).endVertex();
            buf.pos(guiLeft + 200, guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(1, 0).endVertex();
            buf.pos(guiLeft + 15,  guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0, 0).endVertex();
        });
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().displayGuiScreen(origin);
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
