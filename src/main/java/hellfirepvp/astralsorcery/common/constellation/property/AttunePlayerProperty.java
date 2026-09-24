/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.property;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.perk.RootPerk;
import net.neoforged.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttunePlayerProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttunePlayerProperty extends ConstellationProperty<AttunePlayerProperty> {

    public static final Key<AttunePlayerProperty> KEY = new Key<>();
    private final Function<LogicalSide, Optional<RootPerk<?>>> rootPerkProvider;

    protected AttunePlayerProperty(BaseConstellation constellation, Function<LogicalSide, Optional<RootPerk<?>>> rootPerkProvider) {
        super(KEY, constellation);
        this.rootPerkProvider = rootPerkProvider;
    }

    public static Function<BaseConstellation, AttunePlayerProperty> of(Function<LogicalSide, Optional<RootPerk<?>>> rootPerkProvider) {
        return cst -> new AttunePlayerProperty(cst, rootPerkProvider);
    }

    public static Function<BaseConstellation, AttunePlayerProperty> defaultRoot() {
        return cst -> new AttunePlayerProperty(cst, side -> Optional.ofNullable(PerkTree.getInstance().getRootPerk(side, cst)));
    }

    public Optional<RootPerk<?>> getRootPerk(LogicalSide side) {
        return this.rootPerkProvider.apply(side);
    }

    public static Optional<RootPerk<?>> getRootPerk(BaseConstellation cst, LogicalSide side) {
        return getRootPerk(Optional.ofNullable(cst), side);
    }

    public static Optional<RootPerk<?>> getRootPerk(Optional<BaseConstellation> cstOpt, LogicalSide side) {
        return cstOpt.filter(cst -> cst.hasProperty(AttunePlayerProperty.KEY))
                .flatMap(cst -> cst.getPropertyOpt(AttunePlayerProperty.KEY))
                .flatMap(prop -> prop.getRootPerk(side));
    }
}
