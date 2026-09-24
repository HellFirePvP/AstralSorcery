/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.base.TileDataLumenContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.observerlib.common.util.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CopyLumenFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CopyLumenFunction extends LootItemConditionalFunction {

    public static final MapCodec<CopyLumenFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).apply(inst, CopyLumenFunction::new));

    protected CopyLumenFunction(List<LootItemCondition> predicates) {
        super(predicates);
    }

    public static LootItemConditionalFunction.Builder<?> copyLumen() {
        return simpleBuilder(CopyLumenFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TileEntitySynchronized<?> tile) {
                if (tile.getTileData() instanceof TileDataLumenContainer lumenContainer) {
                    Holder<Lumen> lumenRef = RegistryHelper.of(RegistriesAS.REGISTRY_LUMEN).getOrNull(lumenContainer.getLumen());
                    if (lumenRef != null && !lumenRef.is(LumenAS.NONE.getKey())) {
                        stack.set(DataComponentsAS.LUMEN, new LumenComponent(lumenRef));
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return LootAS.COPY_LUMEN_FUNCTION.get();
    }
}
