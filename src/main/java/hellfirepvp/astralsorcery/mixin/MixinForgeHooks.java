/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyMagnetDrops;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.loot.LootUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinForgeHooks
 * Created by HellFirePvP
 * Date: 01.01.2022 / 10:06
 */
@Mixin(ForgeHooks.class)
public class MixinForgeHooks {

    @Inject(
            method = "modifyLoot(Lnet/minecraft/util/ResourceLocation;Ljava/util/List;Lnet/minecraft/loot/LootContext;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private static void runLootTeleportation(ResourceLocation lootTableId, List<ItemStack> generatedLoot, LootContext context, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> loot = cir.getReturnValue();

        if (!LootUtil.doesContextFulfillSet(context, LootParameterSets.BLOCK)) {
            return;
        }
        Entity e = context.get(LootParameters.THIS_ENTITY);
        if (!(e instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) e;
        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.isValid() || !prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyMagnetDrops)) {
            return;
        }

        //Means we're in the 2nd run of loot manipulation, re-run by top.theillusivec4.curios.common.objects.FortuneBonusMultiplier
        ItemStack tool = context.get(LootParameters.TOOL);
        if (tool != null && tool.hasTag() && tool.getTag().contains("HasCuriosFortuneBonus")) {
            loot.removeIf(result -> ItemUtils.dropItemToPlayer(player, result).isEmpty());
        }
        int curiosFortuneBonus = CuriosApi.getCuriosHelper().getCuriosHandler(player)
                .map(ICuriosItemHandler::getFortuneBonus)
                .orElse(0);
        if (curiosFortuneBonus > 0) {
            return; //Do not modify loot, loot modification gets re-run by curios later
        }
        loot.removeIf(result -> ItemUtils.dropItemToPlayer(player, result).isEmpty());
    }
}
