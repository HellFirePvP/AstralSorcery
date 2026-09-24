/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tooltip;

import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredLumenDisplayTooltip
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StoredLumenDisplayTooltip(ItemStack stack, IdentifierComponent identifier, StoredLumenComponent lumenComponent, int maxComponentWidth) implements TooltipComponent {

    public static StoredLumenDisplayTooltip of(ItemStack stack, UUID id, StoredLumenComponent lumenComponent, int maxComponentWidth) {
        return new StoredLumenDisplayTooltip(stack, new IdentifierComponent(id), lumenComponent, maxComponentWidth);
    }

    public static StoredLumenDisplayTooltip of(ItemStack stack, IdentifierComponent identifier, StoredLumenComponent lumenComponent) {
        return new StoredLumenDisplayTooltip(stack, identifier, lumenComponent, Integer.MAX_VALUE);
    }

    public static StoredLumenDisplayTooltip of(ItemStack stack, UUID id, StoredLumenComponent lumenComponent) {
        return of(stack, new IdentifierComponent(id), lumenComponent);
    }

    public static StoredLumenDisplayTooltip of(UUID id, StoredLumenComponent lumenComponent) {
        return of(ItemStack.EMPTY, new IdentifierComponent(id), lumenComponent);
    }

    public static StoredLumenDisplayTooltip of(UUID id, StoredLumenComponent.StoredLumen... lumen) {
        return of(id, new StoredLumenComponent(List.of(lumen)));
    }
}
