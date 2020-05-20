/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.base.MoonPhase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMinorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:09
 */
public interface IMinorConstellation extends IConstellation {

    public List<MoonPhase> getShowupMoonPhases(long rSeed);

    default public ITextComponent getInfoTraitEffect() {
        return new TranslationTextComponent(this.getTranslationKey() + ".trait");
    }
}
