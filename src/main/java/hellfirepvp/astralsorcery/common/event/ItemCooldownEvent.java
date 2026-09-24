/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemCooldownEvent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemCooldownEvent extends PlayerEvent {

    private final int originalCooldown;
    private int cooldown;

    public ItemCooldownEvent(Player player, int cooldown) {
        super(player);
        this.originalCooldown = cooldown;
        this.setCooldown(this.getOriginalCooldown());
    }

    public int getOriginalCooldown() {
        return this.originalCooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
