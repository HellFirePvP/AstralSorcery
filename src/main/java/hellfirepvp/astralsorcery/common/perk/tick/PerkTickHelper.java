/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tick;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTickHelper
 * Created by HellFirePvP
 * Date: 25.08.2019 / 22:04
 */
public class PerkTickHelper implements ITickHandler {

    public static final PerkTickHelper INSTANCE = new PerkTickHelper();

    private PerkTickHelper() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity ticked = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];
        PlayerProgress prog = ResearchHelper.getProgress(ticked, side);
        if (prog.isValid()) {
            for (AbstractPerk perk : prog.getAppliedPerks()) {
                if (perk instanceof PlayerTickPerk && prog.hasPerkEffect(perk)) {
                    ((PlayerTickPerk) perk).onPlayerTick(ticked, side);
                }
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
