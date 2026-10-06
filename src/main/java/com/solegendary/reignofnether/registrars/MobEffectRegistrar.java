package com.solegendary.reignofnether.registrars;



import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.unit.MyMobEffect;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;


public class MobEffectRegistrar {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ReignOfNether.MOD_ID);

    // Prevents any actions or movement from happening
    public static final DeferredHolder<MobEffect, MobEffect> STUN = MOB_EFFECTS.register("stun",  () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFFFFFF)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "3fd2b186-9aab-4018-88a9-c150d2f6862c"), -1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "126163a9-2ae8-4aff-96f2-2b15c9c0fb55"), -1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // Similar to STUN but also prevents the mob from being knocked back or pushed
    public static final DeferredHolder<MobEffect, MobEffect> FREEZE = MOB_EFFECTS.register("freeze",  () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x000000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "aa19485b-837c-4cd5-91f3-440e05d60ba0"), -1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "de7be626-0f4e-4954-9c11-ec5539d40dd7"), -1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // Prevents players from issuing any new commands
    // usually used in conjunction with a force-attack command for a taunt effect, or a move command for a fear effect
    public static final DeferredHolder<MobEffect, MobEffect> UNCONTROLLABLE = MOB_EFFECTS.register("uncontrollable", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    public static final DeferredHolder<MobEffect, MobEffect> ANGRY = MOB_EFFECTS.register("angry", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    public static final DeferredHolder<MobEffect, MobEffect> FEARFUL = MOB_EFFECTS.register("scared", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x0000FF));

    public static final DeferredHolder<MobEffect, MobEffect> PARTIALLY_POSSESSED = MOB_EFFECTS.register("partially_possessed", () -> new MyMobEffect(MobEffectCategory.HARMFUL, 0x1A001A)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "34787f7c-2718-415f-b15d-9c22ab6d7e84"), -0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    // Causes a mob to turn into a zombie villager, drowned, zombie piglin or zoglin upon death depending on the unit type
    public static final DeferredHolder<MobEffect, MobEffect> ZOMBIE_INFECTED = MOB_EFFECTS.register("zombie_infected", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static final DeferredHolder<MobEffect, MobEffect> SLIME_INFECTED = MOB_EFFECTS.register("slime_infected", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static final DeferredHolder<MobEffect, MobEffect> MINOR_MOVEMENT_SPEED = MOB_EFFECTS.register("minor_speed", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 3402751)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "e6b9720b-131d-4c17-b029-ab8161e8da97"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final DeferredHolder<MobEffect, MobEffect> MINOR_MOVEMENT_SLOWDOWN = MOB_EFFECTS.register("minor_slowdown", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "eb256076-43e6-470e-a907-434a389da860"), -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    // Increases damage taken by units, and can cause negative armour (does not affect players)
    // The LUCK modifier is just a placeholder
    public static final DeferredHolder<MobEffect, MobEffect> DAMAGE_TAKEN_INCREASE = MOB_EFFECTS.register("damage_taken_increase", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.LUCK, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "e0772108-0408-4fa3-ad55-f90f5595d610"), -0.05, AttributeModifier.Operation.ADD_VALUE));

    // Causes a unit to take 2x fire and magma damage, and spreads it to another friendly mob if it dies
    // Higher amplifiers increase the duration of the passed effect
    public static final DeferredHolder<MobEffect, MobEffect> SCORCHING_FIRE = MOB_EFFECTS.register("scorching_fire", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFC6203));
    //.addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "06d218a6-4328-4df1-8263-5dfc23f0c65c"), -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    // ticks fire damage faster
    public static final DeferredHolder<MobEffect, MobEffect> INTENSE_HEAT = MOB_EFFECTS.register("intense_heat", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFC6203));

    // causes fire tick damage to be doubled and renders fire on entities as blue
    // also causes wildfires and blazes to render as soulfire variants
    public static final DeferredHolder<MobEffect, MobEffect> SOULS_AFLAME = MOB_EFFECTS.register("souls_aflame", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x4287f5));

    public static final DeferredHolder<MobEffect, MobEffect> ATTACK_SLOWDOWN = MOB_EFFECTS.register("attack_slowdown", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "95086ec9-c6cc-41b4-a2ce-9b5cf28011e4"), -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    // Used to give workers temporary efficiency effects (faster gathering and build speed)
    public static final DeferredHolder<MobEffect, MobEffect> TEMPORARY_EFFICIENCY = MOB_EFFECTS.register("temporary_efficiency", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x68FF52)
            .addAttributeModifier(Attributes.LUCK, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "a417cf34-dc4e-4047-8e14-89eece60c2f8"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final DeferredHolder<MobEffect, MobEffect> BLOODLUST = MOB_EFFECTS.register("bloodlust", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "b9da3d7f-da19-4860-9daa-328be5911517"), 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "52f887cb-3048-44fc-b176-98314b5467bd"), 0.60, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // Causes a unit to take 1dmg/s per layer of Wraith snow they're standing on
    public static final DeferredHolder<MobEffect, MobEffect> FROST_DAMAGE = MOB_EFFECTS.register("frost_damage", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751));

    // Doubles the effect of all unit enchantments (does not affect players)
    public static final DeferredHolder<MobEffect, MobEffect> ENCHANTMENT_AMPLIFIER = MOB_EFFECTS.register("enchantment_amplifier", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x000000));

    public static final DeferredHolder<MobEffect, MobEffect> DISARM = MOB_EFFECTS.register("disarm", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "a5faf34d-0155-49cf-9c6e-73f16ad41a42"), -1.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    // for striders to maintain visuals slightly off terrain
    public static final DeferredHolder<MobEffect, MobEffect> WARM = MOB_EFFECTS.register("warm", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));

    // cooldown reduction
    public static final DeferredHolder<MobEffect, MobEffect> VIGOR = MOB_EFFECTS.register("vigor", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x0000FF));

    // no knockback taken or received, not pushable
    public static final DeferredHolder<MobEffect, MobEffect> PHASING = MOB_EFFECTS.register("phasing", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x5d105e)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "709b8a00-a856-4161-b32a-32c4970c1490"), 1.0f, AttributeModifier.Operation.ADD_VALUE));

    // temporary night zone
    public static final DeferredHolder<MobEffect, MobEffect> NIGHT_WARPING = MOB_EFFECTS.register("night_warping", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x000000));

    public static final DeferredHolder<MobEffect, MobEffect> COLD = MOB_EFFECTS.register("cold", () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 3402751)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "14dfe523-ff36-4744-9ba3-41937d0b8eb5"), -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "0278ceb0-fc3a-456d-b40a-14be53a1aed6"), -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final DeferredHolder<MobEffect, MobEffect> INVINCIBLE = MOB_EFFECTS.register("invincible", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 5559980)
            .addAttributeModifier(Attributes.ARMOR, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "f16f1e7e-3f89-4ac8-b918-621366dc13bc"), 100, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(AttributeRegistrar.MAGIC_DAMAGE_RESIST, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "4c9d4fd6-6354-420c-a21b-4b6810f2d07b"), 100, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("reignofnether", "f89ff503-070c-4a85-9512-44fe0ec3b153"), 1, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> VILLAGER_INSPIRATION = MOB_EFFECTS.register("villager_inspiration", () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x66ffcc));

    // visual only
    public static final DeferredHolder<MobEffect, MobEffect> LIMITED_LIFESPAN = MOB_EFFECTS.register("limited_lifespan", () -> new InstantenousMobEffect(MobEffectCategory.NEUTRAL, 0xffffff));

    public static boolean isInterrupt(Holder<MobEffect> mobEffect) {
        return mobEffect.value() == STUN.get() ||
                mobEffect.value() == UNCONTROLLABLE.get();
    }

    public static void init(IEventBus bus) {
        MOB_EFFECTS.register(bus);
    }
}