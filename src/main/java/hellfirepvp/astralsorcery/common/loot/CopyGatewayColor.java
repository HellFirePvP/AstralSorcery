/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialGateway;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CopyGatewayColor
 * Created by HellFirePvP
 * Date: 12.09.2020 / 21:35
 */
public class CopyGatewayColor extends LootFunction {

    private CopyGatewayColor(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return Sets.newHashSet(LootParameters.BLOCK_ENTITY);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof TileCelestialGateway) {
            DyeColor color = ((TileCelestialGateway) tile).getColor();
            if (color != null) {
                BlockCelestialGateway.setColor(stack, color);
            }
        }
        return stack;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(CopyGatewayColor::new);
    }

    public static class Serializer extends LootFunction.Serializer<CopyGatewayColor> {

        public Serializer(ResourceLocation name) {
            super(name, CopyGatewayColor.class);
        }

        @Override
        public CopyGatewayColor deserialize(JsonObject jsonObject, JsonDeserializationContext ctx, ILootCondition[] conditions) {
            return new CopyGatewayColor(conditions);
        }
    }
}
