package hellfirepvp.astralsorcery.common.crafting.nojson.attunement;

import hellfirepvp.astralsorcery.common.crafting.nojson.CustomRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementRecipe
 * Created by HellFirePvP
 * Date: 18.11.2019 / 19:02
 */
public abstract class AttunementRecipe<T extends AttunementRecipe.Active> extends CustomRecipe {

    public AttunementRecipe(ResourceLocation key) {
        super(key);
    }

    public abstract boolean canStartCrafting(TileAttunementAltar altar);

    @Nonnull
    public abstract T createRecipe(TileAttunementAltar altar);

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public T deserialize(TileAttunementAltar altar, CompoundNBT nbt, @Nullable T previousInstance) {
        T activeRecipe = this.createRecipe(altar);
        activeRecipe.readFromNBT(nbt);
        return activeRecipe;
    }

    public static abstract class Active<T extends AttunementRecipe<? extends Active<T>>> {

        protected final Random rand = new Random();

        private T recipe;
        private int tick = 0;

        public Active(T recipe) {
            this.recipe = recipe;
        }

        public final T getRecipe() {
            return recipe;
        }

        protected int getTick() {
            return tick;
        }

        public final void tick(LogicalSide side, TileAttunementAltar altar) {
            this.doTick(side, altar);
            this.tick++;
        }

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

        //Called every tick on server to test if this recipe can be continued.
        public boolean matches(TileAttunementAltar altar) {
            return this.recipe.canStartCrafting(altar);
        }

        public void writeToNBT(CompoundNBT nbt) {
            nbt.putInt("tick", this.tick);
        }

        protected void readFromNBT(CompoundNBT nbt) {
            this.tick = nbt.getInt("tick");
        }

    }

}
