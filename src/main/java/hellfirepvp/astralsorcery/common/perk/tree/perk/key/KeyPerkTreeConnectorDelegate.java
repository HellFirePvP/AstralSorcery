/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.tree.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecProducts;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkTreeConnectorDelegate
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkTreeConnectorDelegate extends AttributeModifierPerk<AbstractPerk.Data> {

    protected static <T extends KeyPerkTreeConnectorDelegate> Products.P9<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>, Set<PerkAttributeConverter>, Set<PerkAttributeModifier>, Optional<ResourceLocation>> perkTreeConnectorDelegateFields(RecordCodecBuilder.Instance<T> instance) {
        return CodecProducts.and(perkModifierFields(instance),
                ResourceLocation.CODEC.optionalFieldOf("delegate_key").forGetter(KeyPerkTreeConnectorDelegate::getDelegateKey)
        );
    }
    public static final MapCodec<KeyPerkTreeConnectorDelegate> CODEC = RecordCodecBuilder.mapCodec(inst -> perkTreeConnectorDelegateFields(inst).apply(inst, KeyPerkTreeConnectorDelegate::new));
    public static final PerkType<KeyPerkTreeConnectorDelegate> TYPE =
            PerkType.of(KeyPerkTreeConnectorDelegate.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkTreeConnectorDelegate::new);

    @Nullable
    private ResourceLocation delegateKey;

    private KeyPerkTreeConnectorDelegate(ResourceLocation key, float x, float y) {
        super(key, defaultNameKey(key), x, y, PerkCategory.DEFAULT, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkTreeConnectorDelegate(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers, Optional<ResourceLocation> delegateKey) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
        this.delegateKey = delegateKey.orElse(null);
    }

    public <P extends KeyPerkTreeConnectorDelegate> P setDelegateKey(ResourceLocation delegateKey) {
        AstralSorcery.assertDataGeneration();
        this.delegateKey = delegateKey;
        return MiscUtil.cast(this);
    }

    public Optional<ResourceLocation> getDelegateKey() {
        return Optional.ofNullable(delegateKey);
    }

    public Optional<KeyPerkTreeConnector> getDelegate(LogicalSide side) {
        return this.getDelegateKey().flatMap(key -> PerkTree.getInstance().getPerk(side, key))
                .filter(perk -> perk instanceof KeyPerkTreeConnector)
                .map(perk -> (KeyPerkTreeConnector) perk);
    }

    public boolean isDelegateAllocated(LogicalSide side, PlayerPerkData perkData) {
        return this.getDelegate(side).map(delegate -> {
            return (perkData.hasPerkAllocation(delegate, PerkAllocationType.UNLOCKED) ||
                    perkData.hasPerkAllocation(delegate, PerkAllocationType.UNLOCKED_NON_CONNECT)) &&
                    perkData.hasPerkAllocation(this, PerkAllocationType.GRANTED_CONNECT);
        }).orElse(false);
    }

    @Override
    public Collection<AbstractPerk<?>> getAlwaysDependentPerks(PlayerProgress progress, LogicalSide side) {
        KeyPerkTreeConnector validDelegate = this.getDelegate(side)
                .filter(connector -> progress.getPerkData().hasPerkAllocationGrantingConnections(connector))
                .orElse(null);
        if (validDelegate == null) return Collections.emptySet();
        return Collections.singleton(validDelegate);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
