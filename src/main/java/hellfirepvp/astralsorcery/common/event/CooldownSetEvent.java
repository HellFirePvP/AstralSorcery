package hellfirepvp.astralsorcery.common.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CooldownSetEvent
 * Created by HellFirePvP
 * Date: 27.07.2020 / 21:05
 */
public class CooldownSetEvent extends Event {

    private final PlayerEntity player;
    private final Item item;
    private final int originalCooldown;
    private int cooldown;

    public CooldownSetEvent(PlayerEntity player, Item item, int originalCooldown) {
        this.player = player;
        this.item = item;
        this.originalCooldown = originalCooldown;
        this.setCooldown(this.getOriginalCooldown());
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public int getOriginalCooldown() {
        return originalCooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getResultCooldown() {
        return cooldown;
    }
}
