/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape;

import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Constructor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectFactory
 * Created by HellFirePvP
 * Date: 10.10.2017 / 01:22
 */
public class CapeEffectFactory<V extends CapeArmorEffect> {

    private final Constructor<V> ctor;

    public CapeEffectFactory(Class<V> effect) {
        Constructor<V> v = null;
        try {
            v = effect.getDeclaredConstructor(NBTTagCompound.class);
            v.setAccessible(true);
        } catch (Exception e) {}
        this.ctor = v;
    }

    public V deserializeCapeEffect(NBTTagCompound cmp) {
        try {
            return ctor.newInstance(cmp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
