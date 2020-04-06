/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeHelmetRender
 * Created by HellFirePvP
 * Date: 05.04.2020 / 11:33
 */
public class TypeHelmetRender extends PatreonEffect {

    private final UUID playerUUID;
    private final ItemStack helmetStack;

    private boolean addedHelmet = false;

    public TypeHelmetRender(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID, ItemStack helmetStack) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
        this.helmetStack = helmetStack.copy();
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void renderPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        if (player.getUniqueID().equals(playerUUID) && player.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
            player.inventory.armorInventory.set(EquipmentSlotType.HEAD.getIndex(), ItemUtils.copyStackWithSize(helmetStack, 1));
            addedHelmet = true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void renderPost(RenderPlayerEvent.Post event) {
        PlayerEntity player = event.getPlayer();
        if (player.getUniqueID().equals(playerUUID) && addedHelmet) {
            player.inventory.armorInventory.set(EquipmentSlotType.HEAD.getIndex(), ItemStack.EMPTY);
            addedHelmet = false;
        }
    }
}
