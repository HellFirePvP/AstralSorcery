/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.PlayerActivityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ArmaraRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:22
 */
public class ArmaraRootPerk extends RootPerk {

    public ArmaraRootPerk(int x, int y) {
        super("armara", Constellations.armara, x, y);
    }

    //Measure firstmost incoming damage
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurt(LivingHurtEvent event) {
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PlayerProgress prog = ResearchManager.getProgress(player, side);

            if (!PlayerActivityManager.INSTANCE.isPlayerActiveServer(player)) {
                return;
            }

            if (prog.hasPerkEffect(this)) {
                float expGain = event.getAmount();
                expGain *= 3F;
                if (event.getSource().isFireDamage()) {
                    if (player.isInLava()) {
                        expGain *= 0.01F;
                    } else {
                        expGain *= 0.2F;
                    }
                }
                if (event.getSource() == DamageSource.STARVE) {
                    expGain *= 0.1F;
                }
                if (event.getSource() == DamageSource.DROWN) {
                    expGain *= 0.05F;
                }
                if (event.getSource() == DamageSource.CACTUS) {
                    expGain *= 0.01F;
                }
                if (event.getSource() instanceof EntityDamageSource) {
                    expGain *= 1.3F;
                }
                expGain *= expMultiplier;
                expGain = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, expGain);
                expGain = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, expGain);
                expGain = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, expGain);
                ResearchManager.modifyExp(player, expGain);
            }
        }
    }

}
