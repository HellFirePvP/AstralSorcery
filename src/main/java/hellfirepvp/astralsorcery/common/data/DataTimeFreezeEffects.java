/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopEffectHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataTimeFreezeEffects
 * Created by HellFirePvP
 * Date: 17.10.2017 / 22:49
 */
public class DataTimeFreezeEffects extends AbstractData {

    private Map<Integer, List<TimeStopEffectHelper>> clientActiveFreezeZones = new HashMap<>();
    private Map<Integer, List<TimeStopEffectHelper>> serverActiveFreezeZones = new HashMap<>();

    private List<ServerSyncAction> scheduledServerSyncChanges = new LinkedList<>();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    public void server_addNewEffect(int dimId, TimeStopEffectHelper effectHelper) {
        List<TimeStopEffectHelper> zones = serverActiveFreezeZones.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        zones.add(effectHelper);
        scheduledServerSyncChanges.add(new ServerSyncAction(ServerSyncAction.ActionType.ADD, dimId, effectHelper));
        markDirty();
    }

    public void server_removeEffect(int dimId, TimeStopEffectHelper effectHelper) {
        if(serverActiveFreezeZones.containsKey(dimId)) {
            serverActiveFreezeZones.get(dimId).remove(effectHelper);
        }
        scheduledServerSyncChanges.add(new ServerSyncAction(ServerSyncAction.ActionType.REMOVE, dimId, effectHelper));
        markDirty();
    }

    public void server_clearEffects(int dimId) {
        serverActiveFreezeZones.remove(dimId);
        scheduledServerSyncChanges.add(new ServerSyncAction(ServerSyncAction.ActionType.CLEAR, dimId, null));
        markDirty();
    }

    @Nullable
    public List<TimeStopEffectHelper> client_getTimeStopEffects(World world) {
        if(world.provider == null) return null;
        return client_getTimeStopEffects(world.provider.getDimension());
    }

    @Nullable
    public List<TimeStopEffectHelper> client_getTimeStopEffects(int dimensionId) {
        return clientActiveFreezeZones.get(dimensionId);
    }

    private void client_applyChange(ServerSyncAction action) {
        switch (action.type) {
            case ADD:
                List<TimeStopEffectHelper> zones = clientActiveFreezeZones.computeIfAbsent(action.dimId, (id) -> new LinkedList<>());
                zones.add(action.involvedEffect);
                break;
            case REMOVE:
                if(clientActiveFreezeZones.containsKey(action.dimId)) {
                    clientActiveFreezeZones.get(action.dimId).remove(action.involvedEffect);
                }
                break;
            case CLEAR:
                clientActiveFreezeZones.remove(action.dimId);
                break;
        }
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        compound.setBoolean("sync_all", true);

        NBTTagCompound dims = new NBTTagCompound();
        for (int dimId : serverActiveFreezeZones.keySet()) {
            NBTTagList cmp = new NBTTagList();
            for (TimeStopEffectHelper effect : serverActiveFreezeZones.get(dimId)) {
                cmp.appendTag(effect.serializeNBT());
            }
            dims.setTag(String.valueOf(dimId), cmp);
        }
        compound.setTag("dimensions", dims);
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        compound.setBoolean("sync_all", false);

        NBTTagList changes = new NBTTagList();
        for (ServerSyncAction action : scheduledServerSyncChanges) {
            changes.appendTag(action.serializeNBT());
        }
        compound.setTag("changes", changes);

        scheduledServerSyncChanges.clear();
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        this.clientReadBuffer = compound;
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if(!(serverData instanceof DataTimeFreezeEffects)) return;

        NBTTagCompound buf = ((DataTimeFreezeEffects) serverData).clientReadBuffer;
        if(buf.getBoolean("sync_all")) {
            NBTTagCompound dims = buf.getCompoundTag("dimensions");
            for (String key : dims.getKeySet()) {
                int dimId;
                try {
                    dimId = Integer.parseInt(key);
                } catch (Exception exc) {
                    continue;
                }
                List<TimeStopEffectHelper> effectList = new LinkedList<>();
                clientActiveFreezeZones.put(dimId, effectList);
                NBTTagList effects = dims.getTagList(key, Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < effects.tagCount(); i++) {
                    NBTTagCompound cmp = effects.getCompoundTagAt(i);
                    effectList.add(TimeStopEffectHelper.deserializeNBT(cmp));
                }
            }
        } else {
            NBTTagList changes = buf.getTagList("changes", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < changes.tagCount(); i++) {
                ServerSyncAction action = ServerSyncAction.deserializeNBT(changes.getCompoundTagAt(i));
                client_applyChange(action);
            }
        }
    }

    public void clientClean() {
        this.clientActiveFreezeZones = new HashMap<>();
        this.clientReadBuffer = null;
    }

    public static class ServerSyncAction {

        private final ActionType type;

        private final int dimId;
        private TimeStopEffectHelper involvedEffect;

        private ServerSyncAction(ActionType type, int dimId, TimeStopEffectHelper involvedEffect) {
            this.type = type;
            this.dimId = dimId;
            this.involvedEffect = involvedEffect;
        }

        private NBTTagCompound serializeNBT() {
            NBTTagCompound out = new NBTTagCompound();
            out.setInteger("type", type.ordinal());
            out.setInteger("dimId", dimId);
            switch (type) {
                case ADD:
                case REMOVE:
                    out.setTag("effectTag", involvedEffect.serializeNBT());
                    break;
            }
            return out;
        }

        private static ServerSyncAction deserializeNBT(NBTTagCompound cmp) {
            ActionType type = ActionType.values()[MathHelper.clamp(cmp.getInteger("type"), 0, ActionType.values().length - 1)];
            int dimId = cmp.getInteger("dimId");
            TimeStopEffectHelper helper = null;
            switch (type) {
                case ADD:
                case REMOVE:
                    helper = TimeStopEffectHelper.deserializeNBT(cmp.getCompoundTag("effectTag"));
                    break;
            }
            return new ServerSyncAction(type, dimId, helper);
        }

        public static enum ActionType {

            ADD,
            REMOVE,
            CLEAR

        }

    }

    public static class Provider extends ProviderAutoAllocate<DataTimeFreezeEffects> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataTimeFreezeEffects provideNewInstance() {
            return new DataTimeFreezeEffects();
        }

    }
}
