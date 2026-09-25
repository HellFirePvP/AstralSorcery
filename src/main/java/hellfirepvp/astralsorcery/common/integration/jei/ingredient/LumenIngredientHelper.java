/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.ingredient;

import com.google.common.base.MoreObjects;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenIngredientHelper
 * Created by HellFirePvP
 * Date: 25.09.2026 / 19:44
 */
public class LumenIngredientHelper implements IIngredientHelper<LumenStack> {

    private final ISubtypeManager subtypeManager;
    private final Registry<Lumen> registry;

    public LumenIngredientHelper(ISubtypeManager subtypeManager) {
        this.subtypeManager = subtypeManager;
        this.registry = RegistriesAS.REGISTRY_LUMEN;
    }

    @Override
    public IIngredientType<LumenStack> getIngredientType() {
        return LumenIngredientType.INSTANCE;
    }

    @Override
    public String getDisplayName(LumenStack ingredient) {
        return ingredient.getLumen().getName().getString();
    }

    @SuppressWarnings("removal")
    @Override
    public String getUniqueId(LumenStack ingredient, UidContext context) {
        Lumen lumen = ingredient.getLumen();
        ResourceLocation key = this.getRegistryName(ingredient, lumen);

        StringBuilder result = new StringBuilder()
                .append("lumen:")
                .append(key);

        String subData = this.subtypeManager.getSubtypeInfo(LumenIngredientType.INSTANCE, ingredient, context);
        if (!subData.isEmpty()) {
            result.append(":").append(subData);
        }
        return result.toString();
    }

    @Override
    public Object getGroupingUid(LumenStack ingredient) {
        return ingredient.getLumen();
    }

    @Override
    public Object getUid(LumenStack ingredient, UidContext context) {
        Lumen lumen = ingredient.getLumen();
        Object subData = this.subtypeManager.getSubtypeData(LumenIngredientType.INSTANCE, ingredient, context);
        if (subData != null) {
            return List.of(lumen, subData);
        }
        return lumen;
    }

    @Override
    public ResourceLocation getResourceLocation(LumenStack ingredient) {
        return this.getRegistryName(ingredient, ingredient.getLumen());
    }

	private ResourceLocation getRegistryName(LumenStack ingredient, Lumen lumen) {
		ResourceLocation key = this.registry.getKey(lumen);
		if (key == null) {
			String ingredientInfo = getErrorInfo(ingredient);
			throw new IllegalStateException("null registry name for: " + ingredientInfo);
		}
		return key;
	}

    @Override
    public LumenStack copyIngredient(LumenStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public LumenStack copyWithAmount(LumenStack ingredient, long amount) {
        return ingredient.copyWithAmount(Math.toIntExact(amount));
    }

    @Override
    public long getAmount(LumenStack ingredient) {
        return ingredient.getAmount();
    }

    @Override
    public Iterable<Integer> getColors(LumenStack ingredient) {
        return Collections.singleton(ingredient.getLumen().getColor(0L).getColor());
    }

    @Override
    public LumenStack normalizeIngredient(LumenStack ingredient) {
        return ingredient.copyWithAmount(LumenStack.FLASK_VALUE);
    }

    @Override
    public Stream<ResourceLocation> getTagStream(LumenStack ingredient) {
        return this.registry.getResourceKey(ingredient.getLumen())
                .flatMap(this.registry::getHolder)
                .map(Holder.Reference::tags)
                .orElse(Stream.of())
                .map(TagKey::location);
    }

    @Override
    public String getErrorInfo(@Nullable LumenStack ingredient) {
        if (ingredient == null) {
            return "null";
        }

        MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(ingredient.getClass());
        if (ingredient.isEmpty()) {
            toStringHelper.add("Lumen", "null");
        } else {
            toStringHelper.add("Lumen", ingredient.getLumen().getName().getString());
        }
        toStringHelper.add("Amount", ingredient.getAmount());
        return toStringHelper.toString();
    }
}
