/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.equipment;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EquipmentSourceProvider
 * Created by HellFirePvP
 * Date: 02.04.2020 / 18:56
 */
public class EquipmentSourceProvider extends ModifierSourceProvider<EquipmentModifierSource> {

    static final String KEY_MOD_IDENTIFIER = "modifier_identifier";

    public EquipmentSourceProvider() {
        super(ModifierManager.EQUIPMENT_PROVIDER_KEY);
    }

    @Override
    protected void update(ServerPlayerEntity playerEntity) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            ResourceLocation id = AstralSorcery.key("slot_" + slot.getName());

            ItemStack stack = playerEntity.getItemStackFromSlot(slot);
            EquipmentModifierSource slotSource = new EquipmentModifierSource(slot, stack);
            if (!stack.isEmpty()) {
                Collection<PerkAttributeModifier> modifiers = slotSource.getModifiers(playerEntity, LogicalSide.SERVER, false);
                if (!modifiers.isEmpty()) {
                    CompoundNBT nbt = NBTHelper.getPersistentData(stack);
                    if (!nbt.hasUniqueId(KEY_MOD_IDENTIFIER)) {
                        nbt.putUniqueId(KEY_MOD_IDENTIFIER, UUID.randomUUID());
                    }
                    updateSource(playerEntity, id, slotSource);
                } else {
                    updateSource(playerEntity, id, null);
                }
            } else {
                updateSource(playerEntity, id, null);
            }
        }
    }

    @Override
    public void serialize(EquipmentModifierSource source, PacketBuffer buf) {
        ByteBufUtils.writeEnumValue(buf, source.slot);
        ByteBufUtils.writeItemStack(buf, source.itemStack);
    }

    @Override
    public EquipmentModifierSource deserialize(PacketBuffer buf) {
        EquipmentSlotType type = ByteBufUtils.readEnumValue(buf, EquipmentSlotType.class);
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        return new EquipmentModifierSource(type, stack);
    }

}
