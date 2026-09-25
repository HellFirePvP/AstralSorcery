/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.ingredient;

import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenIngredientType
 * Created by HellFirePvP
 * Date: 25.09.2026 / 19:32
 */
public class LumenIngredientType implements IIngredientTypeWithSubtypes<Lumen, LumenStack> {

    public static final LumenIngredientType INSTANCE = new LumenIngredientType();

    private LumenIngredientType() {}

    @Override
    public Class<? extends LumenStack> getIngredientClass() {
        return LumenStack.class;
    }

    @Override
    public Class<? extends Lumen> getIngredientBaseClass() {
        return Lumen.class;
    }

    @Override
    public Lumen getBase(LumenStack ingredient) {
        return ingredient.getLumen();
    }

    @Override
    public LumenStack getDefaultIngredient(Lumen base) {
        return LumenStack.of(base, LumenStack.FLASK_VALUE);
    }
}
