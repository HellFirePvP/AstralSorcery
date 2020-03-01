/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlinkWand
 * Created by HellFirePvP
 * Date: 01.03.2020 / 08:41
 */
public class ItemBlinkWand extends Item {

    public ItemBlinkWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getBlinkMode(stack).getDisplay().setStyle(new Style().setColor(TextFormatting.GOLD)));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            BlinkMode nextMode = getBlinkMode(held).next();
            setBlinkMode(held, nextMode);
            playerIn.sendStatusMessage(nextMode.getDisplay(), true);
        } else {
            playerIn.setActiveHand(handIn);
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, held);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72_000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (worldIn.isRemote() || !(entityLiving instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;

        BlinkMode mode = getBlinkMode(stack);
        if (mode == BlinkMode.TELEPORT) {
            Vector3 origin = Vector3.atEntityCorner(player).addY(0.5F);
            Vector3 look = new Vector3(player.getLook(1F)).normalize().multiply(40F).add(origin);
            List<BlockPos> blockLine = new ArrayList<>();
            RaytraceAssist rta = new RaytraceAssist(origin, look);
            rta.forEachBlockPos(pos -> {
                return MiscUtils.executeWithChunk(player.getEntityWorld(), pos, () -> {
                    if (player.getEntityWorld().isAirBlock(pos) && player.getEntityWorld().isAirBlock(pos.up())) {
                        blockLine.add(pos);
                        return true;
                    }
                    return false;
                }, false);
            });

            if (!blockLine.isEmpty()) {
                BlockPos at = Iterables.getLast(blockLine);
                if (origin.distance(at) > 5) {
                    player.setPositionAndUpdate(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
                    player.getCooldownTracker().setCooldown(stack.getItem(), 40);

                    //TODO particles..?
                }
            }
        } else if (mode == BlinkMode.LAUNCH) {
            float strength = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration() - timeLeft) / 50F) * 0.8F;

            //TODO launch player & rework packet
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        float perc = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration() - count) / 50F) * 0.8F;
        playUseParticles(stack, player, count, perc);
    }

    @OnlyIn(Dist.CLIENT)
    private void playUseParticles(ItemStack stack, LivingEntity player, int useTicks, float usagePercent) {
        if (getBlinkMode(stack) == BlinkMode.LAUNCH) {
            Vector3 look = new Vector3(player.getLook(1F)).normalize().multiply(20);
            Vector3 pos = Vector3.atEntityCorner(player).addY(player.getEyeHeight());
            Vector3 motion = look.clone().normalize().multiply(-0.8F + random.nextFloat() * -0.5F);
            Vector3 perp = look.clone().perpendicular().normalize();

            for (int i = 0; i < Math.round(usagePercent * 6); i++) {
                float dst = i == 0 ? random.nextFloat() * 0.4F : 0.2F + random.nextFloat() * 0.4F;
                float speed = i == 0 ? 0.005F : 0.5F + random.nextFloat() * 0.5F;
                float angleDeg = random.nextFloat() * 360F;

                Vector3 angle = perp.clone().rotate(angleDeg, look).normalize();
                Vector3 at = pos.clone()
                        .add(look.clone().multiply(0.7F + random.nextFloat() * 0.3F))
                        .add(angle.clone().multiply(dst));
                Vector3 mot = motion.clone().add(angle.clone().multiply(0.1F + random.nextFloat() * 0.15F)).multiply(speed);

                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .setOwner(player.getUniqueID())
                        .spawn(at)
                        .setScaleMultiplier(0.3F + random.nextFloat() * 0.3F)
                        .setAlphaMultiplier(usagePercent)
                        .setMotion(mot)
                        .color(VFXColorFunction.constant(ColorsAS.CONSTELLATION_VICIO))
                        .setMaxAge(20 + random.nextInt(15));
                if (random.nextBoolean()) {
                    p.color(VFXColorFunction.WHITE);
                }
            }
        } else if (getBlinkMode(stack) == BlinkMode.TELEPORT) {
            Vector3 origin = Vector3.atEntityCorner(player).addY(0.5F);
            Vector3 look = new Vector3(player.getLook(1F)).normalize().multiply(40F).add(origin);
            List<Vector3> line = new ArrayList<>();
            RaytraceAssist rta = new RaytraceAssist(origin, look);
            boolean clearLine = rta.forEachStep(v -> {
                BlockPos pos = v.toBlockPos();
                return MiscUtils.executeWithChunk(player.getEntityWorld(), pos, () -> {
                    if (player.getEntityWorld().isAirBlock(pos) && player.getEntityWorld().isAirBlock(pos.up())) {
                        line.add(v);
                        return true;
                    }
                    return false;
                }, false);
            });

            if (!line.isEmpty()) {
                Vector3 last = Iterables.getLast(line);

                for (Vector3 v : line) {
                    if (v == last || random.nextInt(300) == 0) {
                        VFXColorFunction<?> colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_VICIO);
                        float scale = 0.4F + random.nextFloat() * 0.2F;
                        float speed = random.nextFloat() * 0.02F;
                        int age = 20 + random.nextInt(15);
                        if (random.nextInt(3) == 0) {
                            colorFn = VFXColorFunction.WHITE;
                        }
                        if (v == last) {
                            scale *= 1.5F;
                            speed *= 4;
                            age *= 0.7F;
                            if (!clearLine) {
                                colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_EVORSIO);
                            } else {
                                colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_AEVITAS);
                            }
                            if (random.nextInt(5) == 0) {
                                colorFn = VFXColorFunction.WHITE;
                            }
                        }

                        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                .setOwner(player.getUniqueID())
                                .spawn(v)
                                .setScaleMultiplier(scale)
                                .setAlphaMultiplier(usagePercent)
                                .alpha(VFXAlphaFunction.FADE_OUT)
                                .setMotion(Vector3.random().normalize().multiply(speed))
                                .color(colorFn)
                                .setMaxAge(age);
                    }
                }
            }
        }
    }

    public static void setBlinkMode(@Nonnull ItemStack stack, @Nonnull BlinkMode mode) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlinkWand)) {
            return;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        nbt.putInt("blinkMode", mode.ordinal());
    }

    @Nonnull
    public static BlinkMode getBlinkMode(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemBlinkWand)) {
            return BlinkMode.LAUNCH;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        return MiscUtils.getEnumEntry(BlinkMode.class, nbt.getInt("blinkMode"));
    }

    public static enum BlinkMode {

        LAUNCH("launch"),
        TELEPORT("teleport");

        private final String name;

        BlinkMode(String name) {
            this.name = name;
        }

        public ITextComponent getName() {
            return new TranslationTextComponent("astralsorcery.misc.blink.mode." + this.name);
        }

        public ITextComponent getDisplay() {
            return new TranslationTextComponent("astralsorcery.misc.blink.mode", this.getName());
        }

        @Nonnull
        private BlinkMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtils.getEnumEntry(BlinkMode.class, next);
        }
    }
}
