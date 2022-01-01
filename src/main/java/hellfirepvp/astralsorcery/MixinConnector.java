/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2021
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinConnector
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration(String.format("assets/%s/%s.mixins.json", AstralSorcery.MODID, AstralSorcery.MODID));
    }
}
