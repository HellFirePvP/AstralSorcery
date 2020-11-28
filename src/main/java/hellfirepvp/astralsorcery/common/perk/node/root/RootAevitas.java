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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootAevitas
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:49
 */
public class RootAevitas extends RootPerk {

    public static final Config CONFIG = new Config("root.aevitas");

    public RootAevitas(ResourceLocation name, float x, float y) {
        super(name, CONFIG, ConstellationsAS.aevitas, x, y);
    }

    @Nonnull
    @Override
    protected DiminishingMultiplier createMultiplier() {
        return new DiminishingMultiplier(600L, 1F, 1F, 0.01F);
    }

    @Override
    protected void attachListeners(LogicalSide side, IEventBus bus) {
        super.attachListeners(side, bus);

        bus.addListener(this::onPlace);
    }

    private void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntity();
        LogicalSide side = this.getSide(player);
        if (!side.isServer()) {
            return;
        }

        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (!prog.getPerkData().hasPerkEffect(this)) {
            return;
        }

        float hardness;
        try {
            hardness = Math.max(event.getPlacedBlock().getBlockHardness(event.getWorld(), event.getPos()), 1F);
        } catch (Exception exc) {
            hardness = 1F;
        }
        float xp = Math.min(hardness * 6F, 100F);
        xp *= this.getExpMultiplier();
        xp *= this.getDiminishingReturns(player);
        xp *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
        xp *= PerkAttributeHelper.getOrCreateMap(player, side).getModifier(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP);

        xp = AttributeEvent.postProcessModded(player, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP, xp);

        ResearchManager.modifyExp(player, xp);
    }
}
