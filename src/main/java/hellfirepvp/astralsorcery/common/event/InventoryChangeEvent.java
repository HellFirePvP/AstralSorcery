/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InventoryChangeEvent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InventoryChangeEvent extends PlayerEvent {

    private final ItemStack newStack;

    public InventoryChangeEvent(ServerPlayer player, ItemStack newStack) {
        super(player);
        this.newStack = newStack;
    }

    public ServerPlayer getPlayer() {
        return MiscUtil.cast(super.getEntity());
    }

    public ItemStack getNewStack() {
        return this.newStack.copy();
    }
}
