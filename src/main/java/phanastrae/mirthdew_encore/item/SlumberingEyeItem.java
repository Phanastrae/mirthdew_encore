package phanastrae.mirthdew_encore.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.LocationComponent;

public class SlumberingEyeItem extends Item {
    public SlumberingEyeItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (stack.contains(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
            return true;
        } else {
            return super.hasGlint(stack);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(stack.contains(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT)) {
            return super.use(world, user, hand);
        } else {
            if(!world.isClient()) {
                stack.set(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT, LocationComponent.fromPosAndWorld(user.getPos(), user.getWorld()));
            }

            return TypedActionResult.success(stack, world.isClient());
        }
    }
}
