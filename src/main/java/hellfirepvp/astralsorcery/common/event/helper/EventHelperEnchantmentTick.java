/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.enchantment.EnchantmentPlayerTick;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHelperEnchantmentTick
 * Created by HellFirePvP
 * Date: 02.05.2020 / 12:56
 */
public class EventHelperEnchantmentTick implements ITickHandler {

    public static final EventHelperEnchantmentTick INSTANCE = new EventHelperEnchantmentTick();

    private Collection<EnchantmentPlayerTick> tickableEnchantments = null;

    private EventHelperEnchantmentTick() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];

        if (tickableEnchantments == null) {
            tickableEnchantments = ForgeRegistries.ENCHANTMENTS.getValues().stream()
                    .filter(enchantment -> enchantment instanceof EnchantmentPlayerTick)
                    .map(enchantment -> (EnchantmentPlayerTick) enchantment)
                    .collect(Collectors.toList());
        }

        for (EnchantmentPlayerTick ench : this.tickableEnchantments) {
            int totalLevel = EnchantmentHelper.getMaxEnchantmentLevel(ench, player);
            if (totalLevel > 0) {
                ench.tick(player, side, totalLevel);
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "TickEnchantment Helper";
    }
}
