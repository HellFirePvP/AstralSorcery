/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.StoredItemsComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.LootUtil;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputGenerateArtifactShardLoot
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputGenerateArtifactShardLoot extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputGenerateArtifactShardLoot> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            IntRange.CODEC.fieldOf("count_range").forGetter(AltarOutputGenerateArtifactShardLoot::getCountRange)
    ).apply(inst, AltarOutputGenerateArtifactShardLoot::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputGenerateArtifactShardLoot> STREAM_CODEC = StreamCodec.composite(
            IntRange.STREAM_CODEC,
            AltarOutputGenerateArtifactShardLoot::getCountRange,
            AltarOutputGenerateArtifactShardLoot::new);
    public static final Type<AltarOutputGenerateArtifactShardLoot> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final IntRange countRange;

    private AltarOutputGenerateArtifactShardLoot(IntRange countRange) {
        this.countRange = countRange;
    }

    public static AltarOutputGenerateArtifactShardLoot of(int min, int max) {
        return new AltarOutputGenerateArtifactShardLoot(IntRange.of(min, max));
    }

    public IntRange getCountRange() {
        return this.countRange;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        if (input.getAltar().getLevel() instanceof ServerLevel sLevel) {
            StoredItemsComponent cmp = output.getOrDefault(DataComponentsAS.STORED_ITEMS, StoredItemsComponent.EMPTY);
            List<ItemStack> generatedLoot = LootUtil.generateArtifactLoot(sLevel, this.countRange, sLevel.getRandom());
            for (ItemStack generated : generatedLoot) {
                cmp = cmp.accept(generated);
            }
            output.set(DataComponentsAS.STORED_ITEMS, cmp);
        }
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        ItemLore lore = output.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lore = lore.withLineAdded(Component.translatable("recipe.astralsorcery.result.generate_artifact_shard_loot").withStyle(ChatFormatting.GRAY));
        output.set(DataComponents.LORE, lore);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
