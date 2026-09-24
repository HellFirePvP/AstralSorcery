/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientSyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ClientSyncDiffData<C extends ClientData> {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSyncDiffData<?>> STREAM_CODEC = ByteBufCodecs.registry(RegistriesAS.KEY_SYNC_DATA_TYPES)
            .dispatch(ClientSyncDiffData::type, SyncData.Type::syncDiffCodec);

    protected C getClientData() {
        return SyncDataManager.getInstance().getClientData(this.type());
    }

    public abstract void updateClientData(C dataOut);

    public abstract SyncData.Type<?, ?, ?, C> type();

}
