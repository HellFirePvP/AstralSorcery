/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.CommonProxy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: HerdableAnimal
 * Created by HellFirePvP
 * Date: 13.11.2016 / 16:43
 */
public interface HerdableAnimal<T extends EntityLivingBase> {

    static Map<Class<?>, HerdableAnimal> registryHerdable = new HashMap<>();

    public static <T extends EntityLivingBase> HerdableAnimal<T> getHerdable(T entity) {
        return registryHerdable.get(entity.getClass());
    }

    public static void init() {
        register(new Cow());
        register(new Chicken());
        register(new Sheep());
        register(new Pig());
        register(new Horse());
        register(new Rabbit());
        register(new Squid());
        register(new Mooshroom());
        register(new PolarBear());
        register(new Llama());
    }

    public static void register(HerdableAnimal herd) {
        registryHerdable.put(herd.getEntityClass(), herd);
    }

    public Class<T> getEntityClass();

    @Nonnull
    public List<ItemStack> getHerdingDropsTick(T entity, World world, Random rand, float herdingLuck);

    public static class Parrot implements HerdableAnimal<EntityParrot> {

        @Override
        public Class<EntityParrot> getEntityClass() {
            return EntityParrot.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityParrot entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_PARROT).generateLootForPools(rand, builder.build());
        }

    }

    public static class Llama implements HerdableAnimal<EntityLlama> {

        @Override
        public Class<EntityLlama> getEntityClass() {
            return EntityLlama.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityLlama entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_LLAMA).generateLootForPools(rand, builder.build());
        }
    }

    public static class PolarBear implements HerdableAnimal<EntityPolarBear> {

        @Override
        public Class<EntityPolarBear> getEntityClass() {
            return EntityPolarBear.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityPolarBear entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_POLAR_BEAR).generateLootForPools(rand, builder.build());
        }
    }

    public static class Mooshroom implements HerdableAnimal<EntityMooshroom> {

        @Override
        public Class<EntityMooshroom> getEntityClass() {
            return EntityMooshroom.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityMooshroom entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            List<ItemStack> drops = world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_MUSHROOM_COW).generateLootForPools(rand, builder.build());
            if(rand.nextFloat() <= 0.05 * Math.max(0, herdingLuck)) {
                drops.add(new ItemStack(Items.MUSHROOM_STEW));
            }
            return drops;
        }
    }

    public static class Rabbit implements HerdableAnimal<EntityRabbit> {

        @Override
        public Class<EntityRabbit> getEntityClass() {
            return EntityRabbit.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityRabbit entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_RABBIT).generateLootForPools(rand, builder.build());
        }
    }

    public static class Horse implements HerdableAnimal<EntityHorse> {

        @Override
        public Class<EntityHorse> getEntityClass() {
            return EntityHorse.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityHorse entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_HORSE).generateLootForPools(rand, builder.build());
        }
    }

    public static class Squid implements HerdableAnimal<EntitySquid> {

        @Override
        public Class<EntitySquid> getEntityClass() {
            return EntitySquid.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntitySquid entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_SQUID).generateLootForPools(rand, builder.build());
        }

    }

    public static class Cow implements HerdableAnimal<EntityCow> {

        @Override
        public Class<EntityCow> getEntityClass() {
            return EntityCow.class;
        }

        @Override
        @Nonnull
        public List<ItemStack> getHerdingDropsTick(EntityCow entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            List<ItemStack> drops = world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_COW).generateLootForPools(rand, builder.build());
            if(rand.nextFloat() <= 0.15 * Math.max(0, herdingLuck)) {
                drops.add(new ItemStack(Items.MILK_BUCKET));
            }
            return drops;
        }

    }

    public static class Chicken implements HerdableAnimal<EntityChicken> {

        @Override
        public Class<EntityChicken> getEntityClass() {
            return EntityChicken.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityChicken entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            List<ItemStack> drops = world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_CHICKEN).generateLootForPools(rand, builder.build());
            if(rand.nextFloat() <= 0.2) {
                drops.add(new ItemStack(Items.EGG));
            }
            return drops;
        }

    }

    public static class Sheep implements HerdableAnimal<EntitySheep> {

        @Override
        public Class<EntitySheep> getEntityClass() {
            return EntitySheep.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntitySheep entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            List<ItemStack> drops = world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_SHEEP).generateLootForPools(rand, builder.build());
            if(!entity.getSheared() && rand.nextFloat() <= 0.3) {
                drops.add(new ItemStack(Blocks.WOOL, rand.nextInt(2) + 1));
            }
            return drops;
        }

    }

    public static class Pig implements HerdableAnimal<EntityPig> {

        @Override
        public Class<EntityPig> getEntityClass() {
            return EntityPig.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityPig entity, World world, Random rand, float herdingLuck) {
            LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
            builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
            return world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_PIG).generateLootForPools(rand, builder.build());
        }

    }

}
