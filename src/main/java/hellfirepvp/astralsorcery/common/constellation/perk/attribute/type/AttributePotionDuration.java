/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.PotionApplyEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributePotionDuration
 * Created by HellFirePvP
 * Date: 26.10.2018 / 23:04
 */
public class AttributePotionDuration extends PerkAttributeType {

    public AttributePotionDuration() {
        super(AttributeTypeRegistry.ATTR_TYPE_POTION_DURATION);
    }

    @SubscribeEvent
    public void onPotionDurationNew(PotionApplyEvent.New event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            modifyPotionDuration((EntityPlayer) event.getEntityLiving(), event.getPotionEffect());
        }
    }

    @SubscribeEvent
    public void onPotionDurationChanged(PotionApplyEvent.Changed event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            modifyPotionDuration((EntityPlayer) event.getEntityLiving(), event.getNewEffect());
        }
    }

    private void modifyPotionDuration(EntityPlayer player, PotionEffect effect) {
        if (player.world.isRemote) {
            return;
        }

        int dur = effect.getDuration();

        float newDur = PerkAttributeHelper.getOrCreateMap(player, player.world.isRemote ? Side.CLIENT : Side.SERVER)
                .modifyValue(ResearchManager.getProgress(player, Side.SERVER), AttributeTypeRegistry.ATTR_TYPE_POTION_DURATION, dur);
        effect.duration = MathHelper.floor(newDur);
    }

}
