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
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;

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
    public LootFunctionType getFunctionType() {
        return LootAS.Functions.COPY_GATEWAY_COLOR;
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof TileCelestialGateway) {
            ((TileCelestialGateway) tile).getColor().ifPresent(color -> {
                BlockCelestialGateway.setColor(stack, color);
            });
        }
        return stack;
    }

    public static LootFunction.Builder<?> builder() {
        return builder(CopyGatewayColor::new);
    }

    public static class Serializer extends LootFunction.Serializer<CopyGatewayColor> {

        @Override
        public CopyGatewayColor deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditions) {
            return new CopyGatewayColor(conditions);
        }
    }
}
