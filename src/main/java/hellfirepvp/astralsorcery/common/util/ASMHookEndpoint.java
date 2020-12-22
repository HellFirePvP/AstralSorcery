/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.CooldownSetEvent;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyEntityReach;
import hellfirepvp.astralsorcery.common.util.collision.CollisionHelper;
import hellfirepvp.astralsorcery.common.util.reflection.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASMHookEndpoint
 * Created by HellFirePvP
 * Date: 08.08.2019 / 06:52
 */
public class ASMHookEndpoint {

    public static Map<Enchantment, Integer> applyNewEnchantmentLevels(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        return DynamicEnchantmentHelper.addNewLevels(enchantments, stack);
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack stack) {
        return DynamicEnchantmentHelper.getNewEnchantmentLevel(current, enchantment, stack, null);
    }

    public static ListNBT addNewEnchantmentLevelsTag(ListNBT list, ItemStack stack) {
        return DynamicEnchantmentHelper.modifyEnchantmentTags(list, stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static void addTooltipPreEnchantments(ItemStack stack, List<ITextComponent> tooltip) {
        List<ITextComponent> addition = new ArrayList<>();
        try {
            //Add any dynamic modifiers this item has.
            DynamicModifierHelper.addModifierTooltip(stack, addition);

            //Add prism enchantments
            Map<Enchantment, Integer> enchantments;
            if (!stack.hasTag() && !(enchantments = EnchantmentHelper.getEnchantments(stack)).isEmpty()) {
                for (Enchantment e : enchantments.keySet()) {
                    addition.add(e.getDisplayName(enchantments.get(e)));
                }
            }
        } catch (Exception exc) {
            addition.clear();
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.tooltipError").mergeStyle(TextFormatting.GRAY));
        }
        tooltip.addAll(addition);
    }

    @OnlyIn(Dist.CLIENT)
    public static float overrideSunBrightnessClient(float prevBrightness, World world) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        String strDimKey = world.getDimensionKey().getLocation().toString();
        if (ctx != null &&
                RenderingConfig.CONFIG.dimensionsWithSkyRendering.get().contains(strDimKey) &&
                ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            float perc = ctx.getCelestialEventHandler().getSolarEclipsePercent();
            perc = 0.05F + (perc * 0.95F);

            return prevBrightness * perc;
        }
        return prevBrightness;
    }

    public static int overrideSunBrightnessServer(int prevSkyLight, World world) {
        WorldContext ctx = SkyHandler.getContext(world);
        String strDimKey = world.getDimensionKey().getLocation().toString();
        if (ctx != null &&
                ctx.getCelestialEventHandler().getSolarEclipse().isActiveNow()) {
            return 11 - Math.round(ctx.getCelestialEventHandler().getSolarEclipsePercent() * 11F);
        }
        return prevSkyLight;
    }

    public static int fireCooldownEvent(CooldownTracker tracker, Item item, int ticks) {
        if (tracker instanceof ServerCooldownTracker) {
            CooldownSetEvent event = new CooldownSetEvent(((ServerCooldownTracker) tracker).player, item, ticks);
            MinecraftForge.EVENT_BUS.post(event);
            ticks = Math.max(event.getResultCooldown(), 1);
        }
        return ticks;
    }

    public static float getLivingEntityWaterSlowDown(float slowDownIn, LivingEntity entity) {
        if (!entity.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
            if (MantleEffectOctans.shouldPreventWaterSlowdown(entity.getItemStackFromSlot(EquipmentSlotType.CHEST), entity)) {
                return 0.92F;
            }
        }
        return slowDownIn;
    }

    public static AttributeModifierManager markPlayer(AttributeModifierManager map, LivingEntity entity) {
        AttributeEvent.setEntity(map, entity);
        return map;
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attributeInstance) {
        return AttributeEvent.postProcessVanilla(value, attributeInstance);
    }

    public static double getOverriddenSeenEntityReachMaximum(ServerPlayNetHandler handler, double original) {
        PlayerEntity player = handler.player;
        PlayerProgress prog = ResearchHelper.getProgress(player, player.getEntityWorld().isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return 999_999_999.0;
        }
        return original;
    }

    @OnlyIn(Dist.CLIENT)
    public static double getOverriddenCreativeEntityReach(double original, double blockReach) {
        PlayerProgress prog = ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return blockReach;
        }
        return original;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean doesOverrideDistanceRuling(boolean original) {
        PlayerProgress prog = ResearchHelper.getProgress(Minecraft.getInstance().player, LogicalSide.CLIENT);
        if (prog.isValid() && prog.getPerkData().hasPerkEffect(perk -> perk instanceof KeyEntityReach)) {
            return false;
        }
        return original;
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderParticles(MatrixStack renderStack, float pTicks) {
        EffectHandler.getInstance().render(renderStack, pTicks);

        //Setup GL states again
        //Seriously, keep a clean GL state for once mojang.
        RenderSystem.enableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
    }

    public static boolean addCustomCollision(boolean didCollision, VoxelShapeSpliterator iterator, Consumer<? super VoxelShape> action) {
        if (!didCollision) {
            if (CollisionHelper.onCollision(iterator, action)) {
                return true;
            }
            ReflectionHelper.setVoxelShapeIteratorDidCustomCollision(iterator);
        }
        return false;
    }

    public static Vector3d wrapCustomEntityCollision(Vector3d allowedMovement, @Nullable Entity entity) {
        if (entity == null) {
            return allowedMovement;
        }
        return CollisionHelper.onEntityCollision(allowedMovement, entity);
    }
}
