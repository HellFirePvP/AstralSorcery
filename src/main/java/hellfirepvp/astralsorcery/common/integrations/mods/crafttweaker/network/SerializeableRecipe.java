/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import io.netty.buffer.ByteBuf;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SerializeableRecipe
 * Created by HellFirePvP
 * Date: 27.02.2017 / 01:56
 */
public interface SerializeableRecipe {

    public CraftingType getType();

    public void read(ByteBuf buf);

    public void write(ByteBuf buf);

    public void applyRecipe();

    public static enum CraftingType {

        INFUSION_ADD     ((v) -> new InfusionRecipeAdd()),
        INFUSION_REMOVE  ((v) -> new InfusionRecipeRemove()),
        TRANSMUTE_ADD    ((v) -> new LightTransmutationAdd()),
        TRANSMUTE_REMOVE ((v) -> new LightTransmutationRemove()),
        WELL_ADD         ((v) -> new WellRecipeAdd()),
        WELL_REMOVE      ((v) -> new WellRecipeRemove()),
        GRINDSTONE_ADD   ((v) -> new GrindstoneRecipeAdd()),
        GRINDSTONE_REMOVE((v) -> new GrindstoneRecipeRemove()),
        ALTAR_REMOVE     ((v) -> new AltarRecipeRemove()),
        ALTAR_T1_ADD     ((v) -> new AltarRecipeDiscovery()),
        ALTAR_T2_ADD     ((v) -> new AltarRecipeAttunement()),
        ALTAR_T3_ADD     ((v) -> new AltarRecipeConstellation()),
        ALTAR_T4_ADD     ((v) -> new AltarRecipeTrait());

        private final Function<Void, ? extends SerializeableRecipe> recipeProvider;

        CraftingType(Function<Void, ? extends SerializeableRecipe> recipeProvider) {
            this.recipeProvider = recipeProvider;
        }

        public SerializeableRecipe newInstance() {
            return recipeProvider.apply(null);
        }

    }

}
