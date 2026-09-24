/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.artifact.ArtifactStability;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.item.ArtifactItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IsStableArtifactIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IsStableArtifactIngredient implements ICustomIngredient {

    public static final IsStableArtifactIngredient INSTANCE = new IsStableArtifactIngredient();
    public static final MapCodec<IsStableArtifactIngredient> CODEC = MapCodec.unit(INSTANCE);

    private IsStableArtifactIngredient() {}

    @Override
    public boolean test(ItemStack stack) {
        ArtifactComponent cmp = stack.get(DataComponentsAS.ARTIFACT);
        return cmp != null && cmp.stability() == ArtifactStability.STABLE;
    }

    @Override
    public Stream<ItemStack> getItems() {
        Component display = Component.translatable("ingredient.astralsorcery.stable_artifact.description")
                .withStyle(ChatFormatting.GOLD);
        return RegistriesAS.REGISTRY_ARTIFACT_TYPES.stream()
                .map(ArtifactItem::create)
                .peek(artifact -> {
                    ArtifactComponent cmp = artifact.get(DataComponentsAS.ARTIFACT);
                    if (cmp != null) {
                        cmp = cmp.changeStability(ArtifactStability.STABLE);
                        artifact.set(DataComponentsAS.ARTIFACT, cmp);
                    }
                    artifact.set(DataComponents.ITEM_NAME, display);
                });
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return IngredientsAS.IS_STABLE_ARTIFACT.get();
    }
}
