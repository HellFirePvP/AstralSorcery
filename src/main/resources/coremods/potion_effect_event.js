function initializeCoreMod() {
    return {
        'potion_effect_event': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.LivingEntity',
                'methodName': 'func_195064_c',
                'methodDesc': '(Lnet/minecraft/potion/EffectInstance;)Z'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'potion_effect_event\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var onNewPotionEffectName = ASMAPI.mapMethod('func_70670_a');
                var onNewPotionEffect = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/entity/LivingEntity',
                        onNewPotionEffectName,
                        '(Lnet/minecraft/potion/EffectInstance;)V');

                method.instructions.insert(onNewPotionEffect, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'fireNewPotionEffectEvent',
                    '(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/potion/EffectInstance;)V',
                    ASMAPI.MethodType.STATIC));
                method.instructions.insert(onNewPotionEffect, new VarInsnNode(Opcodes.ALOAD, 1)); //passed potion effect
                method.instructions.insert(onNewPotionEffect, new VarInsnNode(Opcodes.ALOAD, 0));


                var onChangedPotionEffectName = ASMAPI.mapMethod('func_70695_b');
                var onChangedPotionEffect = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/entity/LivingEntity',
                        onChangedPotionEffectName,
                        '(Lnet/minecraft/potion/EffectInstance;Z)V');

                method.instructions.insert(onChangedPotionEffect, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'fireChangedPotionEffectEvent',
                    '(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/potion/EffectInstance;Lnet/minecraft/potion/EffectInstance;)V',
                    ASMAPI.MethodType.STATIC));
                method.instructions.insert(onChangedPotionEffect, new VarInsnNode(Opcodes.ALOAD, 2)); //new combined potion effect
                method.instructions.insert(onChangedPotionEffect, new VarInsnNode(Opcodes.ALOAD, 1)); //passed potion effect
                method.instructions.insert(onChangedPotionEffect, new VarInsnNode(Opcodes.ALOAD, 0));

                ASMAPI.log('INFO', 'Added \'potion_effect_event\' ASM patch!');
                return method;
            }
        }
    }
}