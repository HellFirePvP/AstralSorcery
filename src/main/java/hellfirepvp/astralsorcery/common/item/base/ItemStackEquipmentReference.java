/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemStackEquipmentReference
 * Created by HellFirePvP
 * Date: 19.10.2026 / 10:00
 */
public interface ItemStackEquipmentReference {

    @Nullable
    UUID getPlayerReference();

    void setPlayerReference(@Nullable UUID playerReference);

    @Nullable
    EquipmentSlot getEquippedSlot();

    void setEquippedSlot(@Nullable EquipmentSlot slot);

    @Nullable
    static <T extends ItemStack & ItemStackEquipmentReference> Pair<UUID, EquipmentSlot> getEquipmentReference(ItemStack stack) {
        T ref = MiscUtil.cast(stack);
        UUID id = ref.getPlayerReference();
        EquipmentSlot slot = ref.getEquippedSlot();
        if (id == null || slot == null) return null;
        return new Pair<>(id, slot);
    }

    static <T extends ItemStack & ItemStackEquipmentReference> void setEquipmentReference(ItemStack stack, @Nullable UUID playerId, @Nullable EquipmentSlot slot) {
        T ref = MiscUtil.cast(stack);
        ref.setPlayerReference(playerId);
        ref.setEquippedSlot(slot);
    }
}
