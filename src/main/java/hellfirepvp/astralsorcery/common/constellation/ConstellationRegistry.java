/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRegistry
 * Created by HellFirePvP
 * Date: 06.02.2016 01:42
 */
public class ConstellationRegistry {

    private static List<IMajorConstellation> majorConstellations = new LinkedList<>();
    private static List<IWeakConstellation> weakConstellations = new LinkedList<>();
    private static List<IMinorConstellation> minorConstellations = new LinkedList<>();
    private static List<IConstellationSpecialShowup> specialShowupConstellations = new LinkedList<>();

    private static List<IConstellation> generalConstellationList = new LinkedList<>();

    public static <T extends IConstellation> void registerConstellation(T constellation) {
        if(constellation instanceof IWeakConstellation) {
            if(constellation instanceof IMajorConstellation) {
                majorConstellations.add((IMajorConstellation) constellation);
            }
            weakConstellations.add((IWeakConstellation) constellation);
        } else if(constellation instanceof IMinorConstellation) {
            minorConstellations.add((IMinorConstellation) constellation);
        } else {
            AstralSorcery.log.warn("[AstralSorcery] Tried to register constellation that's neither minor nor major or weak: " + constellation.toString());
            AstralSorcery.log.warn("[AstralSorcery] Skipping specific constellation registration...");
            throw new IllegalStateException("Tried to register non-minor, non-weak and non-major constellation.");
        }
        if(constellation instanceof IConstellationSpecialShowup) {
            specialShowupConstellations.add((IConstellationSpecialShowup) constellation);
        }
        generalConstellationList.add(constellation);
    }

    @Nullable
    public static IConstellation getConstellationByName(String name) {
        if(name == null) return null;

        for(IConstellation c : majorConstellations) {
            if(c.getUnlocalizedName().equals(name)) return c;
        }
        for(IConstellation c : weakConstellations) {
            if(c.getUnlocalizedName().equals(name)) return c;
        }
        for(IConstellation c : minorConstellations) {
            if(c.getUnlocalizedName().equals(name)) return c;
        }
        return null;
    }

    @Nullable
    public static IMajorConstellation getMajorConstellationByName(String name) {
        if(name == null) return null;

        for(IMajorConstellation c : majorConstellations) {
            if(c.getUnlocalizedName().equals(name)) return c;
        }
        return null;
    }

    public static List<IConstellation> resolve(List<String> constellationsAsStrings) {
        List<IConstellation> resolved = new LinkedList<>();
        for (String s : constellationsAsStrings) {
            IConstellation c = getConstellationByName(s);
            if(c != null) resolved.add(c);
        }
        return resolved;
    }

    public static List<IConstellationSpecialShowup> getSpecialShowupConstellations() {
        return Collections.unmodifiableList(specialShowupConstellations);
    }

    public static List<IWeakConstellation> getWeakConstellations() {
        return Collections.unmodifiableList(weakConstellations);
    }

    public static List<IMajorConstellation> getMajorConstellations() {
        return Collections.unmodifiableList(majorConstellations);
    }

    public static List<IMinorConstellation> getMinorConstellations() {
        return Collections.unmodifiableList(minorConstellations);
    }

    public static List<IConstellation> getAllConstellations() {
        return Collections.unmodifiableList(generalConstellationList);
    }

    public static int getConstellationId(IConstellation c) {
        List<IConstellation> allConstellations = getAllConstellations();
        return allConstellations.indexOf(c);
    }

    public static IConstellation getConstellationById(int id) {
        List<IConstellation> allConstellations = getAllConstellations();
        return allConstellations.get(id);
    }

}
