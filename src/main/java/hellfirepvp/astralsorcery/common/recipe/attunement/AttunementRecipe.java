/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.attunement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttunementRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AttunementRecipe<T extends AttunementRecipe.Active<?, ?>> {

    private static final Map<ResourceLocation, AttunementRecipe<?>> RECIPES = new HashMap<>();
    public static final Codec<AttunementRecipe<?>> CODEC = ResourceLocation.CODEC.xmap(RECIPES::get, AttunementRecipe::getIdentifier);
    public static final Codec<AttunementRecipe.Active<?, ?>> ACTIVE_CODEC = CODEC.dispatch(Active::getRecipe, AttunementRecipe::codec);

    private final ResourceLocation identifier;

    protected AttunementRecipe(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public static void initRecipes() {
        RECIPES.clear();

        RECIPES.put(PlayerAttunementRecipe.INSTANCE.getIdentifier(), PlayerAttunementRecipe.INSTANCE);
        RECIPES.put(ItemAttunementRecipe.INSTANCE.getIdentifier(), ItemAttunementRecipe.INSTANCE);
    }

    public final ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public static Collection<AttunementRecipe<?>> getAllRecipes() {
        return RECIPES.values();
    }

    public abstract boolean canStartCrafting(TileAttunementAltar altar);

    public abstract T createActiveRecipe(TileAttunementAltar altar);

    public abstract MapCodec<T> codec();

    public static abstract class Active<T extends AttunementRecipe<A>, A extends Active<T, A>> {

        protected final RandomSource rand = RandomSource.create();

        private final T recipe;
        private final BaseConstellation constellation;
        private int tick = 0;

        protected Active(T recipe, BaseConstellation constellation) {
            this.recipe = recipe;
            this.constellation = constellation;
        }

        protected Active(T recipe, int tick, BaseConstellation constellation) {
            this(recipe, constellation);
            this.tick = tick;
        }

        public final T getRecipe() {
            return this.recipe;
        }

        public final BaseConstellation getConstellation() {
            return this.constellation;
        }

        protected int getTick() {
            return this.tick;
        }

        public final void tick(LogicalSide side, TileAttunementAltar altar) {
            this.doTick(side, altar);
            this.tick++;
        }

        //Called every tick on server to test if this recipe can be continued.
        public boolean matches(TileAttunementAltar altar) {
            return this.getRecipe().canStartCrafting(altar);
        }

        //Called on server when this recipe starts
        public abstract void startCrafting(TileAttunementAltar altar);

        //Called on server when this recipe should stop (stop effects, world interactions, ...)
        public abstract void stopCrafting(TileAttunementAltar altar);

        //Called on server when this recipe should create rewards
        public abstract void finishRecipe(TileAttunementAltar altar);

        //Called every tick for both sides
        public abstract void doTick(LogicalSide side, TileAttunementAltar altar);

        //Called every tick on server to test if this recipe is done. Create 'reward' and return true when finished.
        public abstract boolean isFinished(TileAttunementAltar altar);

        //Called on client to stop effects and such
        @OnlyIn(Dist.CLIENT)
        public abstract void stopEffects(TileAttunementAltar altar);

        public void copyEffectDataTo(A other) {
        }
    }
}
