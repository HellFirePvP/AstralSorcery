/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscidiaRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:11
 */
public class DiscidiaRootPerk extends RootPerk {

    public DiscidiaRootPerk(int x, int y) {
        super("discidia", Constellations.discidia, x, y);
    }

    //Measure actual outcome of dmg
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingDamageEvent event) {
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        DamageSource ds = event.getSource();
        EntityPlayer player = null;
        if (ds.getImmediateSource() != null &&
                ds.getImmediateSource() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getImmediateSource();
        }
        if (player == null && ds.getTrueSource() != null &&
                ds.getTrueSource() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getTrueSource();
        }
        if (player != null) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog != null && prog.hasPerkUnlocked(this)) {
                float dmgDealt = event.getAmount();
                dmgDealt *= 0.2F;
                dmgDealt *= expMultiplier;
                dmgDealt = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, dmgDealt);
                ResearchManager.modifyExp(player, dmgDealt);
            }
        }
    }

}
