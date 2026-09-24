/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultDropItem;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultSpawnEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionResultTypesAS
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionResultTypesAS {

    public static final DeferredRegister<LiquidInteractionResult.Type<?>> RESULT_TYPE_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_LIQUID_INTERACTION_RESULT_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<LiquidInteractionResult.Type<?>, LiquidInteractionResult.Type<LiquidInteractionResultDropItem>> DROP_ITEM =
            RESULT_TYPE_REGISTER.register("drop_item", () -> LiquidInteractionResultDropItem.TYPE);

    public static final DeferredHolder<LiquidInteractionResult.Type<?>, LiquidInteractionResult.Type<LiquidInteractionResultSpawnEntity>> SPAWN_ENTITY =
            RESULT_TYPE_REGISTER.register("spawn_entity", () -> LiquidInteractionResultSpawnEntity.TYPE);
}
