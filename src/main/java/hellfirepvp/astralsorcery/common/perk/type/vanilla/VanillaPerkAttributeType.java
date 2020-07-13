/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type.vanilla;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VanillaPerkAttributeType
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:07
 */
public interface VanillaPerkAttributeType {

    @Nonnull
    IAttribute getAttribute();

    void refreshAttribute(PlayerEntity player);

}
