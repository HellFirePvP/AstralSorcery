/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.gui.journal.page.IJournalPage;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageTraitRecipe;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.MoonPhaseRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CapeAttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationPaperRecipe;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.RecipesAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationDetails
 * Created by HellFirePvP
 * Date: 16.08.2016 / 19:09
 */
public class GuiJournalConstellationDetails extends GuiScreenJournal {

    private static final BindableResource texBlack   = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");
    private static final BindableResource texBg      = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiresbgcst");
    private static final BindableResource texBgBlank = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijblankbook");
    private static final BindableResource texArrow   = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guijarrow");

    private IConstellation constellation;
    private GuiJournalConstellationCluster origin;
    private boolean detailed;

    private int doublePageID = 0;
    private int doublePages = 0;

    private IGuiRenderablePage lastFramePage = null;

    private Rectangle rectBack, rectNext, rectPrev;

    private List<MoonPhase> phases = new LinkedList<>();
    private List<MoonPhase> activePhases = new LinkedList<>();

    private List<String> locTextMain = new LinkedList<>();
    private List<String> locTextEnchRitual = new LinkedList<>();
    private List<String> locTextCapeEffect = new LinkedList<>();

    public GuiJournalConstellationDetails(GuiJournalConstellationCluster origin, IConstellation c) {
        super(-1);
        this.origin = origin;
        this.constellation = c;
        boolean has = false;
        for (String strConstellation : ResearchManager.clientProgress.getKnownConstellations()) {
            IConstellation ce = ConstellationRegistry.getConstellationByName(strConstellation);
            if(ce != null && ce.equals(c)) {
                has = true;
                break;
            }
        }
        this.detailed = has;
        ProgressionTier playerProgress = ResearchManager.clientProgress.getTierReached();
        if(has && (EnumGatedKnowledge.CONSTELLATION_RITUAL.canSee(playerProgress) || EnumGatedKnowledge.CONSTELLATION_STELLAR.canSee(playerProgress) ||
                EnumGatedKnowledge.CONSTELLATION_PAPER_CRAFT.canSee(playerProgress))) {
            this.doublePages++;

            if(EnumGatedKnowledge.CONSTELLATION_CAPE.canSee(playerProgress) && !(constellation instanceof IMinorConstellation)) {
                this.doublePages++;
            }
        }

        testPhases();
        testActivePhases();
        buildMainText();
        buildEnchRitualText();
        buildCapeText();
    }

    private void buildCapeText() {
        if(EnumGatedKnowledge.CONSTELLATION_CAPE.canSee(ResearchManager.clientProgress.getTierReached())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".capeeffect";
            String textEnch = I18n.format(unlocEnch);
            if(!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.capeeffect");
                locTextCapeEffect.add(head);
                locTextCapeEffect.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(segment, IJournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextCapeEffect.addAll(lines);
                locTextCapeEffect.add("");
            }
        }
    }

    private void buildEnchRitualText() {
        if(EnumGatedKnowledge.CONSTELLATION_STELLAR.canSee(ResearchManager.clientProgress.getTierReached())) {
            String unlocEnch = constellation.getUnlocalizedName() + ".enchantments";
            String textEnch = I18n.format(unlocEnch);
            if(!unlocEnch.equals(textEnch)) {
                String head = I18n.format("gui.journal.cst.enchantments");
                locTextEnchRitual.add(head);
                locTextEnchRitual.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textEnch.split("<NL>")) {
                    lines.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(segment, IJournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextEnchRitual.addAll(lines);
                locTextEnchRitual.add("");
            }
        }

        if(EnumGatedKnowledge.CONSTELLATION_RITUAL.canSee(ResearchManager.clientProgress.getTierReached())) {
            String unlocRitual = constellation.getUnlocalizedName() + ".ritual";
            String textRitual = I18n.format(unlocRitual);
            if(!unlocRitual.equals(textRitual)) {
                String head = I18n.format("gui.journal.cst.ritual");
                locTextEnchRitual.add(head);
                locTextEnchRitual.add("");

                List<String> lines = new LinkedList<>();
                for (String segment : textRitual.split("<NL>")) {
                    lines.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(segment, IJournalPage.DEFAULT_WIDTH));
                    lines.add("");
                }
                locTextEnchRitual.addAll(lines);
            }
        }
    }

    private void buildMainText() {
        String unloc = constellation.getUnlocalizedName() + ".effect";
        String text = I18n.format(unloc);
        if(unloc.equals(text)) return;

        List<String> lines = new LinkedList<>();
        for (String segment : text.split("<NL>")) {
            lines.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(segment, IJournalPage.DEFAULT_WIDTH));
            lines.add("");
        }
        locTextMain.addAll(lines);
    }

