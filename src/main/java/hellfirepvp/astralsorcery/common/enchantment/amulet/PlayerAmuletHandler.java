/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import baubles.api.BaubleType;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import hellfirepvp.astralsorcery.common.util.BaublesHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAmuletHandler
 * Created by HellFirePvP
 * Date: 14.04.2018 / 17:49
 */
public class PlayerAmuletHandler implements ITickHandler {

    public static final PlayerAmuletHandler INSTANCE = new PlayerAmuletHandler();

    private PlayerAmuletHandler() {}

    @SubscribeEvent
    public void attachAmuletItemCapability(AttachCapabilitiesEvent<ItemStack> itemCapEvent) {
        if(!EnchantmentUpgradeHelper.isItemBlacklisted(itemCapEvent.getObject())) {
            itemCapEvent.addCapability(AmuletHolderCapability.CAP_AMULETHOLDER_NAME, new AmuletHolderCapability.Provider());
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        clearAmuletTags((EntityPlayer) context[0]);
    }

    public void clearAmuletTags(EntityPlayer player) {
        boolean hasAmulet = false;
        for (ItemStack bauble : BaublesHelper.getWornBaublesForType(player, BaubleType.AMULET)) {
            if(!bauble.isEmpty() && (bauble.getItem() instanceof ItemEnchantmentAmulet)) {
                hasAmulet = true;
            }
        }
        EnchantmentUpgradeHelper.removeAmuletTagsAndCleanup(player, hasAmulet);
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
        return "PlayerAmuletHandler";
    }

}
