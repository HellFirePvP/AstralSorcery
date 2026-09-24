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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionDamageItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionDamageItem extends ArtifactCondition implements OnHitArtifactCondition {

    public static final MapCodec<ArtifactConditionDamageItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            Ingredient.CODEC.listOf().fieldOf("items").forGetter(ArtifactConditionDamageItem::getItems),
            ComponentSerialization.CODEC.fieldOf("hiddenDescription").forGetter(ArtifactConditionDamageItem::getHiddenDescription),
            ComponentSerialization.CODEC.fieldOf("clearDescription").forGetter(ArtifactConditionDamageItem::getClearDescription)
    ).apply(inst, ArtifactConditionDamageItem::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionDamageItem> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            ArtifactConditionDamageItem::getItems,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionDamageItem::getHiddenDescription,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionDamageItem::getClearDescription,
            ArtifactConditionDamageItem::new);

    private final List<Ingredient> items;
    private final Component hiddenDescription;
    private final Component clearDescription;

    protected ArtifactConditionDamageItem(ResourceLocation id, List<Ingredient> items, Component hiddenDescription, Component clearDescription) {
        super(id);
        this.items = items;
        this.hiddenDescription = hiddenDescription;
        this.clearDescription = clearDescription;
    }

    public List<Ingredient> getItems() {
        return this.items;
    }

    public Component getHiddenDescription() {
        return this.hiddenDescription;
    }

    public Component getClearDescription() {
        return this.clearDescription;
    }

    public static Builder of(Component hiddenDescription, Component clearDescription) {
        return new Builder(hiddenDescription, clearDescription);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.DAMAGE_ITEM;
    }

    @Override
    public boolean isFulfilled(ItemEntityArtifact artifact, RandomSource rand, DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof LivingEntity livingEntity) {
            for (Ingredient ingredient : this.getItems()) {
                if (ingredient.test(livingEntity.getWeaponItem())) return true;
            }
        }
        return false;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        return List.of();
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {}

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return wasSuccessful ? this.getClearDescription() : this.getHiddenDescription();
    }

    public static class Builder {

        private final List<Ingredient> items = new ArrayList<>();
        private final Component hiddenDescription;
        private final Component clearDescription;

        public Builder(Component hiddenDescription, Component clearDescription) {
            this.hiddenDescription = hiddenDescription;
            this.clearDescription = clearDescription;
        }

        public Builder item(Ingredient... item) {
            this.items.addAll(List.of(item));
            return this;
        }

        public Provider build() {
            if (this.items.isEmpty()) {
                throw new IllegalArgumentException("Cannot build ArtifactConditionDamageItem with no items specified!");
            }
            return id -> new ArtifactConditionDamageItem(id, this.items, this.hiddenDescription, this.clearDescription);
        }
    }
}
