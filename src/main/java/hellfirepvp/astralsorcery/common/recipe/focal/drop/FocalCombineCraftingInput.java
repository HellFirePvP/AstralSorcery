/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.focal.drop;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalCombineCraftingInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalCombineCraftingInput extends CustomRecipeInput {

    private final BaseConstellation constellation;
    private final Level level;
    private final List<ItemEntity> inputs;
    private final Vec3 center;

    public FocalCombineCraftingInput(BaseConstellation constellation, Level level, List<ItemEntity> inputs) {
        this.constellation = constellation;
        this.level = level;
        this.inputs = inputs;
        this.center = this.calculateCenter(inputs);
    }

    public BaseConstellation getConstellation() {
        return this.constellation;
    }

    private Vec3 calculateCenter(List<ItemEntity> inputs) {
        if (inputs.isEmpty()) {
            return Vec3.ZERO;
        }
        double x = 0, y = 0, z = 0;
        for (ItemEntity item : inputs) {
            x += item.getX();
            y += item.getY();
            z += item.getZ();
        }
        return new Vec3(x / inputs.size(), y / inputs.size(), z / inputs.size());
    }

    public Level getLevel() {
        return this.level;
    }

    public Vec3 getCenter() {
        return this.center;
    }

    public List<ItemEntity> getInputs() {
        return Collections.unmodifiableList(this.inputs);
    }

    public Map<ItemEntity, ItemStack> getAssociatedInputs() {
        return this.inputs.stream()
                .collect(Collectors.toMap(itemEntity -> itemEntity, itemEntity -> itemEntity.getItem().copy()));
    }

    @Override
    public boolean isEmpty() {
        return this.inputs.isEmpty();
    }
}
