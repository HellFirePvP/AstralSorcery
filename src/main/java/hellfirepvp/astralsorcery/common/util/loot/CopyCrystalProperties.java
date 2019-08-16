/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalPropertyItem;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalPropertyTile;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CopyCrystalProperties
 * Created by HellFirePvP
 * Date: 16.08.2019 / 06:18
 */
public class CopyCrystalProperties extends LootFunction {

    private CopyCrystalProperties(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        if (context.has(LootParameters.BLOCK_ENTITY)) {
            TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
            if (tile instanceof CrystalPropertyTile && stack.getItem() instanceof CrystalPropertyItem) {
                CrystalProperties prop = ((CrystalPropertyTile) tile).getCrystalProperties();
                if (prop != null) {
                    ((CrystalPropertyItem) stack.getItem()).applyCrystalProperties(stack, prop);
                }
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<CopyCrystalProperties> {

        public Serializer(ResourceLocation name) {
            super(name, CopyCrystalProperties.class);
        }

        @Override
        public CopyCrystalProperties deserialize(JsonObject jsonObject, JsonDeserializationContext ctx, ILootCondition[] conditions) {
            return new CopyCrystalProperties(conditions);
        }
    }
}
