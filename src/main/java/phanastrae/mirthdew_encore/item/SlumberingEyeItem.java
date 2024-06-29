package phanastrae.mirthdew_encore.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.component.type.LocationComponent;

import java.util.List;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT;

public class SlumberingEyeItem extends Item {
    public SlumberingEyeItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (stack.contains(LOCATION_COMPONENT)) {
            return true;
        } else {
            return super.hasGlint(stack);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(stack.contains(LOCATION_COMPONENT)) {
            return super.use(world, user, hand);
        } else {
            if(!world.isClient()) {
                Vec3d pos = user.isSneaking() ? user.getPos() : user.getBlockPos().toBottomCenterPos();
                stack.set(LOCATION_COMPONENT, LocationComponent.fromPosAndWorld(pos, user.getWorld()));
            }

            return TypedActionResult.success(stack, world.isClient());
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        LocationComponent locationComponent = stack.get(LOCATION_COMPONENT);
        if(locationComponent != null) {
            tooltip.add(Text.translatable("item.mirthdew_encore.slumbering_eye.bound").copy().formatted(Formatting.LIGHT_PURPLE));

            Text xyz = Text.translatable("item.mirthdew_encore.slumbering_eye.bound.to.xyz", locationComponent.x(), locationComponent.y(), locationComponent.z()).formatted(Formatting.WHITE);
            Text dimension = Text.of(locationComponent.dimensionId()).copy().formatted(Formatting.WHITE);

            tooltip.add(Text.translatable("item.mirthdew_encore.slumbering_eye.bound.to", xyz).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.mirthdew_encore.slumbering_eye.bound.in", dimension).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("item.mirthdew_encore.slumbering_eye.unbound").copy().formatted(Formatting.GRAY));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }
}
