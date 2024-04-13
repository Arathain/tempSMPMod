package arathain.mason.item;

import arathain.mason.init.MasonObjects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class GlaiveItem extends SwordItem {
    private final float attackDamage;
    public final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    public GlaiveItem(int attackDamage, float attackSpeed, Settings settings) {
        super(ToolMaterials.NETHERITE, attackDamage, attackSpeed, settings);
        this.attackDamage = ToolMaterials.NETHERITE.getAttackDamage() + attackDamage;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        builder.put(ReachEntityAttributes.REACH, new EntityAttributeModifier("Attack range", 1.2D, EntityAttributeModifier.Operation.ADDITION));
        builder.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier("Attack range", 1.2D, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("It is the lament of the fallen").formatted(Formatting.GOLD, Formatting.ITALIC));
        tooltip.add(Text.literal("which pushes the living onward.").formatted(Formatting.GOLD, Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(attacker instanceof PlayerEntity player) {
            player.spawnSweepAttackParticles();
            target.damage(target.getWorld().getDamageSources().create(MasonObjects.SOUL_RIP_DMG_TYPE, player), this.attackDamage);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if(!player.getItemCooldownManager().isCoolingDown(MasonObjects.GLAIVE)) {
            float yaw = player.getYaw() * 0.017453292F;
            Vec3d pos = player.getPos().add(-MathHelper.sin(yaw) * 1.4D, player.getHeight() / 2D, MathHelper.cos(yaw) * 1.4D);
            List<LivingEntity> targets = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.from(pos).offset(-0.5D, -0.5D, -0.5D).expand(3D, 1D, 3D), EntityPredicates.EXCEPT_SPECTATOR);
            stack.damage(1, player, entity -> entity.sendEquipmentBreakStatus(hand.equals(Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));

            targets.forEach(target -> {
                if (target != player && player.squaredDistanceTo(target) > 6.0 && player.squaredDistanceTo(target) < 36.0) {
                    if(!(target instanceof ArmorStandEntity)) {
                        target.takeKnockback(0.4D, MathHelper.sin(player.getYaw() * 0.0175F), -MathHelper.cos(player.getYaw() * 0.0175F));
                    }
                    if(world.isClient) {
                        Random r = new Random();
                        for(int i = 0; i < 8;  i++) {
                            world.addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME, target.getParticleX(0.5), target.getRandomBodyY(), target.getParticleZ(0.5), (r.nextFloat() - 0.5f) * 0.5f, r.nextFloat()* 0.5f, (r.nextFloat() - 0.5f) * 0.5f);
                        }
                    }
                    target.damage(player.getWorld().getDamageSources().create(MasonObjects.SOUL_RIP_DMG_TYPE, player), this.attackDamage);
                }
            });
            player.getWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1F, 1F);
            player.swingHand(hand);
            spawnSweepAttackParticles(player);
            player.getItemCooldownManager().set(MasonObjects.GLAIVE, 35);
            return TypedActionResult.success(stack);
        }
        return super.use(world, player, hand);

    }
    private void spawnSweepAttackParticles(PlayerEntity player) {
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            for(int i = 0; i <= 6; i++) {
                double d = -MathHelper.sin((player.getYaw() + i*20 - 60) * ((float)Math.PI / 180)) * 3;
                double e = MathHelper.cos((player.getYaw() + i*20 - 60) * ((float)Math.PI / 180)) * 3;
                (serverWorld).spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getBodyY(0.5), player.getZ() + e, 0, d, 0.0, e, 0.0);
            }
        }
    }
}
