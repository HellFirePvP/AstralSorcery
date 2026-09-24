/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SoundUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@OnlyIn(Dist.CLIENT)
public class SoundUtil {

    public static float getSoundVolume(SoundSource cat) {
        return Minecraft.getInstance().options.getSoundSourceVolume(cat);
    }
}
