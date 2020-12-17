/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
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

    private final List<IReorderingProcessor> locTextMain = new ArrayList<>();
    private final List<IReorderingProcessor> locTextRitual = new ArrayList<>();
    private final List<IReorderingProcessor> locTextRefraction = new ArrayList<>();
    private final List<IReorderingProcessor> locTextMantle = new ArrayList<>();

    public ScreenJournalConstellationDetail(ScreenJournal origin, IConstellation cst) {
        super(cst.getConstellationName(), NO_BOOKMARK);
        this.origin = origin;
        this.constellation = cst;

        this.font = Minecraft.getInstance().fontRenderer;

        this.detailed = ResearchHelper.getClientProgress().hasConstellationDiscovered(cst);

        PlayerProgress playerProgress = ResearchHelper.getClientProgress();
        if (this.detailed) {
            if (playerProgress.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT)) {
                this.doublePages++;
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
                ITextComponent txtMantle = ((IWeakConstellation) this.constellation).getInfoMantleEffect();

                ITextProperties headTxt = new TranslationTextComponent("astralsorcery.journal.constellation.mantle");
                locTextMantle.add(localize(headTxt));
                locTextMantle.add(IReorderingProcessor.field_242232_a);

                List<IReorderingProcessor> lines = new LinkedList<>();
                for (String segment : txtMantle.getString().split("<NL>")) {
                    lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
                    lines.add(IReorderingProcessor.field_242232_a);
                }
                locTextMantle.addAll(lines);
                locTextMantle.add(IReorderingProcessor.field_242232_a);
            }
        }
    }

    private void buildEnchText() {
        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT)) {
            ITextComponent txtEnchantments = this.constellation.getConstellationEnchantmentDescription();

            ITextProperties headTxt = new TranslationTextComponent("astralsorcery.journal.constellation.enchantments");
            locTextRefraction.add(localize(headTxt));
            locTextRefraction.add(IReorderingProcessor.field_242232_a);

            List<IReorderingProcessor> lines = new LinkedList<>();
            for (String segment : txtEnchantments.getString().split("<NL>")) {
                lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
                lines.add(IReorderingProcessor.field_242232_a);
            }
            locTextRefraction.addAll(lines);
            locTextRefraction.add(IReorderingProcessor.field_242232_a);
        }
    }

    private void buildRitualText() {
        if (this.constellation instanceof IMinorConstellation) {
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                ITextComponent txtRitual = ((IMinorConstellation) this.constellation).getInfoTraitEffect();

                ITextProperties headTxt = new TranslationTextComponent("astralsorcery.journal.constellation.ritual.trait");
                locTextRitual.add(localize(headTxt));
                locTextRitual.add(IReorderingProcessor.field_242232_a);

                List<IReorderingProcessor> lines = new LinkedList<>();
                for (String segment : txtRitual.getString().split("<NL>")) {
                    lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
                    lines.add(IReorderingProcessor.field_242232_a);
                }
                locTextRitual.addAll(lines);
            }
        } else if (this.constellation instanceof IWeakConstellation) {
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT)) {
                ITextComponent txtRitual = ((IWeakConstellation) this.constellation).getInfoRitualEffect();

                ITextProperties headTxt = new TranslationTextComponent("astralsorcery.journal.constellation.ritual");
                locTextRitual.add(localize(headTxt));
                locTextRitual.add(IReorderingProcessor.field_242232_a);

                List<IReorderingProcessor> lines = new LinkedList<>();
                for (String segment : txtRitual.getString().split("<NL>")) {
                    lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
                    lines.add(IReorderingProcessor.field_242232_a);
                }
                locTextRitual.addAll(lines);
                locTextRitual.add(IReorderingProcessor.field_242232_a);
            }
            if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
                ITextComponent txtCorruptedRitual = ((IWeakConstellation) this.constellation).getInfoCorruptedRitualEffect();

                ITextProperties headTxt = new TranslationTextComponent("astralsorcery.journal.constellation.corruption");
                locTextRitual.add(localize(headTxt));
                locTextRitual.add(IReorderingProcessor.field_242232_a);

                List<IReorderingProcessor> lines = new LinkedList<>();
                for (String segment : txtCorruptedRitual.getString().split("<NL>")) {
                    lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
                    lines.add(IReorderingProcessor.field_242232_a);
                }
                locTextRitual.addAll(lines);
                locTextRitual.add(IReorderingProcessor.field_242232_a);
            }
        }
    }

    private void buildMainText() {
        ITextComponent txtDescription = this.constellation.getConstellationDescription();

        List<IReorderingProcessor> lines = new LinkedList<>();
        for (String segment : txtDescription.getString().split("<NL>")) {
            lines.addAll(font.trimStringToWidth(new StringTextComponent(segment), JournalPage.DEFAULT_WIDTH));
            lines.add(IReorderingProcessor.field_242232_a);
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
    public void render(MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
        this.lastFramePage = null;

        if (this.doublePageID == 0) {
            drawCstBackground(renderStack);
            drawDefault(renderStack, TexturesAS.TEX_GUI_BOOK_FRAME_LEFT, mouseX, mouseY);
        } else {
            drawDefault(renderStack, TexturesAS.TEX_GUI_BOOK_BLANK, mouseX, mouseY);
        }

        drawNavArrows(renderStack, pTicks, mouseX, mouseY);

        this.setBlitOffset(120);
        switch (doublePageID) {
            case 0:
                drawPageConstellation(renderStack, pTicks);
                drawPagePhaseInformation(renderStack);
                drawPageExtendedInformation(renderStack);
                break;
            case 1:
                drawRefractionTableInformation(renderStack, mouseX, mouseY, pTicks);
                break;
            case 2:
                drawCapeInformationPages(renderStack, mouseX, mouseY, pTicks);
                if (this.constellation instanceof IMinorConstellation) { //Doesn't have a 3rd double page
                    drawConstellationPaperRecipePage(renderStack, mouseX, mouseY, pTicks);
                }
                break;
            case 3:
                drawConstellationPaperRecipePage(renderStack, mouseX, mouseY, pTicks);
                break;
            default:
                break;
        }
        this.setBlitOffset(0);
    }

    private void drawRefractionTableInformation(MatrixStack renderStack, int mouseX, int mouseY, float pTicks) {
        for (int i = 0; i < locTextRitual.size(); i++) {
            IReorderingProcessor line = locTextRitual.get(i);
            renderStack.push();
            renderStack.translate(guiLeft + 30, guiTop + 30 + i * 10, this.getGuiZLevel());
            RenderingDrawUtils.renderStringAt(line, renderStack, font, 0xFFCCCCCC, true);
            renderStack.pop();
        }
        for (int i = 0; i < locTextRefraction.size(); i++) {
            IReorderingProcessor line = locTextRefraction.get(i);
            renderStack.push();
            renderStack.translate(guiLeft + 220, guiTop + 30 + i * 10, this.getGuiZLevel());
            RenderingDrawUtils.renderStringAt(line, renderStack, font, 0xFFCCCCCC, true);
            renderStack.pop();
        }
    }

    private void drawCapeInformationPages(MatrixStack renderStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < locTextMantle.size(); i++) {
            IReorderingProcessor line = locTextMantle.get(i);
            renderStack.push();
            renderStack.translate(guiLeft + 30, guiTop + 30 + i * 10, this.getGuiZLevel());
            RenderingDrawUtils.renderStringAt(line, renderStack, font, 0xFFCCCCCC, true);
            renderStack.pop();
        }

        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
            SimpleAltarRecipe recipe = RecipeHelper.findAltarRecipeResult(stack ->
                    stack.getItem() instanceof ItemMantle &&
                            this.constellation.equals(ItemsAS.MANTLE.getConstellation(stack)));

            if (recipe != null) {
                lastFramePage = new RenderPageAltarRecipe(null, -1, recipe);
                lastFramePage.render    (renderStack, guiLeft + 220, guiTop + 20, this.getGuiZLevel(), partialTicks, mouseX, mouseY);
                lastFramePage.postRender(renderStack, guiLeft + 220, guiTop + 20, this.getGuiZLevel(), partialTicks, mouseX, mouseY);
            }
        }
    }

    private void drawConstellationPaperRecipePage(MatrixStack renderStack, int mouseX, int mouseY, float partialTicks) {
        if (ResearchHelper.getClientProgress().getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)) {
            SimpleAltarRecipe recipe = RecipeHelper.findAltarRecipeResult(stack ->
                    stack.getItem() instanceof ItemConstellationPaper &&
                    this.constellation.equals(ItemsAS.CONSTELLATION_PAPER.getConstellation(stack)));

            if (recipe != null) {
                lastFramePage = new RenderPageAltarRecipe(null, -1, recipe);
                lastFramePage.render    (renderStack, guiLeft + 30, guiTop + 20, this.getGuiZLevel(), partialTicks, mouseX, mouseY);
                lastFramePage.postRender(renderStack, guiLeft + 30, guiTop + 20, this.getGuiZLevel(), partialTicks, mouseX, mouseY);
            }
        }
    }

    private void drawPageExtendedInformation(MatrixStack renderStack) {
        ITextProperties info = this.getConstellation().getConstellationTag();
        if (!detailed) {
            info = new TranslationTextComponent("astralsorcery.journal.constellation.unknown");
        }

        int width = font.getStringPropertyWidth(info);
        float chX = 305 - (width / 2F);
        renderStack.push();
        renderStack.translate(guiLeft + chX, guiTop + 44, this.getGuiZLevel());
        RenderingDrawUtils.renderStringAt(font, renderStack, info, 0xFFCCCCCC);
        renderStack.pop();

        if (detailed && !locTextMain.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            renderStack.push();
            renderStack.translate(guiLeft + offsetX, guiTop + offsetY, this.getGuiZLevel());
            for (IReorderingProcessor line : locTextMain) {
                RenderingDrawUtils.renderStringAt(font, renderStack, line, 0xFFCCCCCC);
                renderStack.translate(0, 13, 0);
            }
            renderStack.pop();
        }
    }

    private void drawPagePhaseInformation(MatrixStack renderStack) {
        if (this.activePhases == null) {
            this.testActivePhases();
            if (this.activePhases == null) {
                return;
            }
        }

        List<MoonPhase> phases = this.activePhases;
        if (phases.isEmpty()) {

            ITextProperties none = new TranslationTextComponent("astralsorcery.journal.constellation.unknown");
            float scale = 1.8F;
            float length = font.getStringPropertyWidth(none) * scale;
            float offsetLeft = guiLeft + 296 - length / 2;
            int offsetTop = guiTop + 199;

            renderStack.push();
            renderStack.translate(offsetLeft + 10, offsetTop, getGuiZLevel());
            renderStack.scale(scale, scale, scale);
            RenderingDrawUtils.renderStringAt(none, renderStack, font, 0xCCDDDDDD, true);
            renderStack.pop();
        } else {
            boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(this.constellation);

            int size = 19;
            int offsetX = 95 + (width / 2) - (MoonPhase.values().length * (size + 2)) / 2;
            int offsetY = 199 + guiTop;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

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
                    RenderSystem.defaultBlendFunc();
                    brightness = 0.7F;
                }
                RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                    RenderingGuiUtils.rect(buf, renderStack, offsetX + (index * (size + 2)), offsetY, this.getGuiZLevel(), size, size)
                            .color(brightness, brightness, brightness, brightness)
                            .draw();
                });
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }

    private void drawPageConstellation(MatrixStack renderStack, float partial) {
        ITextProperties cstName = this.constellation.getConstellationName();
        int width = font.getStringPropertyWidth(cstName);

        renderStack.push();
        renderStack.translate(guiLeft + (305 - (width * 1.8F / 2F)), guiTop + 26, this.getGuiZLevel());
        renderStack.scale(1.8F, 1.8F, 1);
        RenderingDrawUtils.renderStringAt(cstName, renderStack, font, 0xFFC3C3C3, true);
        renderStack.pop();

        ITextProperties dstInfo = constellation.getConstellationTypeDescription();
        if (!detailed) {
            dstInfo = new TranslationTextComponent("astralsorcery.journal.constellation.unknown");
        }
        width = font.getStringPropertyWidth(dstInfo);

        renderStack.push();
        renderStack.translate(guiLeft + (305 - (width / 2F)), guiTop + 219, this.getGuiZLevel());
        RenderingDrawUtils.renderStringAt(dstInfo, renderStack, font, 0xFFDDDDDD, true);
        renderStack.pop();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Random rand = new Random(0x4196A15C91A5E199L);
        boolean known = ResearchHelper.getClientProgress().hasConstellationDiscovered(constellation);
        RenderingConstellationUtils.renderConstellationIntoGUI(
                known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation, renderStack,
                guiLeft + 40, guiTop + 60, this.getGuiZLevel(),
                150, 150, 2F,
                () -> 0.6F + 0.4F * RenderingConstellationUtils.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10)),
                true, false);
        RenderSystem.disableBlend();
    }

    private void drawNavArrows(MatrixStack renderStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        this.rectNext = null;
        this.rectPrev = null;
        this.rectBack = this.drawArrow(renderStack, guiLeft + 197, guiTop + 230, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);

        if (doublePageID - 1 >= 0) {
            this.rectPrev = this.drawArrow(renderStack, guiLeft + 25, guiTop + 220, this.getGuiZLevel(), Type.LEFT, mouseX, mouseY, partialTicks);
        }

        if (doublePageID + 1 <= doublePages) {
            this.rectNext = this.drawArrow(renderStack, guiLeft + 367, guiTop + 220, this.getGuiZLevel(), Type.RIGHT, mouseX, mouseY, partialTicks);
        }

        RenderSystem.disableBlend();
    }

    private void drawCstBackground(MatrixStack renderStack) {
        TexturesAS.TEX_BLACK.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            Matrix4f offset = renderStack.getLast().getMatrix();
            buf.pos(offset, guiLeft + 15,  guiTop + 240, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 1).endVertex();
            buf.pos(offset, guiLeft + 200, guiTop + 240, this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 1).endVertex();
            buf.pos(offset, guiLeft + 200, guiTop + 10,  this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(1, 0).endVertex();
            buf.pos(offset, guiLeft + 15,  guiTop + 10,  this.getGuiZLevel()).color(1F, 1F, 1F, 1F).tex(0, 0).endVertex();
        });

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        TexturesAS.TEX_GUI_BACKGROUND_CONSTELLATIONS.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            Matrix4f offset = renderStack.getLast().getMatrix();
            buf.pos(offset, guiLeft + 15,  guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.5F).tex(0.3F, 0.9F).endVertex();
            buf.pos(offset, guiLeft + 200, guiTop + 240, this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.5F).tex(0.7F, 0.9F).endVertex();
            buf.pos(offset, guiLeft + 200, guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.5F).tex(0.7F, 0.1F).endVertex();
            buf.pos(offset, guiLeft + 15,  guiTop + 10,  this.getGuiZLevel()).color(0.8F, 0.8F, 1F, 0.5F).tex(0.3F, 0.1F).endVertex();
        });
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return true;
    }

    @Override
    public void closeScreen() {
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
