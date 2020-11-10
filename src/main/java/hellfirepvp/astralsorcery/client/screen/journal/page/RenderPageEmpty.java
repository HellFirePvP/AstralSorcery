/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

import com.mojang.blaze3d.matrix.MatrixStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderPageEmpty
 * Created by HellFirePvP
 * Date: 10.10.2019 / 17:26
 */
public class RenderPageEmpty extends RenderablePage {

    public static final RenderPageEmpty INSTANCE = new RenderPageEmpty();

    private RenderPageEmpty() {
        super(null, -1);
    }

    @Override
    public void render(MatrixStack renderStack, float x, float y, float z, float pTicks, float mouseX, float mouseY) {}
}
