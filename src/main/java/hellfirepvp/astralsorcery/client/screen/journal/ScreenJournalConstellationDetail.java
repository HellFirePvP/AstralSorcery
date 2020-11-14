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
import hellfirepvp.astralsorcery.client.screen.base.NavigationArrowScreen;
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
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
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
public class ScreenJournalConstellationDetail extends ScreenJournal implements NavigationArrowScreen {

    private final ScreenJournal origin;
    private final IConstellation constellation;
    private final boolean detailed;

    private int doublePageID = 0;
    private int doublePages = 0;
    private List<MoonPhase> activePhases = null;

    private RenderablePage lastFramePage = null;
    private Rectangle rectBack, rectNext, rectPrev;

    private final List<String> locTextMain = new LinkedList<>();
    private final List<String> locTextRitual = new LinkedList<>();
    private final List<String> locTextRefraction = new LinkedList<>();
    private final List<String> locTextMantle = new LinkedList<>();

    public ScreenJournalConstellationDetail(ScreenJournal origin, IConstellation cst) {
        super(cst.getConstellationName(), NO_BOOKMARK);
        this.origin = origin;
        this.constellation = cst;

        this.font = Minecraft.getInstance().fontRenderer;

        this.detailed = ResearchHelper.getClientProgress().hasConstellationDiscovered(cst);

        PlayerProgress playerProgress = ResearchHelper.getClientProgress();
        if (this.detailed) {
            if (playerProgress.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT)) {
                this.doublePages++; //ritual + refTable
            }
            if (playerProgress.getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                if (!(constellation instanceof IMinorConstellation)) {
                    this.doublePages++; //mantle info pages
                }
                this.doublePages++; //constellation paper page
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
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                String textMantle = ((IWeakConstellation) this.constellation).getInfoMantleEffect().getFormattedText();

                String head = I18n.format("astralsorcery.journal.constellation.mantle");
                locTextMantle.add(head);
                locTextMantle.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textMantle.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextMantle.addAll(lines);
                locTextMantle.add("");
            }
        }
    }

    private void buildEnchText() {
        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT)) {
            String textEnchantments = this.constellation.getConstellationEnchantmentDescription().getFormattedText();
            String head = I18n.format("astralsorcery.journal.constellation.enchantments");
            locTextRefraction.add(head);
            locTextRefraction.add("");

            List<String> lines = new LinkedList<>();
            for (String segment : textEnchantments.split("<NL>")) {
                lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                lines.add("");
            }
            locTextRefraction.addAll(lines);
            locTextRefraction.add("");
        }
    }

    private void buildRitualText() {
        if (this.constellation instanceof IMinorConstellation) {
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                String textRitual = ((IMinorConstellation) this.constellation).getInfoTraitEffect().getFormattedText();
                String head = I18n.format("astralsorcery.journal.constellation.ritual.trait");
                locTextRitual.add(head);
                locTextRitual.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textRitual.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitual.addAll(lines);
            }
        } else if (this.constellation instanceof IWeakConstellation) {
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT)) {
                String textRitual = ((IWeakConstellation) this.constellation).getInfoRitualEffect().getFormattedText();
                String head = I18n.format("astralsorcery.journal.constellation.ritual");
                locTextRitual.add(head);
                locTextRitual.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textRitual.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitual.addAll(lines);
                locTextRitual.add("");
            }
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                String textCorruptedRitual = ((IWeakConstellation) this.constellation).getInfoCorruptedRitualEffect().getFormattedText();

                String head = I18n.format("astralsorcery.journal.constellation.corruption");
                locTextRitual.add(head);
                locTextRitual.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textCorruptedRitual.split("<NL>")) {
                    lines.addAll(font.listFormattedStringToWidth(segment, JournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextRitual.addAll(lines);
                locTextRitual.add("");
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
        this.lastFramePage = null;

        if (this.doublePageID == 0) {
            drawCstBackground();
            drawDefault(TexturesAS.TEX_GUI_BOOK_FRAME_LEFT, mouseX, mouseY);
        } else {
            drawDefault(TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        }

        drawNavArrows(pTicks, mouseX, mouseY);

        this.setBlitOffset(120);
        switch (doublePageID) {
            case 0:
                drawPageConstellation(pTicks);
                drawPagePhaseInformation();
                drawPageExtendedInformation();
                break;
            case 1:
                drawRitualRefractionTableInformation(mouseX, mouseY, pTicks);
                break;
            case 2:
                drawCapeInformationPages(mouseX, mouseY, pTicks);
                break;
            case 3:
                drawConstellationPaperRecipePage(mouseX, mouseY, pTicks);
                break;
            default:
                break;
        }
        this.setBlitOffset(0);
    }

    private void drawConstellationPaperRecipePage(int mouseX, int mouseY, float pTicks) {
        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
            SimpleAltarRecipe recipe = RecipeHelper.findAltarRecipeResult(stack ->
                    stack.getItem() instanceof ItemConstellationPaper &&
                            this.constellation.equals(ItemsAS.CONSTELLATION_PAPER.getConstellation(stack)));

            if (recipe != null) {
                lastFramePage = new RenderPageAltarRecipe(null, -1, recipe);
                lastFramePage.render    (guiLeft + 30, guiTop + 20, pTicks, this.getGuiZLevel(), mouseX, mouseY);
                lastFramePage.postRender(guiLeft + 30, guiTop + 20, pTicks, this.getGuiZLevel(), mouseX, mouseY);
            }
        }
    }

    private void drawCapeInformationPages(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < locTextMantle.size(); i++) {
            String line = locTextMantle.get(i);
            RenderingDrawUtils.renderStringAtPos(guiLeft + 30, guiTop + 30 + i * 10, this.getGuiZLevel(), font, line, 0xFFCCCCCC, true);
        }

        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
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

    private void drawRitualRefractionTableInformation(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < locTextRitual.size(); i++) {
            String line = locTextRitual.get(i);
            RenderingDrawUtils.renderStringAtPos(guiLeft + 30, guiTop + 30 + i * 10, this.getGuiZLevel(), font, line, 0xFFCCCCCC, true);
        }

        for (int i = 0; i < locTextRefraction.size(); i++) {
            String line = locTextRefraction.get(i);
            RenderingDrawUtils.renderStringAtPos(guiLeft + 220, guiTop + 30 + i * 10, this.getGuiZLevel(), font, line, 0xFFCCCCCC, true);
        }
    }

    private void drawPageExtendedInformation() {
        String info = this.constellation.getConstellationTag().getFormattedText().toUpperCase();
        info = detailed ? info : "? ? ?";

        int width = font.getStringWidth(info);
        int chX = 305 - (width / 2);
        RenderingDrawUtils.renderStringAtPos(guiLeft + chX, guiTop + 44, this.getGuiZLevel(), font, info, 0xFFCCCCCC, true);

        if (detailed && !locTextMain.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            for (String s : locTextMain) {
                RenderingDrawUtils.renderStringAtPos(guiLeft + offsetX, guiTop + offsetY, this.getGuiZLevel(), font, s, 0xFFCCCCCC, true);
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
        RenderSystem.translated(guiLeft + (305 - (width * 1.8F / 2F)), guiTop + 26, this.getGuiZLevel());
        RenderSystem.scaled(1.8, 1.8, 1);
        RenderingDrawUtils.renderStringWithShadowAtCurrentPos(font, name, 0xFFC3C3C3);
        RenderSystem.popMatrix();

        String dstInfo = constellation.getConstellationTypeDescription().getFormattedText();
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = font.getStringWidth(dstInfo);

        RenderingDrawUtils.renderStringAtPos(guiLeft + (305 - (width / 2)), guiTop + 219, this.getGuiZLevel(), font, dstInfo, 0xFFDDDDDD, true);

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        Random rand = new Random(0x4196A15C91A5E199L);
        boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(constellation);
        RenderingConstellationUtils.renderConstellationIntoGUI(known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation,
                guiLeft + 40, guiTop + 60, this.getGuiZLevel(),
                150, 150, 2F,
                () -> 0.6F + 0.4F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10)),
                true, false);
        RenderSystem.disableBlend();
    }

    private void drawNavArrows(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        this.rectNext = null;
        this.rectPrev = null;
        this.rectBack = this.drawArrow(guiLeft + 197, guiTop + 230, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);

        if (doublePageID - 1 >= 0) {
            this.rectPrev = this.drawArrow(guiLeft + 25, guiTop + 220, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);
        }

        if (doublePageID + 1 <= doublePages) {
            this.rectNext = this.drawArrow(guiLeft + 367, guiTop + 220, this.getGuiZLevel(), Type.RIGHT, mouseX, mouseY, partialTicks);
        }

        RenderSystem.disableBlend();
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
            buf.pos(guiLeft + 15,  guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.3F, 0.9F).endVertex();
            buf.pos(guiLeft + 200, guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.7F, 0.9F).endVertex();
            buf.pos(guiLeft + 200, guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.7F, 0.1F).endVertex();
            buf.pos(guiLeft + 15,  guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.7F).tex(0.3F, 0.1F).endVertex();
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
