/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DamageUtil
 * Created by HellFirePvP
 * Date: 17.11.2018 / 09:52
 */
public class DamageUtil {

    public static boolean attackEntityFrom(@Nonnull Entity attacked, @Nonnull DamageSource type, float amount) {
        return attacked.attackEntityFrom(type, amount);
    }

    public static boolean attackEntityFrom(@Nonnull Entity attacked, @Nonnull DamageSource type, float amount, @Nullable Entity newSource) {
        DamageSource newType = DamageSourceUtil.withEntityDirect(type, newSource);
        return attackEntityFrom(attacked, newType != null ? newType : type, amount);
    }

}
