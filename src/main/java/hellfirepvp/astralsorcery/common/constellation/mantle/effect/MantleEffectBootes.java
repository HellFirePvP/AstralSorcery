/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectBootes
 * Created by HellFirePvP
 * Date: 22.02.2020 / 11:22
 */
public class MantleEffectBootes extends MantleEffect {

    public static BootesConfig CONFIG = new BootesConfig();

    public MantleEffectBootes() {
        super(ConstellationsAS.bootes);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.LOW, this::onHurt);
        bus.addListener(EventPriority.LOW, this::onAttacked);
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        ItemStack mantle = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (mantle.isEmpty() || !(mantle.getItem() instanceof ItemMantle)) {
            return;
        }

        World world = player.getEntityWorld();
        List<EntityFlare> flares = gatherFlares(world, mantle);
        if (flares.size() < CONFIG.maxFlareCount.get()) {
            if (player.ticksExisted % 80 == 0) {
                if (AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFlare.get()) && rand.nextInt(4) == 0) {
                    EntityFlare flare = EntityTypesAS.FLARE.create(player.getEntityWorld());
                    flare.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
                    flare.setFollowingTarget(player);
                    if (world.addEntity(flare)) {
                        flares.add(flare);
                        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFlare.get(), false);
                    }
                }
            }
        }

        for (EntityFlare flare : flares) {
            if (flare.getFollowingTarget() != null && (flare.getAttackTarget() == null ? player.getDistance(flare) >= 12 : player.getDistance(flare) >= 35)) {
                flare.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), 0, 0);
            }
        }
        setEntityIds(mantle, flares.stream().map(Entity::getEntityId).collect(Collectors.toList()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        this.playCapeSparkles(player, 0.15F);
    }

    private void onAttacked(LivingAttackEvent event) {
        LivingEntity attacked = event.getEntityLiving();
        DamageSource src = event.getSource();
        if (!attacked.getEntityWorld().isRemote() && src.getTrueSource() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) src.getTrueSource();
            if (ItemMantle.getEffect(attacker, ConstellationsAS.bootes) != null && attacked.isAlive()) {
                if (attacked instanceof PlayerEntity && !MiscUtils.canPlayerAttackServer(attacker, attacked)) {
                    return;
                }
                this.forEachFlare(attacker, flare -> flare.setAttackTarget(attacked));
            }
        }
    }

    private void onHurt(LivingHurtEvent event) {
        LivingEntity hurt = event.getEntityLiving();
        if (!hurt.getEntityWorld().isRemote() && ItemMantle.getEffect(hurt, ConstellationsAS.bootes) != null) {
            Entity source = event.getSource().getTrueSource();
            if (source instanceof LivingEntity) {
                this.forEachFlare(hurt, flare -> flare.setAttackTarget((LivingEntity) source));
            }
        }
    }

    protected void forEachFlare(LivingEntity owner, Consumer<EntityFlare> fn) {
        ItemStack mantle = owner.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (mantle.isEmpty() || !(mantle.getItem() instanceof ItemMantle)) {
            return;
        }

        this.gatherFlares(owner.getEntityWorld(), mantle).forEach(fn);
    }

    protected List<EntityFlare> gatherFlares(World world, ItemStack mantleStack) {
        List<EntityFlare> flares = new ArrayList<>();
        for (int flareId : getEntityIds(mantleStack)) {
            Entity e = world.getEntityByID(flareId);
            if (e instanceof EntityFlare && e.isAlive()) {
                flares.add((EntityFlare) e);
            }
        }
        return flares;
    }

    protected void setEntityIds(ItemStack mantleStack, List<Integer> ids) {
        ListNBT list = new ListNBT();
        ids.forEach(i -> list.add(IntNBT.valueOf(i)));
        NBTHelper.getPersistentData(mantleStack).put("flareIds", list);
    }

    protected List<Integer> getEntityIds(ItemStack mantleStack) {
        List<Integer> ids = new ArrayList<>();
        ListNBT nbtIds = NBTHelper.getPersistentData(mantleStack).getList("flareIds", Constants.NBT.TAG_INT);
        for (int i = 0; i < nbtIds.size(); i++) {
            ids.add(nbtIds.getInt(i));
        }
        return ids;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class BootesConfig extends Config {

        private final int defaultMaxFlareCount = 3;

        private final int defaultChargeCostPerFlare = 400;

        public ForgeConfigSpec.IntValue maxFlareCount;

        public ForgeConfigSpec.IntValue chargeCostPerFlare;

        public BootesConfig() {
            super("bootes");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.maxFlareCount = cfgBuilder
                    .comment("Defines the maximum flare count the mantle can summon and keep following the wearer.")
                    .translation(translationKey("maxFlareCount"))
                    .defineInRange("maxFlareCount", this.defaultMaxFlareCount, 0, 6);

            this.chargeCostPerFlare = cfgBuilder
                    .comment("Set the amount alignment charge consumed per created flare")
                    .translation(translationKey("chargeCostPerFlare"))
                    .defineInRange("chargeCostPerFlare", this.defaultChargeCostPerFlare, 0, 1000);
        }
    }
}
