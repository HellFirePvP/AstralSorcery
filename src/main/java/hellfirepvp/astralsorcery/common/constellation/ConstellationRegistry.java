/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRegistry
 * Created by HellFirePvP
 * Date: 30.05.2019 / 00:01
 */
public class ConstellationRegistry {

    private static SortedSet<IMajorConstellation> majorConstellations = new TreeSet<>();
    private static SortedSet<IWeakConstellation> weakConstellations = new TreeSet<>();
    private static SortedSet<IMinorConstellation> minorConstellations = new TreeSet<>();
    private static SortedSet<IConstellationSpecialShowup> specialShowupConstellations = new TreeSet<>();

    public static <T extends IConstellation> void addConstellation(T constellation) {
        if (constellation instanceof IWeakConstellation) {
            if (constellation instanceof IMajorConstellation) {
                majorConstellations.add((IMajorConstellation) constellation);
            }
            weakConstellations.add((IWeakConstellation) constellation);
        } else if (constellation instanceof IMinorConstellation) {
            minorConstellations.add((IMinorConstellation) constellation);
        } else {
            AstralSorcery.log.warn("Tried to register constellation that's neither minor nor major or weak: " + constellation.toString());
            AstralSorcery.log.warn("Skipping specific constellation registration...");
            throw new IllegalArgumentException("Tried to register non-minor, non-weak and non-major constellation.");
        }
        if (constellation instanceof IConstellationSpecialShowup) {
            specialShowupConstellations.add((IConstellationSpecialShowup) constellation);
        }
    }

    @Nullable
    public static IConstellation getConstellation(ResourceLocation name) {
        return RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(name);
    }

    public static Collection<IConstellationSpecialShowup> getSpecialShowupConstellations() {
        return Collections.unmodifiableCollection(specialShowupConstellations);
    }

    public static Collection<IWeakConstellation> getWeakConstellations() {
        return Collections.unmodifiableCollection(weakConstellations);
    }

    public static Collection<IMajorConstellation> getMajorConstellations() {
        return Collections.unmodifiableCollection(majorConstellations);
    }

    public static Collection<IMinorConstellation> getMinorConstellations() {
        return Collections.unmodifiableCollection(minorConstellations);
    }

    public static Collection<IConstellation> getAllConstellations() {
        List<IConstellation> all = new ArrayList<>(RegistriesAS.REGISTRY_CONSTELLATIONS.getValues());
        Collections.sort(all);
        return all;
    }

}
