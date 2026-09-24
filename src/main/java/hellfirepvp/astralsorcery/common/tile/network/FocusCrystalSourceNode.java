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
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.linking.LinkContainer;
import hellfirepvp.astralsorcery.common.linking.SimpleLineOfSightLinkable;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionSourceNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalSourceNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalSourceNode implements TransmissionSourceNode, SimpleLineOfSightLinkable {

    public static final MapCodec<FocusCrystalSourceNode> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(FocusCrystalSourceNode::getNodePos),
            LinkContainer.CODEC.fieldOf("linkData").forGetter(node -> node.linkContainer),
            Codec.INT.fieldOf("validLayers").forGetter(FocusCrystalSourceNode::getValidLayers),
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("constellation").forGetter(FocusCrystalSourceNode::getConstellation),
            CrystalAttributesComponent.CODEC.fieldOf("crystalProperties").forGetter(FocusCrystalSourceNode::getCrystalProperties)
    ).apply(inst, FocusCrystalSourceNode::new));

    private final BlockPos pos;
    private final LinkContainer linkContainer;
    private int validLayers;
    private BaseConstellation constellation;
    private CrystalAttributesComponent crystalProperties;

    public FocusCrystalSourceNode(BlockPos pos) {
        this(pos, new LinkContainer(), 0, Optional.empty(), CrystalAttributesComponent.defaultEmpty());
    }

    protected FocusCrystalSourceNode(BlockPos pos, LinkContainer linkContainer, int validLayers, Optional<BaseConstellation> cst, CrystalAttributesComponent crystalProperties) {
        this.pos = pos;
        this.linkContainer = linkContainer;
        this.validLayers = validLayers;
        this.constellation = cst.orElse(null);
        this.crystalProperties = crystalProperties;
    }

    public void setConstellation(@Nullable BaseConstellation constellation) {
        this.constellation = constellation;
    }

    public Optional<BaseConstellation> getConstellation() {
        return Optional.ofNullable(this.constellation);
    }

    public void setCrystalProperties(CrystalAttributesComponent crystalProperties) {
        this.crystalProperties = crystalProperties;
    }

    public CrystalAttributesComponent getCrystalProperties() {
        return this.crystalProperties;
    }

    public void setValidLayers(int validLayers) {
        this.validLayers = validLayers;
    }

    public int getValidLayers() {
        return this.validLayers;
    }

    @Override
    public Optional<StarlightTransmissionPacket> produceStarlight(Level level) {
        if (this.constellation == null) return Optional.empty();
        return LevelSkyHandler.getContext(level).flatMap(ctx -> {
            float generated = CrystalPropertyCalculator.getStarlightFocusRate(this.crystalProperties);
            float distributionMultiplier = ctx.getConstellationHandler().getDistributionMultiplier(level, this.constellation);
            float dayTimeMultiplier = DayTimeHelper.getCurrentDaytimeDistribution(level);

            return Optional.of(new StarlightTransmissionPacket(this.constellation,
                    generated * distributionMultiplier * dayTimeMultiplier));
        });
    }

    @Override
    public void updateLinkStateChange(ServerLevel sLevel, BlockPos to, boolean isLink) {
        StarlightTransmissionLevelHelper.getInstance().getHandler(sLevel)
                .ifPresent(handler -> handler.notifyNodeChange(this));
        this.markDirty(sLevel);
    }

    @Override
    public Optional<Integer> getMaxBlockLinkDistance() {
        return Optional.of(16);
    }

    @Override
    public Optional<LinkContainer> getLinkDataContainer() {
        return Optional.of(this.linkContainer);
    }

    @Override
    public BlockPos getLinkablePos() {
        return this.getNodePos();
    }

    @Override
    public BlockPos getNodePos() {
        return this.pos;
    }

    @Override
    public TransmissionSourceNodeProvider<?> getProvider() {
        return StarlightNetworkNodesAS.FOCUS_CRYSTAL_SOURCE_NODE.get();
    }
}
