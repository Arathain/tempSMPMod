package arathain.mason.item;

import arathain.mason.entity.AnimatedStatueEntity;
import arathain.mason.entity.BoneflyEntity;
import arathain.mason.init.MasonObjects;
import gg.moonflower.mannequins.common.entity.Statue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SoultrapEffigyItem extends Item {
    public SoultrapEffigyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(entity instanceof PlayerEntity player) {
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(0);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient && user.getItemCooldownManager().getCooldownProgress(this, 0) == 0) {
            for (Entity entity : world.getOtherEntities(user, new Box(user.getBlockPos()).expand(50), entity -> entity instanceof Statue)) {
                Statue statue = (Statue) entity;
                AnimatedStatueEntity animStatue = new AnimatedStatueEntity(MasonObjects.STATUE, world);
                if(statue.getExpression().isPresent()) {
                    animStatue.setExpression(statue.getExpression().get());
                }
                ((ServerWorld)world)
                        .spawnParticles(
                                statue.getParticle(),
                                statue.getX(),
                                statue.getBodyY(0.6666666666666666),
                                statue.getZ(),
                                10,
                                (double)(statue.getWidth() / 4.0F),
                                (double)(statue.getHeight() / 4.0F),
                                (double)(statue.getWidth() / 4.0F),
                                0.05
                        );
                animStatue.setBodyPose(statue.getBodyPose());
                animStatue.setHeadPose(statue.getHeadPose());
                animStatue.setTrolled(statue.isTrolled());
                animStatue.setLeftArmPose(statue.getLeftArmPose());
                animStatue.setRightArmPose(statue.getRightArmPose());
                animStatue.equipStack(EquipmentSlot.HEAD, statue.getEquippedStack(EquipmentSlot.HEAD));
                animStatue.equipStack(EquipmentSlot.MAINHAND, statue.getEquippedStack(EquipmentSlot.MAINHAND));
                animStatue.equipStack(EquipmentSlot.OFFHAND, statue.getEquippedStack(EquipmentSlot.OFFHAND));
                animStatue.equipStack(EquipmentSlot.CHEST, statue.getEquippedStack(EquipmentSlot.CHEST));
                animStatue.setOwner(user);
                animStatue.setDormantPos(statue.getBlockPos());
                animStatue.refreshPositionAndAngles(statue.getBlockPos(), statue.getYaw(), statue.getPitch());
                animStatue.setDormantYaw(statue.getYaw());
                world.spawnEntity(animStatue);
                statue.remove(Entity.RemovalReason.DISCARDED);
            }
            user.getItemCooldownManager().set(this, 600);
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
