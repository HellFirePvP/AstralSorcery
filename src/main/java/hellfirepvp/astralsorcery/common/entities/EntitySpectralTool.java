/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntitySpectralTool
 * Created by HellFirePvP
 * Date: 11.10.2017 / 20:56
 */
public class EntitySpectralTool extends EntityFlying implements EntityTechnicalAmbient {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntitySpectralTool.class, DataSerializers.ITEM_STACK);
    private AIToolTask aiTask;
    private int ticksUntilDeath = 0;

    public EntitySpectralTool(World worldIn) {
        super(worldIn);
        setSize(0.6F, 0.8F);
        this.moveHelper = new EntityFlyHelper(this);
    }

    public EntitySpectralTool(World world, BlockPos spawnPos, ItemStack tool, ToolTask task) {
        super(world);
        setSize(0.6F, 0.8F);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        setItem(tool);
        this.aiTask.taskTarget = task;
        this.ticksUntilDeath = 100 + rand.nextInt(40);
        this.moveHelper = new EntityFlyHelper(this);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.5);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        aiTask = new AIToolTask(this);
        this.tasks.addTask(1, aiTask);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(world.isRemote) {
            spawnAmbientEffects();
        } else {
            this.ticksUntilDeath--;
            if(this.ticksUntilDeath <= 0) {
                attackEntityFrom(CommonProxy.dmgSourceStellar, 50000);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnAmbientEffects() {
        if(rand.nextFloat() < 0.2F) {
            Color c = IConstellation.weak;
            double x = posX + rand.nextFloat() * width - (width / 2);
            double y = posY + rand.nextFloat() * (height / 2) + 0.2;
            double z = posZ + rand.nextFloat() * width - (width / 2);

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.5F + 0.3F);
            p.setMaxAge(30 + rand.nextInt(20));

            if(rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    private void setItem(ItemStack tool) {
        this.dataManager.set(ITEM, tool);
    }

    public ItemStack getItem() {
        return this.dataManager.get(ITEM);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        setItem(NBTHelper.getStack(compound, "AS_SpectralItem", ItemStack.EMPTY));
        int task = compound.getInteger("AS_ToolTask");
        if(this.aiTask != null) {
            this.aiTask.taskTarget = new ToolTask(ToolTask.Type.values()[MathHelper.clamp(task, 0, ToolTask.Type.values().length - 1)]);
        } else {
            //Fcking thanks TOP
            this.aiTask = new AIToolTask(this);
            this.aiTask.taskTarget = new ToolTask(ToolTask.Type.BREAK_BLOCK);
        }
        this.ticksUntilDeath = compound.getInteger("AS_ToolDeathTicks");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        NBTHelper.setStack(compound, "AS_SpectralItem", getItem());
        int task = 0;
        if(this.aiTask != null) {
            task = this.aiTask.taskTarget.type.ordinal();
        }
        compound.setInteger("AS_ToolTask", task);
        compound.setInteger("AS_ToolDeathTicks", this.ticksUntilDeath);
    }

    public static class ToolTask {

        private final Type type;

        private ToolTask(Type type) {
            this.type = type;
        }

        public static ToolTask createPickaxeTask() {
            return new ToolTask(Type.BREAK_BLOCK);
        }

        public static ToolTask createLogTask() {
            return new ToolTask(Type.BREAK_LOG);
        }

        public static ToolTask createAttackTask() {
            return new ToolTask(Type.ATTACK_MONSTER);
        }

        private static enum Type {

            BREAK_BLOCK,
            BREAK_LOG,
            ATTACK_MONSTER

        }

    }

    private static class AIToolTask extends EntityAIBase {

        private final EntitySpectralTool parentEntity;
        private ToolTask taskTarget = null;

        private BlockPos designatedBreakTarget = null;
        private EntityLivingBase designatedAttackTarget = null;

        //Break-process ticks
        //Attack-cooldown ticks
        private int actionTicks = 0;

        public AIToolTask(EntitySpectralTool entity) {
            this.parentEntity = entity;
            this.setMutexBits(7);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();

            if (!entitymovehelper.isUpdating()) {
                return true;
            } else {
                switch (this.taskTarget.type) {
                    case BREAK_BLOCK:
                        BlockPos validStateStone = MiscUtils.searchAreaForFirst(parentEntity.world, parentEntity.getPosition(), 8, Vector3.atEntityCorner(this.parentEntity),
                                (world, at, state) -> world.getTileEntity(at) == null &&
                                        !state.getBlock().isAir(state, world, at) &&
                                        state.getBlockHardness(world, at) <= 10 &&
                                        MiscUtils.canToolBreakBlockWithoutPlayer(world, at, state, new ItemStack(Items.DIAMOND_PICKAXE))
                        );
                        return validStateStone != null;
                    case BREAK_LOG:
                        BlockPos validStateLog = MiscUtils.searchAreaForFirst(parentEntity.world, parentEntity.getPosition(), 10, Vector3.atEntityCorner(this.parentEntity),
                                (world, at, state) -> world.getTileEntity(at) == null &&
                                    !state.getBlock().isAir(state, world, at) &&
                                    (state.getBlock().isWood(world, at) || state.getBlock().isLeaves(state, world, at)) &&
                                    state.getBlockHardness(world, at) <= 10 &&
                                    MiscUtils.canToolBreakBlockWithoutPlayer(world, at, state, new ItemStack(Items.DIAMOND_AXE))
                        );
                        return validStateLog != null;
                    case ATTACK_MONSTER:
                        java.util.List<EntityLivingBase> eList = this.parentEntity.world.getEntitiesWithinAABB(
                                EntityLivingBase.class,
                                new AxisAlignedBB(-8, -8, -8, 8, 8, 8).offset(this.parentEntity.getPosition()),
                                e -> e != null && !e.isDead && e.isCreatureType(EnumCreatureType.MONSTER, false));
                        EntityLivingBase entity = EntityUtils.selectClosest(eList, (e) -> e.getDistanceSqToEntity(this.parentEntity));
                        return entity != null;
                }
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return designatedAttackTarget != null || designatedBreakTarget != null;
        }

        @Override
        public void resetTask() {
            super.resetTask();

            this.designatedBreakTarget = null;
            this.designatedAttackTarget = null;

            this.actionTicks = 0;
        }

        @Override
        public void updateTask() {
            super.updateTask();

            if(!shouldContinueExecuting()) {
                return;
            }

            if(actionTicks < 0) {
                actionTicks = 0; //lol. wtf.
            }

            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();
            boolean resetTimer = false;
            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    if(this.parentEntity.world.isAirBlock(this.designatedBreakTarget)) {
                        this.designatedBreakTarget = null;
                        resetTimer = true;
                    } else {
                        double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                        double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                        double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        this.parentEntity.getMoveHelper().setMoveTo(
                                this.designatedBreakTarget.getX(),
                                this.designatedBreakTarget.getY(),
                                this.designatedBreakTarget.getZ(),
                                1.5);
                        if(d3 < 3D) {
                            this.actionTicks++;
                            if(this.actionTicks > CapeEffectPelotrio.getTicksBreakBlockPick() && this.parentEntity.world instanceof WorldServer) {
                                MiscUtils.breakBlockWithoutPlayer((WorldServer) this.parentEntity.world, this.designatedBreakTarget,
                                        this.parentEntity.world.getBlockState(this.designatedBreakTarget), true, true, true);
                                resetTimer = true;
                            }
                        }
                    }
                    break;
                case BREAK_LOG:
                    if(this.parentEntity.world.isAirBlock(this.designatedBreakTarget)) {
                        this.designatedBreakTarget = null;
                        resetTimer = true;
                    } else {
                        double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                        double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                        double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        this.parentEntity.getMoveHelper().setMoveTo(
                                this.designatedBreakTarget.getX(),
                                this.designatedBreakTarget.getY(),
                                this.designatedBreakTarget.getZ(),
                                1.5);
                        if(d3 < 3D) {
                            this.actionTicks++;
                            if(this.actionTicks > CapeEffectPelotrio.getTicksBreakBlockAxe() && this.parentEntity.world instanceof WorldServer) {
                                MiscUtils.breakBlockWithoutPlayer((WorldServer) this.parentEntity.world, this.designatedBreakTarget,
                                        this.parentEntity.world.getBlockState(this.designatedBreakTarget), true, true, true);
                                resetTimer = true;
                            }
                        }
                    }
                    break;
                case ATTACK_MONSTER:
                    if(this.designatedAttackTarget.isDead) {
                        this.designatedAttackTarget = null;
                        resetTimer = true;
                    } else {
                        java.util.List<EntityLivingBase> eList = this.parentEntity.world.getEntitiesWithinAABB(
                                EntityLivingBase.class,
                                new AxisAlignedBB(-8, -8, -8, 8, 8, 8).offset(this.parentEntity.getPosition()),
                                e -> e != null && !e.isDead && e.isCreatureType(EnumCreatureType.MONSTER, false));
                        EntityLivingBase entity = EntityUtils.selectClosest(eList, (e) -> e.getDistanceSqToEntity(this.parentEntity));
                        if(entity != null) {
                            double d0 = entity.posX;
                            double d1 = entity.posY;
                            double d2 = entity.posZ;
                            this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.6D);
                        }

                        double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                        double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                        double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        this.parentEntity.getMoveHelper().setMoveTo(
                                this.designatedAttackTarget.posX,
                                this.designatedAttackTarget.posY,
                                this.designatedAttackTarget.posZ,
                                1.7);
                        if(d3 < 3D) {
                            this.actionTicks++;
                            if(this.actionTicks > CapeEffectPelotrio.getTicksSwordAttacks()) {
                                this.designatedAttackTarget.attackEntityFrom(CommonProxy.dmgSourceStellar, CapeEffectPelotrio.getSwordAttackDamage());
                                resetTimer = true;
                            }
                        }
                    }
                    break;
            }
            if(resetTimer) {
                this.actionTicks = 0;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            switch (this.taskTarget.type) {
                case BREAK_BLOCK:
                    BlockPos validStateStone = MiscUtils.searchAreaForFirst(parentEntity.world, parentEntity.getPosition(), 8, Vector3.atEntityCorner(this.parentEntity),
                            (world, at, state) -> world.getTileEntity(at) == null &&
                            !state.getBlock().isAir(state, world, at) &&
                            state.getBlockHardness(world, at) <= 10 &&
                            MiscUtils.canToolBreakBlockWithoutPlayer(world, at, state, new ItemStack(Items.DIAMOND_PICKAXE))
                    );
                    if(validStateStone != null) {
                        this.designatedBreakTarget = validStateStone;

                        double d0 = validStateStone.getX();
                        double d1 = validStateStone.getY();
                        double d2 = validStateStone.getZ();
                        this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.5);
                    }
                    break;
                case BREAK_LOG:
                    BlockPos validStateLog = MiscUtils.searchAreaForFirst(parentEntity.world, parentEntity.getPosition(), 10, Vector3.atEntityCorner(this.parentEntity),
                            (world, at, state) -> world.getTileEntity(at) == null &&
                                    !state.getBlock().isAir(state, world, at) &&
                                    (state.getBlock().isWood(world, at) || state.getBlock().isLeaves(state, world, at)) &&
                                    state.getBlockHardness(world, at) <= 10 &&
                                    MiscUtils.canToolBreakBlockWithoutPlayer(world, at, state, new ItemStack(Items.DIAMOND_AXE))
                    );
                    if(validStateLog != null) {
                        this.designatedBreakTarget = validStateLog;

                        double d0 = validStateLog.getX();
                        double d1 = validStateLog.getY();
                        double d2 = validStateLog.getZ();
                        this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.5);
                    }
                    break;
                case ATTACK_MONSTER:
                    java.util.List<EntityLivingBase> eList = this.parentEntity.world.getEntitiesWithinAABB(
                            EntityLivingBase.class,
                            new AxisAlignedBB(-8, -8, -8, 8, 8, 8).offset(this.parentEntity.getPosition()),
                            e -> e != null && !e.isDead && e.isCreatureType(EnumCreatureType.MONSTER, false));
                    EntityLivingBase entity = EntityUtils.selectClosest(eList, (e) -> e.getDistanceSqToEntity(this.parentEntity));
                    if(entity != null) {
                        this.designatedAttackTarget = entity;

                        double d0 = entity.posX;
                        double d1 = entity.posY;
                        double d2 = entity.posZ;
                        this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.7);
                    }
                    break;
            }
        }

    }

}
