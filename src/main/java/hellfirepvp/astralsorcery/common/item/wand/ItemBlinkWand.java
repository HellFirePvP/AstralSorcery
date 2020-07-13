/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktShootEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

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
public class ItemBlinkWand extends Item implements AlignmentChargeConsumer {

    private static final float COST_PER_BLINK = 700F;
    private static final float COST_PER_DASH = 850F;

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
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        if (player.getCooldownTracker().hasCooldown(this)) {
            return 0F;
        }
        if (getBlinkMode(stack) == BlinkMode.TELEPORT) {
            return COST_PER_BLINK;
        } else if (player.isHandActive()) {
            ItemStack held = player.getActiveItemStack();
            if (!held.isEmpty() && held.getItem() instanceof ItemBlinkWand) {
                int timeLeft = player.getItemInUseCount();
                float strength = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration() - timeLeft) / 50F) * 0.8F;
                return COST_PER_DASH * strength;
            }
        }
        return 0F;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (player.isSneaking()) {
            BlinkMode nextMode = getBlinkMode(held).next();
            setBlinkMode(held, nextMode);
            player.sendStatusMessage(nextMode.getDisplay(), true);
        } else if (!player.getCooldownTracker().hasCooldown(this)) {
            player.setActiveHand(hand);
        }
        return ActionResult.resultConsume(held);
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
                    if (BlockUtils.isReplaceable(player.getEntityWorld(), pos) && BlockUtils.isReplaceable(player.getEntityWorld(), pos.up())) {
                        blockLine.add(pos);
                        return true;
                    }
                    return false;
                }, false);
            });

            if (!blockLine.isEmpty()) {
                BlockPos at = Iterables.getLast(blockLine);
                if (origin.distance(at) > 5) {
                    if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_BLINK, false)) {
                        player.setPositionAndUpdate(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
                        if (!player.isCreative()) {
                            player.getCooldownTracker().setCooldown(stack.getItem(), 40);
                        }
                    }
                }
            }
        } else if (mode == BlinkMode.LAUNCH) {
            float strength = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration() - timeLeft) / 50F) * 0.8F;
            if (strength > 0.3F) {
                float chargeCost = COST_PER_DASH * strength;
                if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, chargeCost, false)) {
                    Vector3 motion = new Vector3(player.getLook(1F)).normalize().multiply(strength * 3F);
                    if (motion.getY() > 0) {
                        motion.setY(MathHelper.clamp(motion.getY() + (0.7F * strength), 0.7F * strength, Float.MAX_VALUE));
                    }

                    player.setMotion(motion.toVec3d());
                    player.fallDistance = 0F;

                    if (ItemMantle.getEffect(player, ConstellationsAS.vicio) != null) {
                        AstralSorcery.getProxy().scheduleClientside(player::startFallFlying, 2);
                    }

                    PktShootEntity pkt = new PktShootEntity(player.getEntityId(), motion);
                    pkt.setEffectLength(strength);
                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(worldIn, player.getPosition(), 64));
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if (entity.getEntityWorld().isRemote()) {
            float perc = 0.2F + Math.min(1F, Math.min(50, stack.getUseDuration() - count) / 50F) * 0.8F;
            playUseParticles(stack, entity, count, perc);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playUseParticles(ItemStack stack, LivingEntity entity, int useTicks, float usagePercent) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        if (player.getCooldownTracker().hasCooldown(this)) {
            return;
        }
        if (getBlinkMode(stack) == BlinkMode.LAUNCH) {
            Vector3 look = new Vector3(entity.getLook(1F)).normalize().multiply(20);
            Vector3 pos = Vector3.atEntityCorner(entity).addY(entity.getEyeHeight());
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
                        .setOwner(entity.getUniqueID())
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
            Vector3 origin = Vector3.atEntityCorner(entity).addY(0.5F);
            Vector3 look = new Vector3(entity.getLook(1F)).normalize().multiply(40F).add(origin);
            List<Vector3> line = new ArrayList<>();
            RaytraceAssist rta = new RaytraceAssist(origin, look);
            boolean clearLine = rta.forEachStep(v -> {
                BlockPos pos = v.toBlockPos();
                return MiscUtils.executeWithChunk(entity.getEntityWorld(), pos, () -> {
                    if (BlockUtils.isReplaceable(entity.getEntityWorld(), pos) && BlockUtils.isReplaceable(entity.getEntityWorld(), pos.up())) {
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
                                colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_AEVITAS);
                            } else {
                                colorFn = VFXColorFunction.constant(ColorsAS.CONSTELLATION_EVORSIO);
                            }
                            if (random.nextInt(5) == 0) {
                                colorFn = VFXColorFunction.WHITE;
                            }
                        }

                        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                .setOwner(entity.getUniqueID())
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
