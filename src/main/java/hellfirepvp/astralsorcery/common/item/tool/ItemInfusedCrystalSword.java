/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.CelestialStrike;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedCrystalSword
 * Created by HellFirePvP
 * Date: 04.04.2020 / 12:55
 */
public class ItemInfusedCrystalSword extends ItemCrystalSword implements EquipmentAttributeModifierProvider {

    private static final DynamicAttributeModifier INFUSED_SWORD_BASECRIT =
            new DynamicAttributeModifier(UUID.fromString("bf154d57-22ca-4b62-822e-2ad09df5f1e8"), PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_CHANCE, ModifierType.ADDITION, 5F);

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.getEntityWorld().isRemote() && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack sword = serverPlayer.getHeldItem(Hand.MAIN_HAND);
            if (!MiscUtils.isPlayerFakeMP(serverPlayer) &&
                    !sword.isEmpty() &&
                    sword.getItem() instanceof ItemInfusedCrystalSword &&
                    !serverPlayer.isSneaking() &&
                    !serverPlayer.getCooldownTracker().hasCooldown(sword.getItem())) {

                CelestialStrike.play(serverPlayer, serverPlayer.getEntityWorld(), Vector3.atEntityCorner(entity), Vector3.atEntityCorner(entity));
                serverPlayer.getCooldownTracker().setCooldown(sword.getItem(), 120);
            }
        }
        return false;
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        return Collections.singletonList(INFUSED_SWORD_BASECRIT);
    }
}
