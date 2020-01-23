/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen;

import hellfirepvp.astralsorcery.client.screen.base.SkyScreen;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenTelescope
 * Created by HellFirePvP
 * Date: 15.01.2020 / 17:16
 */
public class ScreenTelescope extends WidthHeightScreen implements SkyScreen {

    private final PlayerEntity player;
    private final TileTelescope telescope;
    private TileTelescope.TelescopeRotation rotation;

    public ScreenTelescope(PlayerEntity player, TileTelescope telescope) {
        super(new TranslationTextComponent("screen.astralsorcery.telescope"), 280, 280);
        this.player = player;
        this.telescope = telescope;
        this.rotation = this.telescope.getRotation();


    }
}
