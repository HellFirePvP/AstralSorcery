package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.client.Minecraft;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalConstellationCluster
 * Created by HellFirePvP
 * Date: 15.08.2016 / 12:53
 */
public class GuiJournalConstellationCluster extends GuiScreenJournal {

    private static final BindableResource texCFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiJCFrame");

    private final boolean isDiscovered;
    private List<Constellation> constellations;

    public GuiJournalConstellationCluster(int bookmark, boolean discoveredTier, List<Constellation> constellations) {
        super(bookmark);
        this.isDiscovered = discoveredTier;
        this.constellations = constellations;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefault(textureResBlank);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if(rectResearchBookmark != null && rectResearchBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalProgression.currentInstance == null ? new GuiJournalProgression() : GuiJournalProgression.currentInstance);
        }
        if(bookmarkIndex == -1 && rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            Minecraft.getMinecraft().displayGuiScreen(GuiJournalConstellations.getConstellationScreen());
        }
    }

}
