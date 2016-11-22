package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMajorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:08
 */
public interface IMajorConstellation extends IConstellation {

    //Those 2 methods are registry lookups. Try to avoid spamming them maybe.

    @Nullable
    public ConstellationPerkMap getPerkMap();

    @Nullable
    public ConstellationEffect getRitualEffect();

}
