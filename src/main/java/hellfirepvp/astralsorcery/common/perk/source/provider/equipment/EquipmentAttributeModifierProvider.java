/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.equipment;

import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EquipmentAttributeModifierProvider
 * Created by HellFirePvP
 * Date: 02.04.2020 / 22:05
 */
//Special extension to attribute modifier providers for items in the equipment context
public interface EquipmentAttributeModifierProvider extends AttributeModifierProvider {

    @Override
    default Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        return Collections.emptyList();
    }

    Collection<PerkAttributeModifier> getModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements);

}
