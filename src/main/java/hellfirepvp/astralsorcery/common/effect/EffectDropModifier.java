/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectDropModifier
 * Created by HellFirePvP
 * Date: 26.08.2019 / 20:08
 */
public class EffectDropModifier extends EffectCustomTexture {

    public EffectDropModifier() {
        super(EffectType.BENEFICIAL, ColorsAS.EFFECT_DROP_MODIFIER);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>(0);
    }

    @Override
    public void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(EventPriority.HIGH, this::onDrops);
    }

    private void onDrops(LivingDropsEvent event) {
        LivingEntity le = event.getEntityLiving();
        if (le.getEntityWorld().isRemote() ||
                !(le instanceof MobEntity) ||
                !(le.getEntityWorld() instanceof ServerWorld) ||
                !le.getEntityWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            return;
        }

        if (le.isPotionActive(EffectsAS.EFFECT_DROP_MODIFIER)) {
            ServerWorld sw = (ServerWorld) le.getEntityWorld();
            DamageSource src = event.getSource();
            MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);

            int amplifier = le.removeActivePotionEffect(EffectsAS.EFFECT_DROP_MODIFIER).getAmplifier();
            if (amplifier == 0) {
                event.getDrops().clear(); //Special case to void all items
            } else {
                ResourceLocation ltName = le.func_213346_cF();
                LootTable loottable = srv.getLootTableManager().getLootTableFromLocation(ltName);
                LootContext.Builder builder = new LootContext.Builder(sw)
                        .withRandom(rand)
                        .withParameter(LootParameters.THIS_ENTITY, le)
                        .withParameter(LootParameters.POSITION, new BlockPos(le))
                        .withParameter(LootParameters.DAMAGE_SOURCE, src)
                        .withNullableParameter(LootParameters.KILLER_ENTITY, src.getTrueSource())
                        .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, src.getImmediateSource());
                if (event.isRecentlyHit()) {
                    LivingEntity attack = le.getAttackingEntity();
                    if (attack instanceof PlayerEntity) {
                        builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity) attack)
                                .withLuck(((PlayerEntity) attack).getLuck());
                    }
                }
                for (int i = 0; i < amplifier; i++) {
                    for (ItemStack stack : loottable.generate(builder.build(LootParameterSets.ENTITY))) {
                        if (stack.isEmpty()) {
                            continue;
                        }

                        event.getDrops().add(le.entityDropItem(stack));
                    }
                }
            }
        }
    }

    @Override
    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.GUI, "effect_drop_modifier", 1, 1);
    }
}
