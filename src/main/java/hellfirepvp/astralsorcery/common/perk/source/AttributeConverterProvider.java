/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeConverterProvider
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:06
 */
public interface AttributeConverterProvider {

    Collection<PerkConverter> getConverters(PlayerEntity player, LogicalSide side, boolean ignoreRequirements);

}
