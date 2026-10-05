/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertyEvent
 * Created by HellFirePvP
 * Date: 05.10.2026 / 13:49
 */
public class CrystalPropertyEvent extends Event {

    private final ItemStack crystalStack;

    private CrystalPropertyEvent(ItemStack crystalStack) {
        this.crystalStack = crystalStack;
    }

    public ItemStack getCrystalStack() {
        return this.crystalStack.copy();
    }

    public static class Split extends CrystalPropertyEvent {

        private final Type type;
        private final CrystalAttributesComponent sourceComponent;
        private final List<CrystalAttributesComponent> originalResultComponents;
        private final List<CrystalAttributesComponent> resultComponents = new ArrayList<>();

        public Split(ItemStack crystalStack, Type type, CrystalAttributesComponent sourceComponent, List<CrystalAttributesComponent> resultComponents) {
            super(crystalStack);
            this.type = type;
            this.sourceComponent = sourceComponent;
            this.originalResultComponents = List.copyOf(resultComponents);
            this.resultComponents.addAll(resultComponents);
        }

        public Type getType() {
            return this.type;
        }

        public CrystalAttributesComponent getSourceComponent() {
            return this.sourceComponent;
        }

        public List<CrystalAttributesComponent> getOriginalResultComponents() {
            return this.originalResultComponents;
        }

        public List<CrystalAttributesComponent> getResultComponents() {
            return this.resultComponents;
        }

        public void setResultComponents(List<CrystalAttributesComponent> resultComponents) {
            this.resultComponents.clear();
            this.resultComponents.addAll(resultComponents);
        }

        public enum Type implements IExtensibleEnum {

            CHISEL_SPLIT_CRYSTAL;

            public static ExtensionInfo getExtensionInfo() {
                return ExtensionInfo.nonExtended(Type.class);
            }
        }
    }

    public static class Merge extends CrystalPropertyEvent {

        private final Type type;
        private final List<CrystalAttributesComponent> sourceComponents;
        private final CrystalAttributesComponent originalResultComponent;
        private CrystalAttributesComponent resultComponent;

        public Merge(ItemStack crystalStack, Type type, List<CrystalAttributesComponent> sourceComponents, CrystalAttributesComponent resultComponent) {
            super(crystalStack);
            this.type = type;
            this.sourceComponents = List.copyOf(sourceComponents);
            this.originalResultComponent = resultComponent;
            this.resultComponent = resultComponent;
        }

        public Type getType() {
            return this.type;
        }

        public List<CrystalAttributesComponent> getSourceComponents() {
            return this.sourceComponents;
        }

        public CrystalAttributesComponent getOriginalResultComponent() {
            return this.originalResultComponent;
        }

        public CrystalAttributesComponent getResultComponent() {
            return this.resultComponent;
        }

        public void setResultComponent(CrystalAttributesComponent resultComponent) {
            this.resultComponent = resultComponent;
        }

        public enum Type implements IExtensibleEnum {

            MERGE_CRYSTALS,
            ALTAR_RECIPE_MERGE;

            public static ExtensionInfo getExtensionInfo() {
                return ExtensionInfo.nonExtended(Type.class);
            }
        }
    }

    public static class Change extends CrystalPropertyEvent {

        private final Type type;
        private final CrystalAttributesComponent sourceComponent;
        private final CrystalAttributesComponent originalResultComponent;
        private CrystalAttributesComponent resultComponent;

        public Change(ItemStack crystalStack, Type type, CrystalAttributesComponent sourceComponent, CrystalAttributesComponent resultComponent) {
            super(crystalStack);
            this.type = type;
            this.sourceComponent = sourceComponent;
            this.originalResultComponent = resultComponent;
            this.resultComponent = resultComponent;
        }

        public Type getType() {
            return this.type;
        }

        public CrystalAttributesComponent getSourceComponent() {
            return this.sourceComponent;
        }

        public CrystalAttributesComponent getOriginalResultComponent() {
            return this.originalResultComponent;
        }

        public CrystalAttributesComponent getResultComponent() {
            return this.resultComponent;
        }

        public void setResultComponent(CrystalAttributesComponent resultComponent) {
            this.resultComponent = resultComponent;
        }

        public enum Type implements IExtensibleEnum {

            GROW_SIZE,
            FORM_CRYSTAL_CLUSTER;

            public static ExtensionInfo getExtensionInfo() {
                return ExtensionInfo.nonExtended(Type.class);
            }
        }
    }
}
