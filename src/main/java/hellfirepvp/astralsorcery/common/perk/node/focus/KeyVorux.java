/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.focus;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.ConstellationPerk;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyVorux
 * Created by HellFirePvP
 * Date: 25.08.2019 / 19:38
 */
public class KeyVorux extends ConstellationPerk {

    public KeyVorux(ResourceLocation name, int x, int y) {
        super(name, ConstellationsAS.vorux, x, y);
        setCategory(CATEGORY_FOCUS);
        this.addModifier(new PerkAttributeModifier(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ModifierType.ADDED_MULTIPLY, 0.01F) {
            @Override
            protected void initModifier() {
                super.initModifier();

                this.setAbsolute();
            }

            @Override
            public float getValue(PlayerEntity player, PlayerProgress progress) {
                return getRawValue() * (progress.getAppliedPerks().size() - progress.getSealedPerks().size());
            }

            @Override
            public boolean hasDisplayString() {
                return false;
            }
        });
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(this::onExpGain);
    }

    public void onExpGain(AttributeEvent.PostProcessModded ev) {
        if (ev.getType().equals(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP)) {
            PlayerEntity player = ev.getPlayer();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                ev.setValue(0);
            }
        }
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        return super.mayUnlockPerk(progress, player) &&
                !MiscUtils.contains(progress.getAppliedPerks(), perk -> perk.getCategory().equals(CATEGORY_FOCUS));
    }
}
