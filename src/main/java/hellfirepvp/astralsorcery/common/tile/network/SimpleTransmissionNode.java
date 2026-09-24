/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.linking.LinkContainer;
import hellfirepvp.astralsorcery.common.linking.SimpleLineOfSightLinkable;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionLevelHelper;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SimpleTransmissionNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SimpleTransmissionNode implements TransmissionNode, SimpleLineOfSightLinkable {

    public static final MapCodec<SimpleTransmissionNode> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(SimpleTransmissionNode::getNodePos),
            LinkContainer.CODEC.fieldOf("linkContainer").forGetter(node -> node.linkContainer),
            Codec.FLOAT.fieldOf("lossMultiplier").forGetter(SimpleTransmissionNode::getLossMultiplier)
    ).apply(inst, SimpleTransmissionNode::new));

    protected final BlockPos pos;
    protected final LinkContainer linkContainer;
    protected float lossMultiplier;

    public SimpleTransmissionNode(BlockPos pos) {
        this(pos, new LinkContainer(), 1F);
    }

    public SimpleTransmissionNode(BlockPos pos, LinkContainer linkContainer, float lossMultiplier) {
        this.pos = pos;
        this.linkContainer = linkContainer;
        this.lossMultiplier = lossMultiplier;
    }

    @Override
    public void updateLinkStateChange(ServerLevel sLevel, BlockPos to, boolean isLink) {
        StarlightTransmissionLevelHelper.getInstance().getHandler(sLevel)
                .ifPresent(handler -> handler.notifyNodeChange(this));
        this.markDirty(sLevel);

        MiscUtil.getTileAt(sLevel, this.getNodePos(), TileLens.class, true).ifPresent(lens -> {
            lens.getTileData().setLinkedPos(isLink ? to : null);
            lens.getTileData().markForUpdate();
        });
    }

    @Override
    public <T extends BlockEntity> boolean updateFromTileEntity(T tile) {
        if (tile instanceof TileEntitySynchronized<?> syncedTile &&
                syncedTile.getTileData() instanceof TileDataCrystalAttributeContainer crystalContainer) {
            CrystalAttributesComponent cmp = crystalContainer.getCrystalAttributes();
            this.setLossMultiplier(CrystalPropertyCalculator.getStarlightTransmissionLoss(cmp));
            this.markDirty(tile.getLevel());
        }
        return true;
    }

    @Override
    public Optional<Integer> getMaxBlockLinkDistance() {
        return Optional.of(16);
    }

    @Override
    public BlockPos getNodePos() {
        return this.pos;
    }

    public void setLossMultiplier(float lossMultiplier) {
        this.lossMultiplier = lossMultiplier;
    }

    public float getLossMultiplier() {
        return this.lossMultiplier;
    }

    @Override
    public float getTransmissionLossMultiplier() {
        return this.getLossMultiplier();
    }

    @Override
    public TransmissionNodeProvider<?> getProvider() {
        return StarlightNetworkNodesAS.SIMPLE_NODE.value();
    }

    @Override
    public Optional<LinkContainer> getLinkDataContainer() {
        return Optional.of(this.linkContainer);
    }

    @Override
    public BlockPos getLinkablePos() {
        return this.getNodePos();
    }
}
