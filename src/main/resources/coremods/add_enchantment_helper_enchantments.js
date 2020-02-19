function initializeCoreMod() {
    return {
        'add_enchantment_helper_enchantments': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.enchantment.EnchantmentHelper',
                'methodName': 'func_82781_a',
                'methodDesc': '(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_enchantment_helper_enchantments\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.ARETURN, 0);

                while (aReturn !== null) {
                    method.instructions.insertBefore(aReturn, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
                    method.instructions.insertBefore(aReturn, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'applyNewEnchantmentLevels',
                        '(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)Ljava/util/Map;',
                        ASMAPI.MethodType.STATIC));

                    aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.ARETURN, method.instructions.indexOf(aReturn) + 1);
                }

                ASMAPI.log('INFO', 'Added \'add_enchantment_helper_enchantments\' ASM patch!');
                return method;
            }
        }
    }
}