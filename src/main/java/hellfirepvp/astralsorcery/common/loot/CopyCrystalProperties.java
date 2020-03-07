/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeItem;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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
            if (tile instanceof CrystalAttributeTile && stack.getItem() instanceof CrystalAttributeItem) {
                CrystalAttributes attr = ((CrystalAttributeTile) tile).getAttributes();
                if (attr != null) {
                    ((CrystalAttributeItem) stack.getItem()).setAttributes(stack, attr);
                }
            }
        }
        return stack;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(CopyCrystalProperties::new);
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
