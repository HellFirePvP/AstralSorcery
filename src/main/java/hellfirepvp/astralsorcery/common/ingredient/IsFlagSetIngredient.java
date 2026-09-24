/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.FlagsComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IsFlagSetIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record IsFlagSetIngredient(FlagsComponent.Flag flag, FlagState state) implements ICustomIngredient {

    public static final MapCodec<IsFlagSetIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FlagsComponent.Flag.CODEC.fieldOf("flag").forGetter(IsFlagSetIngredient::flag),
            StringRepresentable.fromEnum(FlagState::values).fieldOf("state").forGetter(IsFlagSetIngredient::state)
    ).apply(inst, IsFlagSetIngredient::new));

    public static IsFlagSetIngredient of(FlagsComponent.Flag flag, boolean set) {
        return new IsFlagSetIngredient(flag, set ? FlagState.IS_SET : FlagState.IS_NOT_SET);
    }

    @Override
    public boolean test(ItemStack stack) {
        FlagsComponent cmp = stack.getOrDefault(DataComponentsAS.FLAGS, FlagsComponent.EMPTY);
        return switch (this.state) {
            case IS_SET -> cmp.isSet(this.flag);
            case IS_NOT_SET -> !cmp.isSet(this.flag);
        };
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return IngredientsAS.IS_FLAG_SET.get();
    }

    public enum FlagState implements StringRepresentable {

        IS_SET,
        IS_NOT_SET;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
