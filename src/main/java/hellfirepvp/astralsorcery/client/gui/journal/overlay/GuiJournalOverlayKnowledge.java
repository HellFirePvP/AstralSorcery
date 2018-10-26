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
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import net.minecraft.client.renderer.GlStateManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 26.09.2018 / 12:51
 */
public class GuiJournalOverlayKnowledge extends GuiScreenJournalOverlay {

    public static final BindableResource textureKnowledgeOverlay = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guicontippaper");

    private final KnowledgeFragment knowledgeFragment;

    public GuiJournalOverlayKnowledge(GuiScreenJournal origin, KnowledgeFragment display) {
        super(origin);
        this.knowledgeFragment = display;
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

    public KnowledgeFragment getKnowledgeFragment() {
        return knowledgeFragment;
    }

}
