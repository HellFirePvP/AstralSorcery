/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.PerkExperienceRevealer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemShiftingStar
 * Created by HellFirePvP
 * Date: 22.02.2020 / 21:39
 */
public class ItemShiftingStar extends Item implements PerkExperienceRevealer {

    public ItemShiftingStar() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IConstellation cst = this.getBaseConstellation();
        if (cst != null) {
            if (ResearchHelper.getClientProgress().hasConstellationDiscovered(cst)) {
                tooltip.add(cst.getConstellationName().applyTextStyle(TextFormatting.BLUE));
            } else {
                tooltip.add(new TranslationTextComponent("astralsorcery.misc.noinformation").applyTextStyle(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isRemote() && entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
            IMajorConstellation cst = this.getBaseConstellation();
            if (cst != null) {
                PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
                if (!prog.isValid() || !prog.wasOnceAttuned() || !prog.hasConstellationDiscovered(cst)) {
                    return stack;
                }

                double perkExp = prog.getPerkExp();
                if (ResearchManager.setAttunedConstellation(player, cst)) {
                    ResearchManager.setExp(player, MathHelper.lfloor(perkExp));
                    player.sendMessage(new TranslationTextComponent("astralsorcery.progress.switch.attunement").setStyle(new Style().setColor(TextFormatting.BLUE)));
                    SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, worldIn, entityLiving.getPosition(), 1F, 1F);

                }
            } else if (ResearchManager.setAttunedConstellation(player, null)) {
                player.sendMessage(new TranslationTextComponent("astralsorcery.progress.remove.attunement").setStyle(new Style().setColor(TextFormatting.BLUE)));
                SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, worldIn, entityLiving.getPosition(), 1F, 1F);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.getEntityWorld().isRemote()) {
            this.playUseEffects(player, getUseDuration(stack) - count, getUseDuration(stack));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playUseEffects(LivingEntity player, int tick, int total) {
        IMajorConstellation cst = this.getBaseConstellation();
        if (cst == null) {
            FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(player).addY(player.getHeight() / 2))
                    .setMotion(new Vector3(-0.1 + random.nextFloat() * 0.2, 0.01, -0.1 + random.nextFloat() * 0.2))
                    .setScaleMultiplier(0.2F + random.nextFloat());
            if (random.nextBoolean()) {
                p.color(VFXColorFunction.WHITE);
            }
        } else {
            float percCycle = (float) ((((float) (tick % total)) / ((float) total)) * 2 * Math.PI);
            int parts = 5;
            for (int i = 0; i < parts; i++) {
                float angleSwirl = 75F;
                Vector3 center = Vector3.atEntityCorner(player).addY(player.getHeight() / 2);
                Vector3 v = Vector3.RotAxis.X_AXIS.clone();
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(4);
                Vector3 pos = center.clone().add(v);
                Vector3 mot = center.clone().subtract(pos).normalize().multiply(0.1);

                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setScaleMultiplier(0.25F + random.nextFloat() * 0.4F)
                        .setMotion(mot)
                        .setMaxAge(50);
                if (random.nextInt(4) == 0) {
                    p.color(VFXColorFunction.WHITE);
                } else if (random.nextInt(3) == 0) {
                    p.color(VFXColorFunction.constant(cst.getConstellationColor().brighter()));
                } else {
                    p.color(VFXColorFunction.constant(cst.getConstellationColor()));
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return this.getBaseConstellation() == null ? 60 : 100;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Nullable
    public IMajorConstellation getBaseConstellation() {
        return null;
    }
}
