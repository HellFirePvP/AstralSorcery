/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.container;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.ScreenCustomContainer;
import hellfirepvp.astralsorcery.common.container.ContainerTome;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenContainerTome
 * Created by HellFirePvP
 * Date: 15.08.2019 / 14:38
 */
public class ScreenContainerTome extends ScreenCustomContainer<ContainerTome> {

    public ScreenContainerTome(ContainerTome screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name, 176, 166);
    }

    @Override
    public AbstractRenderableTexture getBackgroundTexture() {
        return TexturesAS.TEX_CONTAINER_TOME_STORAGE;
    }
}
