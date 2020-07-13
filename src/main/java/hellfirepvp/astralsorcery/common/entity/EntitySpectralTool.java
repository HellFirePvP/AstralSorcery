/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectPelotrio;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolBreakBlockGoal;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolGoal;
import hellfirepvp.astralsorcery.common.entity.goal.SpectralToolMeleeAttackGoal;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntitySpectralTool
 * Created by HellFirePvP
 * Date: 22.02.2020 / 14:25
 */
public class EntitySpectralTool extends FlyingEntity {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntitySpectralTool.class, DataSerializers.ITEMSTACK);

    private LivingEntity owningEntity = null;
    private SpectralToolGoal task = null;
    private BlockPos startPosition = null;
    private int remainingTime = 0;

    private int idleTime = 0;

    public EntitySpectralTool(World worldIn) {
        super(EntityTypesAS.SPECTRAL_TOOL, worldIn);
        this.moveController = new FlyingMovementController(this, 10, false);
    }

    public EntitySpectralTool(World worldIn, BlockPos spawnPos, LivingEntity owner, ToolTask task) {
        this(worldIn);
        this.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ());
        this.setItem(task.displayStack);
        this.startPosition = spawnPos;
        this.owningEntity = owner;
        this.task = task.createGoal(this);
        this.goalSelector.addGoal(1, this.task);
        this.remainingTime = task.maxAge + rand.nextInt(task.maxAge);
    }

    public static EntityType.IFactory<EntitySpectralTool> factory() {
        return (type, world) -> new EntitySpectralTool(world);
    }

    @Override
    protected void registerData() {
        super.registerData();

        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();

        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.85);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getEntityWorld().isRemote()) {
            this.tickClient();
        } else {
            if (this.startPosition == null) {
                this.remove();
                return;
            }

            if (!this.task.shouldExecute()) {
                this.idleTime++;
                if (this.idleTime >= 30) {
                    this.remove();
                    return;
                }
            } else {
                this.idleTime = 0;
            }

            this.remainingTime--;
            if (this.remainingTime <= 0) {
                DamageUtil.attackEntityFrom(this, CommonProxy.DAMAGE_SOURCE_STELLAR, 50.0F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        if (rand.nextFloat() < 0.2F) {
            Vector3 at = Vector3.atEntityCorner(this)
                    .add(rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1) + this.getHeight() / 2,
                            rand.nextFloat() * 0.3 * (rand.nextBoolean() ? 1 : -1));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(ColorsAS.CONSTELLATION_TYPE_WEAK))
                    .setScaleMultiplier(0.35F + rand.nextFloat() * 0.25F)
                    .setMaxAge(30 + rand.nextInt(20));
            if (rand.nextBoolean()) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .color(VFXColorFunction.WHITE)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.15F)
                        .setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    public BlockPos getStartPosition() {
        return startPosition;
    }

    public LivingEntity getOwningEntity() {
        return owningEntity;
    }

    private void setItem(@Nonnull ItemStack tool) {
        this.dataManager.set(ITEM, tool);
    }

    @Nonnull
    public ItemStack getItem() {
        return this.dataManager.get(ITEM);
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (!(entityIn instanceof PlayerEntity || entityIn instanceof EntitySpectralTool)) {
            super.applyEntityCollision(entityIn);
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (!(entityIn instanceof PlayerEntity || entityIn instanceof EntitySpectralTool)) {
            super.applyEntityCollision(entityIn);
        }
    }

    public static class ToolTask {

        private final int maxAge;
        private final double speed;
        private final ItemStack displayStack;
        private final BiFunction<EntitySpectralTool, Double, SpectralToolGoal> toolGoal;

        protected ToolTask(int maxAge, double speed, ItemStack displayStack, BiFunction<EntitySpectralTool, Double, SpectralToolGoal> toolGoal) {
            this.maxAge = maxAge;
            this.speed = speed;
            this.displayStack = displayStack;
            this.toolGoal = toolGoal;
        }

        public static ToolTask createPickaxeTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationPickaxe.get(),
                    MantleEffectPelotrio.CONFIG.speedPickaxe.get(),
                    new ItemStack(Items.DIAMOND_PICKAXE),
                    SpectralToolBreakBlockGoal::new);
        }

        public static ToolTask createLogTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationAxe.get(),
                    MantleEffectPelotrio.CONFIG.speedAxe.get(),
                    new ItemStack(Items.DIAMOND_AXE),
                    SpectralToolBreakBlockGoal::new);
        }

        public static ToolTask createAttackTask() {
            return new ToolTask(MantleEffectPelotrio.CONFIG.durationSword.get(),
                    MantleEffectPelotrio.CONFIG.speedSword.get(),
                    new ItemStack(Items.DIAMOND_SWORD),
                    SpectralToolMeleeAttackGoal::new);
        }

        private SpectralToolGoal createGoal(EntitySpectralTool tool) {
            return this.toolGoal.apply(tool, this.speed);
        }
    }
}
