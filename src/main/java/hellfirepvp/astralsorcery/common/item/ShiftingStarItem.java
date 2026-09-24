/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShiftingStarItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShiftingStarItem extends ItemCustom {

    private final Supplier<? extends BaseConstellation> cstSupplier;

    public ShiftingStarItem() {
        this(() -> null);
    }

    public ShiftingStarItem(Supplier<? extends BaseConstellation> cstSupplier) {
        super(new Properties()
                .stacksTo(1));
        this.cstSupplier = cstSupplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        BaseConstellation cst = cstSupplier.get();
        if (cst != null && context.level() != null) {
            LogicalSide side = SidedHelper.getSide(context.level());
            if (side.isClient()) {
                PlayerProgress progress = ResearchManager.getClientProgress();
                if (progress.hasDiscoveredConstellation(cst)) {
                    tooltipComponents.add(cst.getColoredName());
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        BaseConstellation cst = this.cstSupplier.get();
        if (cst != null) {
            PlayerProgress progress = ResearchManager.getProgress(player, SidedHelper.getSide(player));
            if (!progress.isValid() || !progress.wasOnceAttuned() || !progress.hasDiscoveredConstellation(cst)) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand));
            }
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer sPlayer && level instanceof ServerLevel sLevel) {
            BaseConstellation cst = this.cstSupplier.get();
            if (cst != null) {
                PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
                if (!progress.isValid() || !progress.wasOnceAttuned() || !progress.hasDiscoveredConstellation(cst)) {
                    return stack;
                }

                double exp = progress.getPerkData().getPerkExp();
                if (ResearchHelper.attuneConstellation(sPlayer, cst)) {
                    ResearchHelper.setPerkExp(sPlayer, exp);
                    ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, sLevel, new Vector3(sPlayer), 1F, 1F);
                    if (!sPlayer.isCreative()) stack.shrink(1);
                    return stack;
                }
            } else if (ResearchHelper.removeAttunedConstellation(sPlayer)) {
                ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, sLevel, new Vector3(sPlayer), 1F, 1F);
                if (!sPlayer.isCreative()) stack.shrink(1);
                return stack;
            }
        }
        return stack;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) {
            int totalDuration = this.getUseDuration(stack, livingEntity);
            this.playUseEffects(livingEntity, totalDuration - remainingUseDuration, totalDuration);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playUseEffects(LivingEntity livingEntity, int tick, int totalTick) {
        BaseConstellation cst = this.cstSupplier.get();
        if (cst == null) {


            return;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.cstSupplier.get() == null ? 60 : 100;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
}
