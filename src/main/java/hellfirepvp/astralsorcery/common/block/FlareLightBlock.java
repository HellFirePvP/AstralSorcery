/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlareLightBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FlareLightBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(6, 6, 6, 10, 10, 10);

    public static MapCodec<FlareLightBlock> CODEC = simpleCodec(FlareLightBlock::new);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public FlareLightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(COLOR, DyeColor.YELLOW));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        ColorWrapper color = ColorsAS.DYE_COLORS[state.getValue(COLOR).getId()];

        for (int i = 0; i < 2; i++) {
            Vector3 vec = Vector3.atCenter(pos)
                    .add(random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1),
                            random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1),
                            random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1));
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(vec)
                    .setAlpha(0.35F)
                    .setScale(0.35F + random.nextFloat() * 0.15F)
                    .color(FXColorFunction.constant(color))
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setGravity(Vector3.y(0.0004F + random.nextFloat() * 0.0002F))
                    .setMaxAge(50 + random.nextInt(20));
        }

        Vector3 vec = Vector3.atCenter(pos)
                .add(random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1),
                        random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1),
                        random.nextFloat() * 0.1 * (random.nextBoolean() ? 1 : -1));
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(vec)
                .setAlpha(0.75F)
                .setScale(0.2F + random.nextFloat() * 0.1F)
                .color(FXColorFunction.WHITE)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setGravity(Vector3.y(0.0004F + random.nextFloat() * 0.0002F))
                .setMaxAge(40 + random.nextInt(10));
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
