/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import net.minecraft.entity.player.PlayerEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CooldownPerk
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:07
 */
public interface CooldownPerk {

    void onCooldownTimeout(PlayerEntity player);

}
