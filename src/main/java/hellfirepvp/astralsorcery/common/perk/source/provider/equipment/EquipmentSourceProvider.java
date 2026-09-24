/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.equipment;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.Arrays;
import java.util.EnumMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EquipmentSourceProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EquipmentSourceProvider extends ModifierSourceProvider<EquipmentModifierSource> {

    private static final EnumMap<EquipmentSlot, ResourceLocation> SLOT_IDS = new EnumMap<>(EquipmentSlot.class) {
        {
            Arrays.stream(EquipmentSlot.values()).forEach(slot -> this.put(slot, AstralSorcery.key(slot.getName())));
        }
    };

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(EquipmentSourceProvider::onEquipmentChange);
    }

    // update technically also covers this, but this is quicker so it may avoid weird fov jitter
    private static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.OFFHAND) return;
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        if (event.getEntity().level().isClientSide()) return;
        EquipmentSourceProvider provider = PerksAS.Sources.EQUIPMENT.get();

        provider.updateSource(sPlayer, event.getSlot(), event.getTo());
    }

    @Override
    protected void update(ServerPlayer playerEntity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.OFFHAND) continue;

            this.updateSource(playerEntity, slot, playerEntity.getItemBySlot(slot));
        }
    }

    private void updateSource(ServerPlayer player, EquipmentSlot slot, ItemStack stack) {
        ItemStack newStack = stack.copy();
        EquipmentModifierSource slotSource = new EquipmentModifierSource(slot, newStack);
        if (!newStack.isEmpty()) {
            if (!slotSource.getModifiers(player, LogicalSide.SERVER, false).isEmpty()) {
                IdentifierComponent.createIdentifierIfNotExists(stack);
                this.updateSource(player, SLOT_IDS.get(slot), slotSource);
            } else {
                this.removeSource(player, SLOT_IDS.get(slot));
            }
        } else {
            this.removeSource(player, SLOT_IDS.get(slot));
        }
    }

    @Override
    protected void removeModifiers(ServerPlayer playerEntity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.OFFHAND) continue;

            this.removeSource(playerEntity, SLOT_IDS.get(slot));
        }
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EquipmentModifierSource> getModifierSourceSyncCodec() {
        return EquipmentModifierSource.STREAM_CODEC;
    }
}
