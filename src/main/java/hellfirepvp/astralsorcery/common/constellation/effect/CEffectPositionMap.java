/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.nbt.NBTBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionMap
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:24
 */
public abstract class CEffectPositionMap<K extends NBTBase, V extends NBTBase> extends CEffectPositionListGen<GenListEntries.PosDefinedTuple<K, V>> {

    public CEffectPositionMap(IMajorConstellation c, String cfgName, int searchRange, int maxCount, Verifier verifier) {
        super(c, cfgName, searchRange, maxCount, verifier, GenListEntries.PosDefinedTuple::new);
    }

}
