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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyLastBreath
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:06
 */
public class KeyLastBreath extends KeyPerk {

    private static float digSpeedIncrease = 1.5F;
    private static float damageIncrease = 3F;

    public KeyLastBreath(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                digSpeedIncrease = cfg.getFloat("HarvestSpeed_Increase", getConfigurationSection(), digSpeedIncrease, 1F, 32F,
                        "Defines the dig speed multiplier you get additionally to your normal dig speed when being low on health (25% health = 75% of this additional multiplier)");
                damageIncrease = cfg.getFloat("Damage_Increase", getConfigurationSection(), damageIncrease, 1F, 32F,
                        "Defines the damage multiplier you get additionally to your normal damage when being low on health (25% health = 75% of this additional multiplier)");
            }
        });
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog != null && prog.hasPerkUnlocked(this)) {
                float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, damageIncrease);
                float healthPerc = 1F - (player.getHealth() / player.getMaxHealth());
                event.setAmount(event.getAmount() * (1F + (healthPerc * actIncrease)));
            }
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null && prog.hasPerkUnlocked(this)) {
            float actIncrease = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, digSpeedIncrease);
            float healthPerc = 1F - (player.getHealth() / player.getMaxHealth());
            event.setNewSpeed(event.getNewSpeed() * (1F + (healthPerc * actIncrease)));
        }
    }

}
