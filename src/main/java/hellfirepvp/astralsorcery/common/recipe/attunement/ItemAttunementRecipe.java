/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.attunement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRefreshFunction;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXItemAttunementOrbitalSource;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXImmediateFacingSprite;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.item.base.AttuneableItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.EntityUtil;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemAttunementRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemAttunementRecipe extends AttunementRecipe<ItemAttunementRecipe.Active> {

    public static final ResourceLocation ID = AstralSorcery.key("item_attunement");
    public static final ItemAttunementRecipe INSTANCE = new ItemAttunementRecipe();

    private static final AABB BOX = new AABB(0, 0, 0, 1, 1, 1).inflate(1);

    protected ItemAttunementRecipe() {
        super(ID);
    }

    @Override
    public boolean canStartCrafting(TileAttunementAltar altar) {
        return DayTimeHelper.isNight(altar.getLevel()) && findEligibleItem(altar).isPresent();
    }

    @Override
    public Active createActiveRecipe(TileAttunementAltar altar) {
        return altar.getTileData().getActiveConstellation().flatMap(cst -> {
            return findEligibleItem(altar).map(item -> {
                return new ItemAttunementRecipe.Active(cst, item.getId());
            });
        }).orElse(null);
    }

    @Override
    public MapCodec<Active> codec() {
        return Active.CODEC;
    }

    public static Optional<ItemEntity> findEligibleItem(TileAttunementAltar altar) {
        return altar.getTileData().getActiveConstellation().flatMap(cst -> {
            AABB searchBox = BOX.move(altar.getBlockPos().above());

            Vector3 altarVec = Vector3.atCenter(altar).addY(1);
            List<ItemEntity> items = altar.getLevel().getEntities(EntityTypeTest.forClass(ItemEntity.class), searchBox, ItemAttunementRecipe::isEligibleItem);
            if (!items.isEmpty()) {
                return EntityUtil.selectClosest(items, altarVec::distanceSquared);
            }
            return Optional.empty();
        });
    }

    public static boolean isEligibleItem(ItemEntity entity) {
        if (entity.isAlive() && entity.getItem().is(TagsAS.Items.FUNCTIONAL_ATTUNEABLE_ITEM)) {
            ItemStack stack = entity.getItem();

            ConstellationPaperComponent cmp = stack.getOrDefault(DataComponentsAS.ATTUNED_CONSTELLATION, AttunedConstellationComponent.EMPTY);
            return cmp.getConstellation().isEmpty();
        }
        return false;
    }

    public static class Active extends AttunementRecipe.Active<ItemAttunementRecipe, ItemAttunementRecipe.Active> {

        private static final int DURATION_CRYSTAL_ATTUNEMENT = 500;
        public static final MapCodec<Active> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("tick").forGetter(Active::getTick),
                RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("constellation").forGetter(Active::getConstellation),
                Codec.INT.fieldOf("itemId").forGetter(Active::getItemId)
        ).apply(inst, Active::new));

        private final int itemId;

        private final ClientObject<PlayableSoundInstance> attunementSound = new ClientObject<>();
        private final ClientObject<VFXImmediateFacingSprite> attunementFX = new ClientObject<>();
        private final ClientObject<FXItemAttunementOrbitalSource> orbitalSource = new ClientObject<>();

        protected Active(BaseConstellation constellation, int itemId) {
            super(INSTANCE, constellation);
            this.itemId = itemId;
        }

        protected Active(int tick, BaseConstellation constellation, int itemId) {
            super(INSTANCE, tick, constellation);
            this.itemId = itemId;
        }

        @Override
        public boolean matches(TileAttunementAltar altar) {
            if (!super.matches(altar)) return false;

            return this.getEntity(altar.getLevel()).map(ItemAttunementRecipe::isEligibleItem).orElse(false);
        }

        @Override
        public void startCrafting(TileAttunementAltar altar) {

        }

        @Override
        public void stopCrafting(TileAttunementAltar altar) {

        }

        @Override
        public void finishRecipe(TileAttunementAltar altar) {
            altar.getTileData().getActiveConstellation().ifPresent(cst -> {
                this.getEntity(altar.getLevel()).ifPresent(itemEntity -> {
                    ItemStack stack = itemEntity.getItem();

                    if (stack.getItem() instanceof AttuneableItem aItem) {
                        ItemStack newStack = ItemUtil.swapItem(altar.getLevel().registryAccess(), stack, aItem.getAttunedItem()).orElse(ItemStack.EMPTY);
                        if (!newStack.isEmpty()) {
                            stack = newStack;
                        }
                    }

                    AttunedConstellationComponent cmp = new AttunedConstellationComponent(cst);
                    stack.set(DataComponentsAS.ATTUNED_CONSTELLATION, cmp);
                    itemEntity.setItem(stack);

                    if (itemEntity.getOwner() instanceof ServerPlayer sPlayer) {
                        //TODO attune advancement
                    }
                });
            });
        }

        @Override
        public void doTick(LogicalSide side, TileAttunementAltar altar) {
            this.getEntity(altar.getLevel()).ifPresent(itemEntity -> {
                this.updateEntityPosition(itemEntity, altar.getBlockPos());
            });

            if (side.isClient()) {
                altar.getTileData().getActiveConstellation().ifPresent(cst -> {
                    this.spawnActiveEffects(altar, cst);
                    this.tickPlaySound(altar);
                });
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void spawnActiveEffects(TileAttunementAltar altar, BaseConstellation cst) {
            if (this.orbitalSource.isNull()) {
                FXItemAttunementOrbitalSource src = new FXItemAttunementOrbitalSource(Vector3.atBottomCenter(altar), Vector3.atBottomCenter(altar).addY(1.75F), cst)
                        .setOrbitRadius(3)
                        .setOrbitalPoints(4)
                        .setOrbitAxis(Vector3.RotAxis.Y_AXIS)
                        .refresh(FXRefreshFunction.tileExistsAnd(altar, (tile, fx) -> {
                            return altar.canPlayConstellationEffects(altar.getLevel()) &&
                                    altar.getTileData().getActiveRecipe().isPresent() &&
                                    altar.getTileData().getActiveRecipe().get().getRecipe() == ItemAttunementRecipe.INSTANCE;
                        }));
                this.orbitalSource.set(EffectHelper.source(src));
            }

            if (this.getTick() >= 40 && (this.attunementFX.isNull() || this.attunementFX.get().isRemoved())) {
                VFXImmediateFacingSprite spr = EffectHelper.of(EffectTemplatesAS.IMMEDIATE_FACING_SPRITE)
                        .spawn(Vector3.atCenter(altar).addY(1.25))
                        .setSpriteSheet(SpritesAS.SPRITE_ATTUNEMENT_ITEM_FLARE)
                        .setScale(2F)
                        .alpha(FXAlphaFunction.fadeIn(40))
                        .refresh(FXRefreshFunction.tileExistsAnd(altar, (tile, fx) -> {
                            return altar.canPlayConstellationEffects(altar.getLevel()) &&
                                    altar.getTileData().getActiveRecipe().isPresent() &&
                                    altar.getTileData().getActiveRecipe().get().getRecipe() == ItemAttunementRecipe.INSTANCE;
                        }));
                this.attunementFX.set(spr);
            }

            FXColorFunction<?> beamColor = FXColorFunction.constant(ColorWrapper.WHITE);
            if (this.getTick() >= 80 && this.getTick() % 40 == 0) {
                TileAttunementAltar.AttunementConstellationFinder finder = new TileAttunementAltar.AttunementConstellationFinder(altar.getLevel(), altar.getBlockPos());
                finder.getConstellationPositions(cst).forEach(pos -> {
                    Vector3 from = VectorUtil.withRandomOffset(Vector3.atBottomCenter(altar), rand, 0.1F);

                    EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                            .spawn(from)
                            .setup(from.copy().addY(6), 1.2, 1.2)
                            .color(beamColor)
                            .setAlpha(0.8F)
                            .setMaxAge(55);
                });
            }

            float total = DURATION_CRYSTAL_ATTUNEMENT;
            float percCycle = (float) (((getTick() % total) / total) * 2 * Math.PI);
            int parts = getTick() % 50 == 0 ? 180 : 6;
            Vector3 center = new Vector3(altar).add(0.5, 0.1, 0.5);
            float angleSwirl = 120F;
            float dst = 4.5F;

            for (int i = 0; i < parts; i++) {
                Vector3 v = Vector3.RotAxis.X_AXIS.getVector();
                float originalAngle = (((float) i) / ((float) parts)) * 360F;
                double angle = originalAngle + (Mth.sin(percCycle) * angleSwirl);
                v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(dst);
                Vector3 pos = center.copy();
                Vector3 mot = center.copy().subtract(pos.copy().add(v)).normalize().multiply(0.14);

                int age = 20 + rand.nextInt(30);
                float size = 0.2F + rand.nextFloat() * 0.7F;

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(FXColorFunction.WHITE)
                        .setScale(size)
                        .setMotion(mot)
                        .setMaxAge(age);

                if (rand.nextInt(6) == 0) {
                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(pos)
                            .color(FXColorFunction.constant(cst.getConstellationColor()))
                            .setScale(size * 1.4F)
                            .setMotion(mot)
                            .setGravity(Vector3.y(0.0004F + rand.nextFloat() * 0.00015F))
                            .setMaxAge(age + 30);
                }
            }


            double scale = 7.0D;
            double edgeScale = (scale * 2 + 1);
            for (int i = 0; i < 7; i++) {
                Vector3 offset = new Vector3(altar).add(-scale, 0.1, -scale);
                if (rand.nextBoolean()) {
                    offset.add(edgeScale * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * edgeScale);
                } else {
                    offset.add(rand.nextFloat() * edgeScale, 0, edgeScale * (rand.nextBoolean() ? 1 : 0));
                }

                EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .color(FXColorFunction.WHITE)
                        .setScale(0.3F + rand.nextFloat() * 0.15F)
                        .setGravity(Vector3.y(0.0002F + rand.nextFloat() * 0.0001F))
                        .setMaxAge(40 + rand.nextInt(10));

                if (rand.nextBoolean()) {
                    fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                }
            }

            if (this.getTick() >= 200) {
                for (int i = 0; i < 3; i++) {
                    Vector3 at = Vector3.atBottomCenter(altar);
                    at.addX(rand.nextFloat() * 7F * (rand.nextBoolean() ? 1 : -1));
                    at.addZ(rand.nextFloat() * 7F * (rand.nextBoolean() ? 1 : -1));

                    EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(at)
                            .setAlpha(0.75F)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .color(FXColorFunction.WHITE)
                            .setScale(0.3F + rand.nextFloat() * 0.1F)
                            .setGravity(Vector3.y(0.001F + rand.nextFloat() * 0.0005F))
                            .setMaxAge(20 + rand.nextInt(10));

                    if (rand.nextBoolean()) {
                        fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                    }
                    if (this.getTick() >= 400) {
                        fx.setScale(0.4F + rand.nextFloat() * 0.2F);
                    }
                }
            }

            if (this.getTick() >= 460 && this.getTick() % 5 == 0) {
                Vector3 from = VectorUtil.withRandomOffset(Vector3.atBottomCenter(altar), rand, 0.25F);

                EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                        .spawn(from)
                        .setup(from.copy().addY(8), 2.4, 1.8)
                        .setAlpha(0.8F)
                        .setMaxAge(30 + rand.nextInt(15));
            }

            if (this.getTick() >= (DURATION_CRYSTAL_ATTUNEMENT - 10)) {
                for (int i = 0; i < 25; i++) {
                    Vector3 at = Vector3.atBottomCenter(altar).addY(rand.nextFloat());

                    EntityVisualFX fx = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(at)
                            .color(FXColorFunction.WHITE)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setAlpha(0.75F)
                            .setScale(0.25F + rand.nextFloat() * 0.15F)
                            .setMotion(Vector3.random(rand).setY(0).normalize().multiply(0.025F + rand.nextFloat()  * 0.075F))
                            .setMaxAge(60 + rand.nextInt(40));

                    if (rand.nextBoolean()) {
                        fx.color(FXColorFunction.constant(cst.getConstellationColor()));
                    }
                }
            }
        }

        @OnlyIn(Dist.CLIENT)
        private void tickPlaySound(TileAttunementAltar altar) {
            if (this.attunementSound.isNull()) {
                PlayableSoundInstance attuneSound = PlayableSoundInstance.of(SoundsAS.ATTUNEMENT_ALTAR_ITEM_LOOP)
                        .pos(Vector3.atCenter(altar))
                        .volume(0.6F)
                        .loop(true)
                        .stopFunction(sound -> {
                            return !altar.canPlayConstellationEffects(altar.getLevel()) ||
                                    altar.getTileData().getActiveRecipe().isEmpty() ||
                                    altar.getTileData().getActiveRecipe().get().getRecipe() != ItemAttunementRecipe.INSTANCE;
                        })
                        .fadeInTicks(20)
                        .fadeOutTicks(80)
                        .play();
                this.attunementSound.set(attuneSound);
            }

            if (this.getTick() == 0) {
                PlayableSoundInstance.of(SoundsAS.ATTUNEMENT_ALTAR_ITEM_START)
                        .pos(Vector3.atCenter(altar))
                        .volume(0.75F)
                        .play();
            }
        }

        @Override
        public boolean isFinished(TileAttunementAltar altar) {
            return this.getTick() >= DURATION_CRYSTAL_ATTUNEMENT;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void stopEffects(TileAttunementAltar altar) {
            if (this.isFinished(altar)) {
                PlayableSoundInstance.of(SoundsAS.ATTUNEMENT_ALTAR_ITEM_FINISH)
                        .pos(altar.getBlockPos().above())
                        .volume(0.8F)
                        .pitch(1.25F)
                        .play();
            }

            this.attunementFX.ifPresent(VFXImmediateFacingSprite::requestRemoval);
            this.orbitalSource.ifPresent(FXItemAttunementOrbitalSource::requestRemoval);
        }

        public Optional<ItemEntity> getEntity(Level level) {
            Entity entity = level.getEntity(this.getItemId());
            if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive() && isEligibleItem(itemEntity)) {
                return Optional.of(itemEntity);
            }
            return Optional.empty();
        }

        private void updateEntityPosition(ItemEntity entity, BlockPos altarPos) {
            Vector3 crystalPos = Vector3.atBottomCenter(altarPos).addY(1.4F);

            entity.setNoGravity(true);
            entity.setPos(crystalPos.toVector3d());
            entity.xo = crystalPos.getX();
            entity.yo = crystalPos.getY();
            entity.zo = crystalPos.getZ();
            entity.setDeltaMovement(Vec3.ZERO);
        }

        public int getItemId() {
            return this.itemId;
        }

        @Override
        public void copyEffectDataTo(Active other) {
            super.copyEffectDataTo(other);

            other.attunementSound.set(this.attunementSound.get());
            other.attunementFX.set(this.attunementFX.get());
            other.orbitalSource.set(this.orbitalSource.get());
        }
    }
}
