/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.artifact.condition.*;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionTypesAS {

    public static final DeferredRegister<ArtifactCondition.Type<?>> ARTIFACT_CONDITION_TYPES_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_ARTIFACT_CONDITION_TYPES, AstralSorcery.MODID);

    public static final ArtifactCondition.DeferredType<ArtifactConditionNearBlock> NEAR_BLOCK =
            register("near_block", ArtifactConditionNearBlock.CODEC, ArtifactConditionNearBlock.STREAM_CODEC);
    public static final ArtifactCondition.DeferredType<ArtifactConditionNearEntity> NEAR_ENTITY =
            register("near_entity", ArtifactConditionNearEntity.CODEC, ArtifactConditionNearEntity.STREAM_CODEC);
    public static final ArtifactCondition.DeferredType<ArtifactConditionInDimension> IN_DIMENSION =
            register("in_dimension", ArtifactConditionInDimension.CODEC, ArtifactConditionInDimension.STREAM_CODEC);
    public static final ArtifactCondition.DeferredType<ArtifactConditionInStructure> IN_STRUCTURE =
            register("in_structure", ArtifactConditionInStructure.CODEC, ArtifactConditionInStructure.STREAM_CODEC);
    public static final ArtifactCondition.DeferredType<ArtifactConditionDamageType> DAMAGE_TYPE =
            register("damage_type", ArtifactConditionDamageType.CODEC, ArtifactConditionDamageType.STREAM_CODEC);
    public static final ArtifactCondition.DeferredType<ArtifactConditionDamageItem> DAMAGE_ITEM =
            register("damage_item", ArtifactConditionDamageItem.CODEC, ArtifactConditionDamageItem.STREAM_CODEC);

    private static <T extends ArtifactCondition> ArtifactCondition.DeferredType<T> register(String name,
                                                                                            MapCodec<T> codec,
                                                                                            StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new ArtifactCondition.DeferredType<>(ARTIFACT_CONDITION_TYPES_REGISTER.register(name,
                () -> new ArtifactCondition.Type<>(codec, streamCodec)));
    }
}
