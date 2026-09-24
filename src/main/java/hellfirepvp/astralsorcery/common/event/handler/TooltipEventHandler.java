/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.component.DynamicTooltipComponent;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TooltipEventHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TooltipEventHandler {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(TooltipEventHandler::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event) {
        List<Component> newLines = new ArrayList<>();
        addComponentTooltip(event.getItemStack(), event.getContext(), newLines::add, event.getEntity(), event.getFlags());
        addStoredLumenTooltip(event.getItemStack(), newLines::add);

        List<Component> tip = event.getToolTip();
        tip.addAll(Math.min(1, tip.size()), newLines);
    }

    public static void addComponentTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> tooltipAdder, @Nullable Player player, TooltipFlag flag) {
        AttributeTooltipContext ctx = AttributeTooltipContext.of(player, context, flag);

        BuiltInRegistries.DATA_COMPONENT_TYPE.forEach(componentType -> {
            Object component = stack.get(componentType);
            if (component instanceof DynamicTooltipComponent dynamicTooltipComponent) {
                dynamicTooltipComponent.addTooltips(stack, tooltipAdder, ctx);
            }
        });
    }

    public static void addStoredLumenTooltip(ItemStack stack, Consumer<Component> tooltipAdder) {
        if (stack.has(DataComponentsAS.STORED_LUMEN) && stack.has(DataComponentsAS.IDENTIFIER)) {
            IdentifierComponent idCmp = stack.getOrDefault(DataComponentsAS.IDENTIFIER, IdentifierComponent.NONE);
            if (!idCmp.id().equals(Util.NIL_UUID)) {
                tooltipAdder.accept(Component.literal(stack.get(DataComponentsAS.IDENTIFIER).id().toString()));
            }
        }
    }
}
