/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EntityTypesAS;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.IPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityIlluminationSpark
 * Created by HellFirePvP
 * Date: 17.08.2019 / 10:45
 */
public class EntityIlluminationSpark extends ThrowableEntity {

    public EntityIlluminationSpark(World world) {
        super(EntityTypesAS.ILLUMINATION_SPARK, world);
    }

    public EntityIlluminationSpark(double x, double y, double z, World world) {
        super(EntityTypesAS.ILLUMINATION_SPARK, x, y, z, world);
    }

    public EntityIlluminationSpark(LivingEntity thrower, World world) {
        super(EntityTypesAS.ILLUMINATION_SPARK, thrower, world);
        this.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0F, 0.7F, 0.9F);
    }

    public static EntityType.IFactory<EntityIlluminationSpark> factory() {
        return (type, world) -> new EntityIlluminationSpark(world);
    }

    @Override
    protected void registerData() {}

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote()) {
            spawnEffects();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnEffects() {
        FXFacingParticle p;
        for (int i = 0; i < 6; i++) {
            p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(Vector3.atEntityCorner(this))
                    .setMotion(new Vector3(
                            0.04F - rand.nextFloat() * 0.08F,
                            0.04F - rand.nextFloat() * 0.08F,
                            0.04F - rand.nextFloat() * 0.08F
                    ))
                    .setScaleMultiplier(0.25F);
            randomizeColor(p);
        }

        p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(Vector3.atEntityCorner(this));
        p.setScaleMultiplier(0.6F);
        randomizeColor(p);

        p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(Vector3.atEntityCorner(this).add(getMotion().mul(0.5, 0.5, 0.5)));
        p.setScaleMultiplier(0.6F);
        randomizeColor(p);

    }

    @OnlyIn(Dist.CLIENT)
    private void randomizeColor(FXFacingParticle p) {
        switch (rand.nextInt(3)) {
            case 0:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_1));
                break;
            case 1:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_2));
                break;
            case 2:
                p.color(VFXColorFunction.constant(ColorsAS.ILLUMINATION_POWDER_3));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote()) {
            return;
        }
        if (!(result instanceof BlockRayTraceResult) || !(this.getThrower() instanceof PlayerEntity)) {
            remove();
            return;
        }
        PlayerEntity player = (PlayerEntity) this.getThrower();
        BlockRayTraceResult brtr = (BlockRayTraceResult) result;

        BlockItemUseContext bCtx = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, brtr));

        BlockPos pos = bCtx.getPos();
        if (!BlockUtils.isReplaceable(world, pos)) {
            pos = pos.offset(bCtx.getFace());
        }

        if (!ForgeEventFactory.onBlockPlace(player, BlockSnapshot.getBlockSnapshot(world, pos), bCtx.getFace())) {
            world.setBlockState(pos, BlocksAS.FLARE_LIGHT.getDefaultState());
        }
        remove();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
