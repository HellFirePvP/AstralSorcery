/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.sextant;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SextantFinder
 * Created by HellFirePvP
 * Date: 02.06.2019 / 10:52
 */
public class SextantFinder {

    @Nonnull
    public static TargetObject getFirst() {
        for (TargetObject to : RegistriesAS.REGISTRY_SEXTANT_TARGET) {
            if (to instanceof TargetObject.ASStructure && !((TargetObject.ASTargetObject) to).isAdvanced()) {
                return to;
            }
        }
        TargetObject first = Iterables.getFirst(RegistriesAS.REGISTRY_SEXTANT_TARGET, null);
        if (first == null) {
            throw new IllegalStateException("There has to be at least ONE sextant target!");
        }
        return first;
    }

    public static Collection<TargetObject> getSelectableTargets() {
        return RegistriesAS.REGISTRY_SEXTANT_TARGET.getValues();
    }

    @Nullable
    public static TargetObject getByName(ResourceLocation target) {
        return RegistriesAS.REGISTRY_SEXTANT_TARGET.getValue(target);
    }
}
