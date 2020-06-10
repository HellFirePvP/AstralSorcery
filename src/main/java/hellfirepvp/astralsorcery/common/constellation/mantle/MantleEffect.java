/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.EnumSet;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffect
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:13
 */
public abstract class MantleEffect extends ForgeRegistryEntry<MantleEffect> implements ITickHandler {

    protected static final Random rand = new Random();

    private final IWeakConstellation constellation;

    public MantleEffect(IWeakConstellation constellation) {
        this.constellation = constellation;
        this.setRegistryName(this.constellation.getRegistryName());

        this.attachEventListeners(MinecraftForge.EVENT_BUS);
        this.attachTickHandlers(AstralSorcery.getProxy().getTickManager()::register);
    }

    public final IWeakConstellation getAssociatedConstellation() {
        return this.constellation;
    }

    public abstract Config getConfig();

    protected void attachEventListeners(IEventBus bus) {}

    protected void attachTickHandlers(Consumer<ITickHandler> registrar) {
        if (this.usesTickMethods()) {
            registrar.accept(this);
        }
    }

    protected void tickServer(PlayerEntity player) {}

    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {}

    protected boolean usesTickMethods() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected void playCapeSparkles(PlayerEntity player, float chance) {
        if (player == Minecraft.getInstance().player && Minecraft.getInstance().gameSettings.thirdPersonView == 0) {
            chance *= 0.1F;
        }
        if (rand.nextFloat() < chance) {
            Color c = this.getAssociatedConstellation().getConstellationColor();
            if (c != null) {
                float width = player.getWidth() * 0.8F;
                double x = player.getPosX() + rand.nextFloat() * width * (rand.nextBoolean() ? 1 : -1);
                double y = player.getPosY() + rand.nextFloat() * (player.getHeight() / 3);
                double z = player.getPosZ() + rand.nextFloat() * width * (rand.nextBoolean() ? 1 : -1);
                Vector3 pos = new Vector3(x, y, z);

                FXFacingParticle fx = this.spawnFacingParticle(player, pos)
                        .color(VFXColorFunction.constant(c))
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setScaleMultiplier(0.4F + rand.nextFloat() * 0.4F)
                        .setMaxAge(20 + rand.nextInt(10));
                if (rand.nextInt(3) == 0) {
                    fx.color(VFXColorFunction.constant(this.getAssociatedConstellation().getTierRenderColor()));
                }

                if (rand.nextFloat() > 0.35F) {
                    this.spawnFacingParticle(player, pos)
                            .color(VFXColorFunction.WHITE)
                            .alpha(VFXAlphaFunction.FADE_OUT)
                            .setScaleMultiplier(0.2F + rand.nextFloat() * 0.2F)
                            .setMaxAge(10 + rand.nextInt(10));
                }
            }
        }
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    protected FXFacingParticle spawnFacingParticle(PlayerEntity player, Vector3 at) {
        return EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .setOwner(player.getUniqueID())
                .spawn(at);
    }

    @Override
    public final void tick(TickEvent.Type type, Object... context) {
        if (!this.getConfig().enabled.get()) {
            return;
        }

        PlayerEntity pl = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];
        boolean hasMantle = ItemMantle.getEffect(pl, this.getAssociatedConstellation()) != null;
        if (!hasMantle) {
            return;
        }

        if (side.isServer()) {
            if (!(pl instanceof ServerPlayerEntity) || MiscUtils.isPlayerFakeMP((ServerPlayerEntity) pl)) {
                return;
            }
            this.tickServer(pl);
        } else {
            this.tickClient(pl);
        }
    }

    @Nonnull
    protected CompoundNBT getData(LivingEntity entity) {
        if (entity == null) {
            return new CompoundNBT();
        }
        ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMantle)) {
            return new CompoundNBT();
        }
        return NBTHelper.getPersistentData(stack);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public static class Config extends ConfigEntry {

        private final boolean defaultEnabled = true;

        public ForgeConfigSpec.BooleanValue enabled;

        public Config(String constellationName) {
            super(String.format("constellation.mantle.%s", constellationName));
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.enabled = cfgBuilder
                    .comment("Set this to false to disable this mantle effect")
                    .translation(translationKey("enabled"))
                    .define("enabled", this.defaultEnabled);
        }
    }
}
