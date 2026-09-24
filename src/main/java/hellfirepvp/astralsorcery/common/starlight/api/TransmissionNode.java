/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.api;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransmissionNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TransmissionNode {

    Codec<TransmissionNode> CODEC = TransmissionNodeProvider.NODE_CODEC
            .dispatch(TransmissionNode::getProvider, TransmissionNodeProvider::nodeCodec);

    RandomSource rand = RandomSource.create();

    //Get the position of this node
    BlockPos getNodePos();

    //Get the provider creating instances of this node
    TransmissionNodeProvider<?> getProvider();

    //Updates information from the tileentity to this node.
    //Return true to indicate a successful data update.
    default <T extends BlockEntity> boolean updateFromTileEntity(T tile) {
        return true;
    }

    //Between 0 and 1; 1 = no loss
    default float getTransmissionLossMultiplier() {
        return 1F;
    }

    default void markDirty(Level level) {
        DataAS.DOMAIN_AS.getData(level, DataAS.KEY_STARLIGHT_NETWORK_DATA).markDirty(this.getNodePos());
    }
}
