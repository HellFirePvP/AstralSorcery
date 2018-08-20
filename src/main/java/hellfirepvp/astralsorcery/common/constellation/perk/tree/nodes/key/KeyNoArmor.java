/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyNoArmor
 * Created by HellFirePvP
 * Date: 11.08.2018 / 20:26
 */
public class KeyNoArmor extends KeyPerk {

    private float dmgReductionMultiplier = 0.7F;

    public KeyNoArmor(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                dmgReductionMultiplier = cfg.getFloat("ReductionMultiplier", getConfigurationSection(), dmgReductionMultiplier, 0.05F, 1F,
                        "The multiplier that is applied to damage the player receives. The lower the more damage is negated.");
            }
        });
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null && prog.hasPerkUnlocked(this)) {
            int eq = 0;
            for (ItemStack stack : player.getArmorInventoryList()) {
                if(!stack.isEmpty()) {
                    eq++;
                }
            }
            if (eq < 2) {
                float effMulti = PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);
                event.setAmount(event.getAmount() * (dmgReductionMultiplier * (1F / effMulti)));
            }
        }
    }

}
