/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.tick;

import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITickHandler
 * Created by HellFirePvP
 * Date: 04.08.2016 / 11:21
 */
public interface ITickHandler {

    public void tick(TickEvent.Type type, Object... context);

    /**
     * WORLD, context: world
     * SERVER, context:
     * CLIENT, context:
     * RENDER, context: pTicks
     * PLAYER, context: player, side
     */
    public EnumSet<TickEvent.Type> getHandledTypes();

    public boolean canFire(TickEvent.Phase phase);

    public String getName();

}
