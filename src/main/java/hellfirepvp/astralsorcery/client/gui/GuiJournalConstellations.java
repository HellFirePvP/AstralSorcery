package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static hellfirepvp.astralsorcery.client.gui.GuiJournalConstellationCluster.drawConstellationRect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellations
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:23
 */
public class GuiJournalConstellations extends GuiScreenJournal {

    private static final Map<Integer, Point> tierOffsetMap = new HashMap<>();

    private final Random rand = new Random();
    private final long staticSeed;

    private List<Constellation> unmapped;
    private List<Tier> discoveredCompleteTiers;

    private Map<Rectangle, Tier> rectTierRenderMap = new HashMap<>();
    private Rectangle rectUnmapped = null;

    private GuiJournalConstellations(List<Constellation> constellations, List<Tier> tiers) {
        super(1);
        this.unmapped = constellations;
        this.discoveredCompleteTiers = tiers;
        this.staticSeed = 0xF095561C419B8123L ^ System.currentTimeMillis();
    }

    private void resetRandom() {
        this.rand.setSeed(staticSeed);
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
            return new GuiJournalConstellationCluster(1, false, "gui.journal.c.unmapped", unmapped);
        } else if(tiersFound.size() == 1) {
            return new GuiJournalConstellationCluster(1, true, tiersFound.get(0).getUnlocalizedName(), tierMapped.get(tiersFound.get(0)));
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
        resetRandom();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        drawDefault(textureResBlank);

        rectTierRenderMap.clear();
        rectUnmapped = null;

        Point mouse = getCurrentMousePoint();

        zLevel += 250;
        drawTierGroups(mouse);
        zLevel -= 250;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawTierGroups(Point mouse) {
        int startIndex = 0;
        if(!unmapped.isEmpty()) {
            Point p = tierOffsetMap.get(startIndex);
            startIndex++;
            rectUnmapped = drawConstellationRect(unmapped.get(rand.nextInt(unmapped.size())), guiLeft + p.x, guiTop + p.y, zLevel, mouse, "gui.journal.c.unmapped");
        }
        for (int i = 0; i < discoveredCompleteTiers.size(); i++) {
            Tier t = discoveredCompleteTiers.get(i);
            Point at = tierOffsetMap.get(startIndex + i);
            rectTierRenderMap.put(drawConstellationRect(t.getRandomConstellation(rand), guiLeft + at.x, guiTop + at.y, zLevel, mouse, t.getUnlocalizedName()), t);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.currentInstance == null ? new GuiJournalProgression() : GuiJournalProgression.currentInstance);
            return;
        }
        if(rectUnmapped != null && rectUnmapped.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(-1, false, "gui.journal.c.unmapped", unmapped));
            return;
        }
        for (Rectangle r : rectTierRenderMap.keySet()) {
            if(r.contains(p)) {
                Tier t = rectTierRenderMap.get(r);
                Minecraft.getMinecraft().displayGuiScreen(new GuiJournalConstellationCluster(-1, true, t.getUnlocalizedName(), t.getConstellations()));
                return;
            }
        }
    }

    static {
        tierOffsetMap.put(0, new Point( 30,  20));
        tierOffsetMap.put(1, new Point( 20, 150));
        tierOffsetMap.put(2, new Point(120, 100));
        tierOffsetMap.put(3, new Point(310,  20));
        tierOffsetMap.put(4, new Point(220, 100));
        tierOffsetMap.put(5, new Point(310, 140));
    }

}
