/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.EffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderConstellationUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.DebugConstellation;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageConstellation extends RenderPage {

    private final BaseConstellation constellation;

    public RenderPageConstellation(@Nullable ResearchNode node, int nodePage, BaseConstellation constellation) {
        super(node, nodePage);
        this.constellation = constellation;
    }

    @Override
    public void preRender(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        RandomSource rand = RandomSource.create(this.constellation.getName().hashCode());
        float relativeX = TomePage.DEFAULT_WIDTH / 450F;
        float relativeY = TomePage.DEFAULT_HEIGHT / 300F;
        float rX = rand.nextFloat() * (1F - relativeX);
        float rY = rand.nextFloat() * (1F - relativeY);

        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_TOME_BACKGROUND_CONSTELLATION_DETAIL,
                ColorWrapper.opaque(0x555555),
                x, y, TomePage.DEFAULT_WIDTH, TomePage.DEFAULT_HEIGHT,
                new UVFrame(0.5F - relativeX / 2F + rX, 0.5F - relativeY / 2F + rY, relativeX, relativeY));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        int size = 140;
        int offsetX = x + TomePage.DEFAULT_WIDTH / 2 - size / 2;
        int offsetY = y + TomePage.DEFAULT_HEIGHT / 2 - size / 2;
        BaseConstellation cst = this.constellation;
        PlayerProgress progress = ResearchManager.getClientProgress();
        boolean discovered = progress.hasDiscoveredConstellation(cst);
        ColorWrapper color = discovered ? cst.getConstellationColor() : ColorsAS.CONSTELLATION_TYPE_BLANK;

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderConstellationUtil.drawConstellationUI(color, cst,
                guiGraphics.pose(), offsetX, offsetY, size, size,
                2F, () -> 1F, true, false);
        RenderSystem.disableBlend();
    }
}
