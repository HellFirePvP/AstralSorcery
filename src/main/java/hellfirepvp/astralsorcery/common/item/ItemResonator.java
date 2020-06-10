/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.AreaOfInfluencePreview;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.base.OverrideInteractItem;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.base.TileAreaOfInfluence;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemResonator
 * Created by HellFirePvP
 * Date: 24.04.2020 / 20:30
 */
public class ItemResonator extends Item implements OverrideInteractItem {

    public ItemResonator() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));

        this.addPropertyOverride(new ResourceLocation("upgrade"),
                (stack, world, entity) -> {
                    if (!(entity instanceof PlayerEntity)) {
                        return ResonatorUpgrade.STARLIGHT.ordinal() / (float) ResonatorUpgrade.values().length;
                    }
                    ResonatorUpgrade current = getCurrentUpgrade((PlayerEntity) entity, stack);
                    return current.ordinal() / (float) ResonatorUpgrade.values().length;
                });
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));

            ItemStack upgradedResonator = new ItemStack(this);
            setUpgradeUnlocked(upgradedResonator, ResonatorUpgrade.values());
            items.add(upgradedResonator);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag extended) {
        ResonatorUpgrade current = getCurrentUpgrade(Minecraft.getInstance().player, stack);
        for (ResonatorUpgrade upgrade : getUpgrades(stack)) {
            TextFormatting color = upgrade.equals(current) ? TextFormatting.GOLD : TextFormatting.BLUE;
            tooltip.add(new TranslationTextComponent(upgrade.getUnlocalizedTypeName()).setStyle(new Style().setColor(color)));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected) {
            selected = entity instanceof LivingEntity && ((LivingEntity) entity).getHeldItemOffhand() == stack;
        }

        if (!world.isRemote()) {
            if (selected && entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                if (getCurrentUpgrade(player, stack) == ResonatorUpgrade.FLUID_FIELDS) {
                    float distribution = DayTimeHelper.getCurrentDaytimeDistribution(world);
                    if (distribution <= 1E-4) {
                        return;
                    }
                    if (random.nextFloat() < distribution && random.nextInt(12) == 0) {
                        int offsetX = random.nextInt(30) * (random.nextBoolean() ? 1 : -1);
                        int offsetZ = random.nextInt(30) * (random.nextBoolean() ? 1 : -1);

                        BlockPos pos = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                new BlockPos(entity.getPosition()).add(offsetX, 0, offsetZ));
                        if (pos.distanceSq(entity.getPosition()) > 5625) { // 75 blocks away
                            return;
                        }

                        IChunk ch = world.getChunk(pos);
                        if (ch instanceof Chunk) {
                            ((Chunk) ch).getCapability(CapabilitiesAS.CHUNK_FLUID).ifPresent(entry -> {
                                FluidStack display = entry.drain(1, true);
                                if (!display.isEmpty()) {
                                    PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.LIQUID_FOUNTAIN).addData(buf -> {
                                        ByteBufUtils.writeFluidStack(buf, display);
                                        ByteBufUtils.writeVector(buf, new Vector3(pos));
                                    });
                                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 32));
                                }
                            });
                        }
                    }
                }
            }
        } else {
            clientInventoryTick(stack, world, entity, slot, selected);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;

        if (selected &&
                getCurrentUpgrade(player, stack) == ResonatorUpgrade.STARLIGHT &&
                WorldSeedCache.getSeedIfPresent(world).isPresent()) {

            float distribution = DayTimeHelper.getCurrentDaytimeDistribution(world);
            if (distribution <= 1E-4) {
                return;
            }
            BlockPos center = player.getPosition();
            int offsetX = center.getX();
            int offsetZ = center.getZ();
            try (BlockPos.PooledMutable pool = BlockPos.PooledMutable.retain()) {

                for (int xx = -30; xx <= 30; xx++) {
                    for (int zz = -30; zz <= 30; zz++) {
                        pool.setPos(world.getHeight(Heightmap.Type.WORLD_SURFACE, pool.setPos(offsetX + xx, 0, offsetZ + zz)));

                        float perc = SkyCollectionHelper.getSkyNoiseDistributionClient(world, pool).get();

                        float fPerc = (float) Math.pow((perc - 0.4F) * 1.65F, 2);
                        if (perc >= 0.4F && random.nextFloat() <= fPerc) {
                            if (random.nextFloat() <= fPerc && random.nextInt(6) == 0) {

                                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                        .spawn(new Vector3(pool).add(random.nextFloat(), 0.15, random.nextFloat()))
                                        .color(VFXColorFunction.constant(ColorsAS.RESONATOR_STARFIELD))
                                        .setScaleMultiplier(4F)
                                        .setAlphaMultiplier(distribution * fPerc);
                                if (perc >= 0.8F && random.nextInt(3) == 0) {

                                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                            .spawn(new Vector3(pool).add(random.nextFloat(), 0.15, random.nextFloat()))
                                            .setScaleMultiplier(0.3F)
                                            .color(VFXColorFunction.WHITE)
                                            .setGravityStrength(-0.001F)
                                            .setAlphaMultiplier(distribution);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldInterceptInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        ResonatorUpgrade upgrade = getCurrentUpgrade(player, player.getHeldItem(hand));
        return upgrade == ResonatorUpgrade.AREA_SIZE && MiscUtils.getTileAt(player.getEntityWorld(), pos, TileAreaOfInfluence.class, false) != null;
    }

    @Override
    public boolean doInteract(LogicalSide side, PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
        ResonatorUpgrade upgrade = getCurrentUpgrade(player, player.getHeldItem(hand));
        if (upgrade == ResonatorUpgrade.AREA_SIZE && player.getEntityWorld().isRemote()) {
            TileAreaOfInfluence aoeTile = MiscUtils.getTileAt(player.getEntityWorld(), pos, TileAreaOfInfluence.class, false);
            if (aoeTile != null) {
                playAreaOfInfluenceEffect(aoeTile);
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void playAreaOfInfluenceEffect(TileAreaOfInfluence aoeTile) {
        AreaOfInfluencePreview.INSTANCE.show(aoeTile);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote() && player.isSneaking()) {
            if (cycleUpgrade(player, player.getHeldItem(hand))) {
                return ActionResult.resultSuccess(player.getHeldItem(hand));
            }
        }
        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    public static boolean cycleUpgrade(@Nonnull PlayerEntity player, ItemStack stack) {
        ResonatorUpgrade current = getCurrentUpgrade(player, stack);
        ResonatorUpgrade next = getNextSelectableUpgrade(player, stack);
        return next != null && !next.equals(current) && setCurrentUpgrade(player, stack, next);
    }

    @Nullable
    public static ResonatorUpgrade getNextSelectableUpgrade(@Nonnull PlayerEntity viewing, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return null;
        }
        ResonatorUpgrade current = getCurrentUpgrade(viewing, stack);
        int currentOrd = current.ordinal();
        int test = currentOrd;
        do {
            test++;
            test %= ResonatorUpgrade.values().length;
            ResonatorUpgrade testUpgrade = ResonatorUpgrade.values()[test];
            if (testUpgrade.canSwitchTo(viewing, stack) && !testUpgrade.equals(current)) {
                return testUpgrade;
            }
        } while (test != currentOrd);
        return null;
    }

    public static boolean setCurrentUpgrade(PlayerEntity setting, ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return false;
        }
        if (upgrade.canSwitchTo(setting, stack)) {
            NBTHelper.getPersistentData(stack).putInt("selected_upgrade", upgrade.ordinal());
            return true;
        }
        return false;
    }

    public static ItemStack setCurrentUpgradeUnsafe(ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return stack;
        }
        NBTHelper.getPersistentData(stack).putInt("selected_upgrade", upgrade.ordinal());
        return stack;
    }

    @Nonnull
    public static ResonatorUpgrade getCurrentUpgrade(@Nullable PlayerEntity viewing, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return ResonatorUpgrade.STARLIGHT; //Fallback
        }
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        int current = cmp.getInt("selected_upgrade");
        ResonatorUpgrade upgrade = ResonatorUpgrade.values()[MathHelper.clamp(current, 0, ResonatorUpgrade.values().length - 1)];
        if (viewing != null) {
            if (!upgrade.canSwitchTo(viewing, stack)) {
                return ResonatorUpgrade.STARLIGHT;
            }
        }
        return upgrade;
    }

    public static ItemStack setUpgradeUnlocked(ItemStack stack, ResonatorUpgrade... upgrades) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return stack;
        }
        for (ResonatorUpgrade upgrade : upgrades) {
            upgrade.applyUpgrade(stack);
        }
        return stack;
    }

    public static boolean hasUpgrade(ItemStack stack, ResonatorUpgrade upgrade) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return false;
        }
        return upgrade.hasUpgrade(stack);
    }

    public static List<ResonatorUpgrade> getUpgrades(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemResonator)) {
            return Lists.newArrayList();
        }
        List<ResonatorUpgrade> upgrades = Lists.newLinkedList();
        for (ResonatorUpgrade ru : ResonatorUpgrade.values()) {
            if (ru.hasUpgrade(stack)) {
                upgrades.add(ru);
            }
        }
        return upgrades;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getCurrentUpgrade(null, stack).getUnlocalizedItemName();
    }

    public static enum ResonatorUpgrade {


        STARLIGHT("starlight",
                (player, side, stack) -> true),
        FLUID_FIELDS("liquid",
                (player, side, stack) -> ResearchHelper.getProgress(player, side).getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT)),
        AREA_SIZE("structure",
                (player, side, stack) -> ResearchHelper.getProgress(player, side).getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT));

        private final TriPredicate<PlayerEntity, LogicalSide, ItemStack> check;
        private final String appendixUpgrade;

        private ResonatorUpgrade(String appendixUpgrade, TriPredicate<PlayerEntity, LogicalSide, ItemStack> check) {
            this.check = check;
            this.appendixUpgrade = appendixUpgrade;
        }

        public String getUnlocalizedItemName() {
            return "item.astralsorcery.resonator." + this.appendixUpgrade;
        }

        public String getUnlocalizedTypeName() {
            return "item.astralsorcery.resonator.upgrade." + this.appendixUpgrade;
        }

        public boolean hasUpgrade(ItemStack stack) {
            int id = ordinal();
            CompoundNBT cmp = NBTHelper.getPersistentData(stack);
            if (cmp.contains("upgrades", Constants.NBT.TAG_LIST)) {
                ListNBT list = cmp.getList("upgrades", Constants.NBT.TAG_INT);
                for (int i = 0; i < list.size(); i++) {
                    if (list.getInt(i) == id) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean canSwitchTo(@Nonnull PlayerEntity player, ItemStack stack) {
            return hasUpgrade(stack) && check.test(player, EffectiveSide.get(), stack);
        }

        public void applyUpgrade(ItemStack stack) {
            if (hasUpgrade(stack)) return;

            CompoundNBT cmp = NBTHelper.getPersistentData(stack);
            if (!cmp.contains("upgrades", Constants.NBT.TAG_LIST)) {
                cmp.put("upgrades", new ListNBT());
            }
            ListNBT list = cmp.getList("upgrades", Constants.NBT.TAG_INT);
            list.add(IntNBT.valueOf(ordinal()));
        }
    }
}
