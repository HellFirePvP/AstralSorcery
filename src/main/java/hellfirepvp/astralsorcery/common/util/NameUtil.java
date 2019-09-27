/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.base.CaseFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NameUtil
 * Created by HellFirePvP
 * Date: 23.09.2019 / 18:09
 */
public class NameUtil {

    public static ResourceLocation fromClass(Class<?> clazz) {
        return fromClass(clazz, null);
    }

    public static ResourceLocation fromClass(Object object) {
        return fromClass(object.getClass(), null);
    }

    public static ResourceLocation fromClass(Object object, @Nullable String cutPrefix) {
        return fromClass(object.getClass(), cutPrefix);
    }

    public static ResourceLocation fromClass(Class<?> clazz, @Nullable String cutPrefix) {
        String name = clazz.getSimpleName();
        if (clazz.getEnclosingClass() != null) {
            name = clazz.getEnclosingClass().getSimpleName() + name;
        }
        if (cutPrefix != null && name.startsWith(cutPrefix)) {
            name = name.substring(cutPrefix.length());
        }
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return AstralSorcery.key(name);
    }

}
