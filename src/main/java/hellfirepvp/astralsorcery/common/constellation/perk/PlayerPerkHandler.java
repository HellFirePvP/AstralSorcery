package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.List;

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
        PlayerProgress prog = ResearchManager.getProgress(ticked);
        if(prog != null) {
            List<ConstellationPerk> perks = prog.getAppliedPerks();
            for (ConstellationPerk perk : perks) {
                if(perk.mayExecute(ConstellationPerk.Target.PLAYER_TICK)) {
                    perk.onPlayerTick(ticked, (Side) context[1]);
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
