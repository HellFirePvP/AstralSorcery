/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.root;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.RootPerk;
import hellfirepvp.astralsorcery.common.util.DiminishingMultiplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootDiscidia
 * Created by HellFirePvP
 * Date: 01.09.2019 / 10:17
 */
public class RootDiscidia extends RootPerk {

    public static final Config CONFIG = new Config("root.discidia");

    public RootDiscidia(ResourceLocation name, float x, float y) {
        super(name, CONFIG, ConstellationsAS.discidia, x, y);
    }

    @Nonnull
    @Override
    protected DiminishingMultiplier createMultiplier() {
        return new DiminishingMultiplier(6_000, 0.075F, 0.025F, 0.15F);
    }

    @Override
    public void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);

        bus.addListener(EventPriority.LOWEST, this::onDamage);
    }

    private void onDamage(LivingDamageEvent event) {
        DamageSource ds = event.getSource();
        PlayerEntity player = null;
        if (ds.getImmediateSource() != null &&
                ds.getImmediateSource() instanceof PlayerEntity) {
            player = (PlayerEntity) ds.getImmediateSource();
        }
        if (player == null && ds.getTrueSource() != null &&
                ds.getTrueSource() instanceof PlayerEntity) {
            player = (PlayerEntity) ds.getTrueSource();
        }
        if (player == null) {
            return;
        }

        LogicalSide side = this.getSide(player);
        if (!side.isServer()) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        float mul = 4.0F;
        CombatTracker combat = event.getEntityLiving().getCombatTracker();
        if (combat.inCombat) {
            if (combat.getCombatDuration() > (2 * 60 * 20)) {
                mul = 0.01F;
            }
        }

        float expGain = Math.min(event.getAmount() * mul, 100F);
        expGain *= this.getExpMultiplier();
        expGain *= this.getDiminishingReturns(player);
        expGain *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
        expGain *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP);
        expGain = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP, expGain);

        ResearchManager.modifyExp(player, expGain);
    }
}
