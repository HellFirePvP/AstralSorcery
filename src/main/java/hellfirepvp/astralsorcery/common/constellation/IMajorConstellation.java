package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMajorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:08
 */
public interface IMajorConstellation extends IConstellation {

    //TODO add Perks hook.

    public ConstellationEffect getRitualEffect();

}