    private void testPhases() {
        if(constellation instanceof IWeakConstellation) {
            Collections.addAll(phases, MoonPhase.values());
        } else if(constellation instanceof IMinorConstellation) {
            //Why this way? To maintain phase-order.
            for (MoonPhase ph : MoonPhase.values()) {
                if(((IMinorConstellation) constellation).getShowupMoonPhases().contains(ph)) {
                    phases.add(ph);
                }
            }
        }
    }

    private void testActivePhases() {
        if(Minecraft.getMinecraft().world == null) return;
        WorldSkyHandler handler = ConstellationSkyHandler.getInstance().getWorldHandler(Minecraft.getMinecraft().world);
        if(handler == null) return;
        for (MoonPhase phase : this.phases) {
            List<IConstellation> active = handler.getConstellationsForMoonPhase(phase);
            if(active != null && !active.isEmpty()) {
                if(active.contains(constellation)) {
                    activePhases.add(phase);
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        lastFramePage = null;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        switch (doublePageID) {
            case 0:
                drawCstBackground();
                drawDefault(textureResShellCst);
                break;
            case 1:
                drawDefault(textureResBlank);
                break;
            case 2:
                drawDefault(textureResBlank);
                break;
        }
        TextureHelper.refreshTextureBindState();

        zLevel += 150;
        drawArrows(partialTicks);
        drawNavArrows(partialTicks);
        switch (doublePageID) {
            case 0:
                drawConstellation(partialTicks);
                drawPhaseInformation();
                drawExtendedInformation();
                break;
            case 1:
                drawERPInformationPages(partialTicks, mouseX, mouseY);
                break;
            case 2:
                drawCapeInformationPages(partialTicks, mouseX, mouseY);
                break;
        }
        zLevel -= 150;

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    private void drawCapeInformationPages(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(0.86F, 0.86F, 0.86F, 0.8F);
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft + 30, guiTop + 30, 0);
        for (int i = 0; i < locTextCapeEffect.size(); i++) {
            String line = locTextCapeEffect.get(i);
            fontRenderer.drawString(line, 0, (i * 10), 0x00DDDDDD, true);
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.color(1F, 1F, 1F, 1F);

        CapeAttunementRecipe recipe = RecipesAS.capeCraftingRecipes.get(this.constellation);
        if(recipe != null) {
            lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();

            GlStateManager.pushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            lastFramePage.render    (guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            lastFramePage.postRender(guiLeft + 220, guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
            GL11.glPopAttrib();
            GlStateManager.popMatrix();
        }
    }

    private void drawERPInformationPages(float partialTicks, int mouseX, int mouseY) {
        boolean usedLeftSide = false;
        ProgressionTier prog = ResearchManager.clientProgress.getTierReached();
        if(EnumGatedKnowledge.CONSTELLATION_RITUAL.canSee(prog) ||
                EnumGatedKnowledge.CONSTELLATION_STELLAR.canSee(prog)) {
            usedLeftSide = true;

            GlStateManager.color(0.86F, 0.86F, 0.86F, 0.8F);
            GlStateManager.disableDepth();
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft + 30, guiTop + 30, 0);
            for (int i = 0; i < locTextEnchRitual.size(); i++) {
                String line = locTextEnchRitual.get(i);
                fontRenderer.drawString(line, 0, (i * 10), 0x00DDDDDD, true);
            }
            GlStateManager.popMatrix();
            GlStateManager.enableDepth();
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
        if(EnumGatedKnowledge.CONSTELLATION_PAPER_CRAFT.canSee(prog)) {
            ConstellationPaperRecipe recipe = RecipesAS.paperCraftingRecipes.get(this.constellation);
            if(recipe != null) {
                lastFramePage = new JournalPageTraitRecipe(recipe).buildRenderPage();

                GlStateManager.pushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                lastFramePage.render    (guiLeft + (usedLeftSide ? 220 : 30), guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
                lastFramePage.postRender(guiLeft + (usedLeftSide ? 220 : 30), guiTop + 20, partialTicks, zLevel, mouseX, mouseY);
                GL11.glPopAttrib();
                GlStateManager.popMatrix();
            }
        }
    }

    private void drawNavArrows(float partialTicks) {
        GlStateManager.disableDepth();
        Point mouse = getCurrentMousePoint();
        rectNext = null;
        rectPrev = null;
        if(doublePageID - 1 >= 0) {
            int width = 30;
            int height = 15;
            rectPrev = new Rectangle(guiLeft + 25, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectPrev.getX() + (width / 2), rectPrev.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0.5F;
            if(rectPrev.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.color(1F, 1F, 1F, 0.8F);
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        if(doublePageID + 1 <= doublePages) {
            int width = 30;
            int height = 15;
            rectNext = new Rectangle(guiLeft + 367, guiTop + 220, width, height);
            GlStateManager.pushMatrix();
            GlStateManager.translate(rectNext.getX() + (width / 2), rectNext.getY() + (height / 2), 0);
            float uFrom = 0F, vFrom = 0F;
            if(rectNext.contains(mouse)) {
                uFrom = 0.5F;
                GlStateManager.scale(1.1, 1.1, 1.1);
            } else {
                double t = ClientScheduler.getClientTick() + partialTicks;
                float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
                GlStateManager.scale(sin, sin, sin);
            }
            GlStateManager.color(1F, 1F, 1F, 0.8F);
            GlStateManager.translate(-(width / 2), -(height / 2), 0);
            texArrow.bind();
            drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableDepth();
    }

    private void drawCstBackground() {
        texBlack.bind();
        GlStateManager.color(1F, 1F, 1F, 1F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 15,  guiTop + 240, zLevel).tex(0, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 240, zLevel).tex(1, 1).endVertex();
        bb.pos(guiLeft + 200, guiTop + 10,  zLevel).tex(1, 0).endVertex();
        bb.pos(guiLeft + 15,  guiTop + 10,  zLevel).tex(0, 0).endVertex();
        tes.draw();
        GlStateManager.color(0.8F, 0.8F, 1F, 0.7F);
        texBg.bind();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(guiLeft + 35,       guiTop + guiHeight - 10, zLevel).tex(0.3, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + guiHeight - 10, zLevel).tex(0.7, 0.9).endVertex();
        bb.pos(guiLeft + 35 + 170, guiTop + 10,             zLevel).tex(0.7, 0.1).endVertex();
        bb.pos(guiLeft + 35,       guiTop + 10,             zLevel).tex(0.3, 0.1).endVertex();
        tes.draw();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawExtendedInformation() {
        float br = 0.8666F;
        GlStateManager.color(br, br, br, 0.8F);
        String info = I18n.format(constellation.getUnlocalizedInfo()).toUpperCase();
        info = detailed ? info : "???";
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        double width = fr.getStringWidth(info);
        double chX = 305 - (width / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + chX, guiTop + 44, 0);
        fr.drawString(info, 0, 0, 0xCCDDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.color(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();

        if(detailed && !locTextMain.isEmpty()) {
            int offsetX = 220, offsetY = 77;
            for (String s : locTextMain) {
                GlStateManager.pushMatrix();
                GlStateManager.disableDepth();
                GlStateManager.translate(guiLeft + offsetX, guiTop + offsetY, 0);
                fr.drawString(s, 0, 0, 0xCCDDDDDD, true);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.color(br, br, br, 0.8F);
                TextureHelper.refreshTextureBindState();
                offsetY += 13;
            }
        }

        /*texArrow.bind();
        fontRenderer.font_size_multiplicator = 0.06F;
        String pref = I18n.translateToLocal("constraint.description");
        fontRenderer.drawString(pref, guiLeft + 228, guiTop + 60, zLevel, null, 0.7F, 0);

        texArrow.bind();
        fontRenderer.font_size_multiplicator = 0.05F;
        SizeConstraint sc = constellation.getSizeConstraint();
        String trSize = I18n.translateToLocal(sc.getUnlocalizedName());
        fontRenderer.drawString("- " + trSize, guiLeft + 228, guiTop + 85, zLevel, null, 0.7F, 0);

        List<RitualConstraint> constrList = constellation.getConstraints();
        for (int i = 0; i < constrList.size(); i++) {
            RitualConstraint cstr = constrList.get(i);
            String str = I18n.translateToLocal(cstr.getUnlocalizedName());
            texArrow.bind();
            fontRenderer.font_size_multiplicator = 0.05F;
            fontRenderer.drawString("- " + str, guiLeft + 228, guiTop + 107 + (i * 22), zLevel, null, 0.7F, 0);
        }*/
    }

    private void drawPhaseInformation() {
        if(constellation instanceof IConstellationSpecialShowup) {
            GlStateManager.disableDepth();
            double scale = 1.8;
            TextureHelper.refreshTextureBindState();
            FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
            double length = fr.getStringWidth("? ? ?") * scale;
            double offsetLeft = guiLeft + 296 - length / 2;
            int offsetTop = guiTop + 199;
            GlStateManager.pushMatrix();
            GlStateManager.translate(offsetLeft + 10, offsetTop, 0);
            GlStateManager.scale(scale, scale, scale);
            fr.drawStringWithShadow("? ? ?", 0, 0, 0xCCDDDDDD);
            GlStateManager.popMatrix();
            GlStateManager.color(1, 1, 1, 1);
            TextureHelper.refreshTextureBindState();
            GlStateManager.enableDepth();
        } else if(ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName())) {
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.color(0.7F, 0.7F, 0.7F, 0.6F);
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if(!this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.PREALPHA.applyStateManager();
            GlStateManager.color(1F, 1F, 1F, 1F);
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                if(this.activePhases.contains(ph)) {
                    MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                    drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
                }
            }
            Blending.DEFAULT.applyStateManager();
            TextureHelper.refreshTextureBindState();
        } else {
            GlStateManager.enableBlend();
            Blending.DEFAULT.applyStateManager();
            GlStateManager.color(0.8F, 0.8F, 0.8F, 1F);
            int size = 19;
            int offsetX = 95 + (width / 2) - (phases.size() * (size + 2)) / 2;
            int offsetY = 199 + guiTop;
            for (int i = 0; i < phases.size(); i++) {
                MoonPhase ph = phases.get(i);
                MoonPhaseRenderHelper.getMoonPhaseTexture(ph).bind();
                drawRect(offsetX + (i * (size + 2)), offsetY, size, size);
            }
            TextureHelper.refreshTextureBindState();
        }
    }

    private void drawConstellation(float partial) {
        float br = 0.866F;
        GlStateManager.color(br, br, br, 0.8F);
        String name = I18n.format(constellation.getUnlocalizedName()).toUpperCase();
        TextureHelper.refreshTextureBindState();
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        double width = fr.getStringWidth(name);
        double offsetX = 305 - (width * 1.8 / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + offsetX, guiTop + 26, 0);
        GlStateManager.scale(1.8, 1.8, 1.8);
        fr.drawString(name, 0, 0, 0xEEDDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.color(br, br, br, 0.8F);
        TextureHelper.refreshTextureBindState();
        String dstInfo = "astralsorcery.journal.constellation.dst.";
        if(constellation instanceof IMajorConstellation) {
            dstInfo += "major";
        } else if(constellation instanceof IWeakConstellation) {
            dstInfo += "weak";
        } else {
            dstInfo += "minor";
        }
        if (!detailed) {
            dstInfo = "???";
        }
        dstInfo = I18n.format(dstInfo);
        width = fr.getStringWidth(dstInfo);
        offsetX = 305 - (width / 2);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.translate(guiLeft + offsetX, guiTop + 219, 0);
        fr.drawString(dstInfo, 0, 0, 0x99DDDDDD, true);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 0.8F);
        TextureHelper.refreshTextureBindState();

        Random rand = new Random(0x4196A15C91A5E199L);

        GlStateManager.enableBlend();
        Blending.DEFAULT.apply();

        boolean known = ResearchManager.clientProgress.hasConstellationDiscovered(constellation.getUnlocalizedName());
        RenderConstellation.renderConstellationIntoGUI(known ? constellation.getConstellationColor() : constellation.getTierRenderColor(), constellation,
                guiLeft + 40, guiTop + 60, zLevel,
                150, 150, 2F,
                new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return 0.3F + 0.7F * RenderConstellation.conCFlicker(ClientScheduler.getClientTick(), partial, 12 + rand.nextInt(10));
            }
        }, true, false);
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawArrows(float partialTicks) {
        Point mouse = getCurrentMousePoint();
        int width = 30;
        int height = 15;
        rectBack = new Rectangle(guiLeft + 197, guiTop + 230, width, height);
        GlStateManager.pushMatrix();
        GlStateManager.translate(rectBack.getX() + (width / 2), rectBack.getY() + (height / 2), 0);
        float uFrom = 0F, vFrom = 0.5F;
        if(rectBack.contains(mouse)) {
            uFrom = 0.5F;
            GlStateManager.scale(1.1, 1.1, 1.1);
        } else {
            double t = ClientScheduler.getClientTick() + partialTicks;
            float sin = ((float) Math.sin(t / 4F)) / 32F + 1F;
            GlStateManager.scale(sin, sin, sin);
        }
        GlStateManager.color(1F, 1F, 1F, 0.8F);
        GlStateManager.translate(-(width / 2), -(height / 2), 0);
        texArrow.bind();
        drawTexturedRectAtCurrentPos(width, height, uFrom, vFrom, 0.5F, 0.5F);
        GlStateManager.popMatrix();
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        Minecraft.getMinecraft().displayGuiScreen(origin);
        return true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            GuiJournalProgression.resetJournal();
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.getJournalInstance());
            return;
        }
        if(rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
            return;
        }
        if(rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        if(rectBack != null && rectBack.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(origin);
            return;
        }
        if(rectPrev != null && rectPrev.contains(p)) {
            if(doublePageID >= 1) {
                this.doublePageID--;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(rectNext != null && rectNext.contains(p)) {
            if(doublePageID <= doublePages - 1) {
                this.doublePageID++;
            }
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
            return;
        }
        if(doublePageID != 0 && lastFramePage != null) {
            lastFramePage.propagateMouseClick(mouseX, mouseY);
        }
    }

}
