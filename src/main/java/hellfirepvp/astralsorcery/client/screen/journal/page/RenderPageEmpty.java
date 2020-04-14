/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.page;

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
    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {}
}
