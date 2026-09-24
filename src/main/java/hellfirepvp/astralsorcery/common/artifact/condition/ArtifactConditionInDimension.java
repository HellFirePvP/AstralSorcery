/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactConditionTypesAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionInDimension
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionInDimension extends ArtifactCondition {

    public static final MapCodec<ArtifactConditionInDimension> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            ResourceKey.codec(Registries.DIMENSION_TYPE).fieldOf("dimensionType").forGetter(ArtifactConditionInDimension::getDimensionType),
            ComponentSerialization.CODEC.fieldOf("hiddenDescription").forGetter(ArtifactConditionInDimension::getHiddenDescription),
            ComponentSerialization.CODEC.fieldOf("clearDescription").forGetter(ArtifactConditionInDimension::getClearDescription)
    ).apply(inst, ArtifactConditionInDimension::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionInDimension> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            ResourceKey.streamCodec(Registries.DIMENSION_TYPE),
            ArtifactConditionInDimension::getDimensionType,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionInDimension::getHiddenDescription,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionInDimension::getClearDescription,
            ArtifactConditionInDimension::new);

    private final ResourceKey<DimensionType> dimensionType;
    private final Component hiddenDescription;
    private final Component clearDescription;

    protected ArtifactConditionInDimension(ResourceLocation id, ResourceKey<DimensionType> dimensionType, Component hiddenDescription, Component clearDescription) {
        super(id);
        this.dimensionType = dimensionType;
        this.hiddenDescription = hiddenDescription;
        this.clearDescription = clearDescription;
    }

    public ResourceKey<DimensionType> getDimensionType() {
        return this.dimensionType;
    }

    public Component getHiddenDescription() {
        return this.hiddenDescription;
    }

    public Component getClearDescription() {
        return this.clearDescription;
    }

    public static Provider of(ResourceKey<DimensionType> dimensionType, Component hiddenDescription, Component clearDescription) {
        return id -> new ArtifactConditionInDimension(id, dimensionType, hiddenDescription, clearDescription);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.IN_DIMENSION;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        if (!sLevel.dimensionTypeRegistration().is(this.getDimensionType())) return List.of();
        List<Vector3> pos = new ArrayList<>();
        Stream.of(Direction.values()).forEach(dir -> {
            pos.add(new Vector3(artifactEntity).add(new Vector3(dir.step()).multiply(2F)));
        });
        return pos;
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {}

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return wasSuccessful ? this.getClearDescription() : this.getHiddenDescription();
    }
}
