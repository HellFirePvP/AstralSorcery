/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class SyncData<SA extends ClientSyncData<C>, SD extends ClientSyncDiffData<C>, C extends ClientData> {

    public void markDirty() {
        SyncDataManager.getInstance().markForUpdate(this.getType());
    }

    public abstract void clearLevel(ServerLevel sLevel);

    public abstract void clearAll();

    public abstract SA syncAllData();

    public abstract SD syncDiffData();

    public abstract Type<?, SA, SD, C> getType();

    public record Type<D extends SyncData<SA, SD, C>, SA extends ClientSyncData<C>, SD extends ClientSyncDiffData<C>, C extends ClientData>(
            Supplier<D> dataProvider,
            Supplier<C> clientDataProvider,
            StreamCodec<RegistryFriendlyByteBuf, SA> syncCodec,
            StreamCodec<RegistryFriendlyByteBuf, SD> syncDiffCodec) {}
}
