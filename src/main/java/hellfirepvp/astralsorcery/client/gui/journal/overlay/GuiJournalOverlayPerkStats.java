/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.overlay;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.client.renderer.GlStateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayPerkStats
 * Created by HellFirePvP
 * Date: 17.01.2019 / 22:01
 */
public class GuiJournalOverlayPerkStats extends GuiScreenJournalOverlay {

    public static final BindableResource textureKnowledgeOverlay = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper_blank");

    public GuiJournalOverlayPerkStats(GuiScreenJournal origin) {
        super(origin);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        int width = 275;
        int height = 344;

        textureKnowledgeOverlay.bindTexture();
        drawTexturedRect(guiLeft + guiWidth / 2 - width / 2, guiTop + guiHeight / 2 - height / 2, width, height, textureKnowledgeOverlay);

        GlStateManager.enableDepth();
        TextureHelper.refreshTextureBindState();
    }

}
