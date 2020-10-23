function initializeCoreMod() {
    return {
        'add_enchantment_condition': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.advancements.criterion.ItemPredicate',
                'methodName': 'func_192493_a',
                'methodDesc': '(Lnet/minecraft/item/ItemStack;)Z'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_enchantment_condition\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var callDeserializeEnchantmentList = ASMAPI.mapMethod('func_226652_a_');
                var methodCall = ASMAPI.findFirstMethodCallAfter(method,
                        ASMAPI.MethodType.STATIC,
                        'net/minecraft/enchantment/EnchantmentHelper',
                        callDeserializeEnchantmentList,
                        '(Lnet/minecraft/nbt/ListNBT;)Ljava/util/Map;',
                        0);

                while (methodCall !== null) {
                    method.instructions.insert(methodCall, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'applyNewEnchantmentLevels',
                        '(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)Ljava/util/Map;',
                        ASMAPI.MethodType.STATIC));
                    method.instructions.insert(methodCall, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack

                    methodCall = ASMAPI.findFirstMethodCallAfter(method,
                        ASMAPI.MethodType.STATIC,
                        'net/minecraft/enchantment/EnchantmentHelper',
                        callDeserializeEnchantmentList,
                        '(Lnet/minecraft/nbt/ListNBT;)Ljava/util/Map;',
                        method.instructions.indexOf(methodCall) + 1);
                }

                ASMAPI.log('INFO', 'Added \'add_enchantment_condition\' ASM patch!');
                return method;
            }
        }
    }
}