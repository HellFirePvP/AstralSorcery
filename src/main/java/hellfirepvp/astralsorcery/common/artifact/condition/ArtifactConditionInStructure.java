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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionInStructure
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionInStructure extends ArtifactCondition {

    public static final MapCodec<ArtifactConditionInStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            ResourceKey.codec(Registries.STRUCTURE).listOf().fieldOf("validStructures").forGetter(ArtifactConditionInStructure::getValidStructures),
            ComponentSerialization.CODEC.fieldOf("hiddenDescription").forGetter(ArtifactConditionInStructure::getHiddenStructureDescription),
            ComponentSerialization.CODEC.fieldOf("clearDescription").forGetter(ArtifactConditionInStructure::getClearStructureDescription)
    ).apply(inst, ArtifactConditionInStructure::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionInStructure> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            ResourceKey.streamCodec(Registries.STRUCTURE).apply(ByteBufCodecs.list()),
            ArtifactConditionInStructure::getValidStructures,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionInStructure::getHiddenStructureDescription,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionInStructure::getClearStructureDescription,
            ArtifactConditionInStructure::new);

    private final List<ResourceKey<Structure>> validStructures;
    private final Component hiddenStructureDescription;
    private final Component clearStructureDescription;

    protected ArtifactConditionInStructure(ResourceLocation id, List<ResourceKey<Structure>> validStructures, Component hiddenStructureDescription, Component clearStructureDescription) {
        super(id);
        this.validStructures = validStructures;
        this.hiddenStructureDescription = hiddenStructureDescription;
        this.clearStructureDescription = clearStructureDescription;
    }

    public List<ResourceKey<Structure>> getValidStructures() {
        return this.validStructures;
    }

    public Component getHiddenStructureDescription() {
        return this.hiddenStructureDescription;
    }

    public Component getClearStructureDescription() {
        return this.clearStructureDescription;
    }

    public static Provider of(List<ResourceKey<Structure>> validStructures, Component hiddenStructureDescription, Component clearStructureDescription) {
        return id -> new ArtifactConditionInStructure(id, validStructures, hiddenStructureDescription, clearStructureDescription);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.IN_STRUCTURE;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        for (ResourceKey<Structure> structureKey : this.getValidStructures()) {
            Structure structure = sLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).get(structureKey);
            if (structure == null) continue;
            if (sLevel.structureManager().getStructureAt(artifactEntity.blockPosition(), structure).isValid()) {
                List<Vector3> pos = new ArrayList<>();
                Stream.of(Direction.values()).forEach(dir -> {
                    pos.add(new Vector3(artifactEntity).add(new Vector3(dir.step()).multiply(2F)));
                });
                return pos;
            }
        }
        return List.of();
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {}

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return wasSuccessful ? this.getClearStructureDescription() : this.getHiddenStructureDescription();
    }
}
