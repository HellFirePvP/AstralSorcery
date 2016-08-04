package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightTransmissionHandler
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:24
 */
public class StarlightTransmissionHandler implements ITickHandler {

    private static final StarlightTransmissionHandler instance = new StarlightTransmissionHandler();

    private Map<Integer, List<StarlightTransmissionTicket>> worldTicketMap = new HashMap<>();

    private StarlightTransmissionHandler() {}

    public static StarlightTransmissionHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if(world.isRemote) return;

        List<StarlightTransmissionTicket> tickets = worldTicketMap.get(world.provider.getDimension());
        if(tickets == null) {
            tickets = new LinkedList<>();
            worldTicketMap.put(world.provider.getDimension(), tickets);
        }
        WorldNetworkHandler handler = WorldNetworkHandler.getNetworkHandler(world);

        for (StarlightTransmissionTicket ticket : tickets) {

        }

    }

    /*public static List<IPrismTransmissionNode> getNextNodes(IPrismTransmissionNode node, WorldNetworkHandler handler) {

    }*/

    public static class StarlightTransmissionTicket {

        private final Constellation type;
        private final double starlightAmount;

        public StarlightTransmissionTicket(Constellation type, double starlightAmount) {
            this.type = type;
            this.starlightAmount = starlightAmount;
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Starlight Transmission Handler";
    }

}
