package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellations
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:23
 */
public class GuiJournalConstellations extends GuiScreenJournal {

    private List<Constellation> unmapped;
    private List<Tier> discoveredCompleteTiers;

    private Map<Rectangle, Tier> rectTierRenderMap = new HashMap<>();

    private GuiJournalConstellations(List<Constellation> constellations, List<Tier> tiers) {
        super(1);
        this.unmapped = constellations;
        this.discoveredCompleteTiers = tiers;
    }

    public static GuiScreenJournal getConstellationScreen() {
        PlayerProgress client = ResearchManager.clientProgress;
        List<Constellation> constellations = ConstellationRegistry.resolve(client.getKnownConstellations());
        Map<Tier, List<Constellation>> tierMapped = new HashMap<>();
        for (Constellation c : constellations) {
            Tier t = c.queryTier();
            List<Constellation> l = tierMapped.get(t);
            if(l == null) {
                l = new LinkedList<>();
                tierMapped.put(t, l);
            }
            l.add(c);
        }
        List<Constellation> unmapped = new LinkedList<>();
        List<Tier> tiersFound = new LinkedList<>();

        lblT:
        for (Tier t : tierMapped.keySet()) {
            List<Constellation> found = tierMapped.get(t);
            for (Constellation c : t.getConstellations()) {
                if(!found.contains(c)) {
                    unmapped.addAll(found);
                    continue lblT;
                }
            }
            tiersFound.add(t);
        }
        if(tiersFound.isEmpty()) {
            return new GuiJournalConstellationCluster(1, false, unmapped);
        } else if(tiersFound.size() == 1) {
            return new GuiJournalConstellationCluster(1, true, tierMapped.get(tiersFound.get(0)));
        } else {
            return new GuiJournalConstellations(unmapped, tiersFound);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        drawDefault(textureResBlank);

        rectTierRenderMap.clear();
        zLevel += 250;
        drawTierGroups();
        zLevel -= 250;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawTierGroups() {
        double width = 78;
        double height = 108;
        double offsetX = guiLeft + 15;
        double offsetY = guiTop +  15;
        drawCRect(Constellations.bigDipper,    offsetX,              offsetY              );
        drawCRect(Constellations.tenifium,   offsetX + width + 20, offsetY              );
        drawCRect(Constellations.ara,        offsetX,              offsetY + height + 20);
        drawCRect(Constellations.fertilitas, offsetX + width + 20, offsetY + height + 20);
    }

    private void drawCRect(Constellation display, double offsetX, double offsetY) {
        GL11.glEnable(GL11.GL_BLEND);

        Color c = Color.DARK_GRAY;
        float r = c.getRed()   / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue()  / 255F;

        GL11.glColor4f(r, g, b, 1F);

        RenderConstellation.renderConstellationIntoGUI(c, display,
                MathHelper.floor_double(offsetX), MathHelper.floor_double(offsetY), zLevel,
                80, 80, 2F, new RenderConstellation.BrightnessFunction() {
            @Override
            public float getBrightness() {
                return 0.15F;
            }
        }, true, false);

        GL11.glColor4f(r, g, b, 0.7F);

        String trName = I18n.translateToLocal(display.getName()).toUpperCase();
        OverlayText.OverlayFontRenderer fontRenderer = new OverlayText.OverlayFontRenderer();
        fontRenderer.font_size_multiplicator = 0.11F;
        float yOffset = ((float) offsetY) + 85F;
        float fullLength = 40 - (((float) fontRenderer.getStringWidth(trName)) / 2F);

        fontRenderer.zLevel = zLevel;
        fontRenderer.drawString(trName, (float) offsetX + fullLength, yOffset, null, 1F, 0);

        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.currentInstance == null ? new GuiJournalProgression() : GuiJournalProgression.currentInstance);
        }
    }

}
