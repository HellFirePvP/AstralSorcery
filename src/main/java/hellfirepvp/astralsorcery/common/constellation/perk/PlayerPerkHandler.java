/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import hellfirepvp.astralsorcery.common.registry.RegistryEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerPerkHandler
 * Created by HellFirePvP
 * Date: 02.12.2016 / 20:02
 */
public class PlayerPerkHandler implements ITickHandler {

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer ticked = (EntityPlayer) context[0];
        PlayerProgress prog = ResearchManager.getProgress(ticked, (Side) context[1]);
        if(prog != null) {
            Map<ConstellationPerk, Integer> perks = new HashMap<>(prog.getAppliedPerks());
            for (ConstellationPerk perk : perks.keySet()) {
                if(!prog.isPerkActive(perk)) continue;
                if(perk.mayExecute(ConstellationPerk.Target.PLAYER_TICK)) {
                    perk.onPlayerTick(ticked, (Side) context[1]);
                }
            }
        }
        boolean client = ticked.getEntityWorld().isRemote;
        for (EnchantmentPlayerWornTick e : RegistryEnchantments.wearableTickEnchantments) {
            int max = EnchantmentHelper.getMaxEnchantmentLevel(e, ticked);
            if(max > 0) {
                e.onWornTick(client, ticked, max);
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
        return "PlayerPerkHandler";
    }

}
