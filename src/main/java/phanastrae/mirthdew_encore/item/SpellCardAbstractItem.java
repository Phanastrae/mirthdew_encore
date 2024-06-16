package phanastrae.mirthdew_encore.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.ArrayList;
import java.util.List;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;
import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.SPELL_CARD;
import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.SPELL_DECK;

public abstract class SpellCardAbstractItem extends Item {
    public static final int MAX_CARD_STACK_SIZE = 32;

    public SpellCardAbstractItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack heldStack, Slot slot, ClickType clickType, PlayerEntity player) {
        ItemStack baseStack = slot.getStack();
        if(baseStack.isEmpty()) {
            if(clickType == ClickType.RIGHT) {
                // Right click over an empty slot to remove the bottom-most card from the stack
                SpellDeckContentsComponent spellDeckContentsComponent = heldStack.get(SPELL_DECK_CONTENTS);
                if(spellDeckContentsComponent != null) {
                    SpellDeckContentsComponent.Builder builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);

                    ItemStack removedStack = builder.removeStackFromBase();
                    if(removedStack != null) {
                        if(builder.isEmpty()) {
                            heldStack.set(SPELL_DECK_CONTENTS, null);
                        } else {
                            heldStack.set(SPELL_DECK_CONTENTS, builder.build());
                        }
                        slot.setStack(removedStack);

                        if(builder.isEmpty()) {
                            // if the held item is no longer a deck, try to convert it to a card
                            ScreenHandler screenHandler = player.currentScreenHandler;
                            if (screenHandler != null) {
                                ItemStack screenHandlerCursorStack = screenHandler.getCursorStack();
                                if (screenHandlerCursorStack == heldStack) {
                                    screenHandler.setCursorStack(getStackWithCorrectItem(heldStack));
                                }
                            }
                        }

                        this.playRemoveOneSound(player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onClicked(ItemStack baseStack, ItemStack heldStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if(heldStack.isEmpty()) {
            if (clickType == ClickType.RIGHT) {
                // Right click with an empty cursor to remove the top-most card from the stack
                SpellDeckContentsComponent spellDeckContentsComponent = baseStack.get(SPELL_DECK_CONTENTS);
                if (spellDeckContentsComponent != null) {
                    List<ItemStack> stackList = deckStackToStackList(baseStack);
                    if(!stackList.isEmpty()) {
                        cursorStackReference.set(stackList.getFirst());
                        stackList.removeFirst();

                        ItemStack remainingDeck = stackListToDeckStack(stackList);
                        slot.setStack(remainingDeck);
                        this.playRemoveOneSound(player);
                        return true;
                    }
                }
            }
        } else if(heldStack.getItem() instanceof SpellCardAbstractItem) {
            if(clickType == ClickType.LEFT) {
                int maxAcceptedCards = getMaxAcceptedCards(baseStack);
                if(maxAcceptedCards < 1) {
                    playInsertFailSound(player);
                    return true;
                } else {
                    List<ItemStack> heldStackList = deckStackToStackList(heldStack);
                    List<ItemStack> baseStackList = deckStackToStackList(baseStack);

                    int heldSize = heldStackList.size();
                    int removedCards = Math.min(maxAcceptedCards, heldSize);

                    List<ItemStack> newHeldStackList = heldStackList.subList(0, heldSize - removedCards);
                    List<ItemStack> newBaseStackList;
                    newBaseStackList = new ArrayList<>(heldStackList.subList(heldSize - removedCards, heldSize));
                    newBaseStackList.addAll(baseStackList);

                    cursorStackReference.set(stackListToDeckStack(newHeldStackList));
                    slot.setStack(stackListToDeckStack(newBaseStackList));

                    this.playRemoveOneSound(player);
                    this.playInsertSound(player);
                    return true;
                }
            } else if(clickType == ClickType.RIGHT) {
                // Right click with a Card/Deck in cursor to remove the bottom item of the Card/Deck and place it on top of the stack
                if(getMaxAcceptedCards(baseStack) < 1) {
                    playInsertFailSound(player);
                    return true;
                }

                SpellDeckContentsComponent spellDeckContentsComponent = heldStack.get(SPELL_DECK_CONTENTS);
                ItemStack removedStack;
                if(spellDeckContentsComponent == null || spellDeckContentsComponent.isEmpty()) {
                    removedStack = getStackWithCorrectItem(heldStack);
                    cursorStackReference.set(ItemStack.EMPTY.copy());
                } else {
                    SpellDeckContentsComponent.Builder builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);
                    removedStack = builder.removeStackFromBase();
                    heldStack.set(SPELL_DECK_CONTENTS, builder.build());
                    cursorStackReference.set(getStackWithCorrectItem(heldStack));
                }

                if(removedStack != null) {
                    slot.setStack(getMerged(removedStack, baseStack));
                }

                this.playRemoveOneSound(player);
                this.playInsertSound(player);
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> deckStackToStackList(ItemStack deckStack) {
        SpellDeckContentsComponent deckContents = deckStack.get(SPELL_DECK_CONTENTS);
        List<ItemStack> stackList = new ArrayList<>();
        if(deckContents == null) {
            stackList.add(deckStack);
        } else {
            ItemStack topStack = deckStack.copy();
            topStack.remove(SPELL_DECK_CONTENTS);
            stackList.add(getStackWithCorrectItem(topStack));
            for(ItemStack stack : deckContents.iterate()) {
                stackList.add(stack);
            }
        }
        return stackList;
    }

    public static ItemStack stackListToDeckStack(List<ItemStack> stackList) {
        if(stackList == null || stackList.isEmpty()) return ItemStack.EMPTY.copy();

        if(stackList.size() == 1) {
            return stackList.getFirst();
        } else {
            ItemStack topStack = stackList.getFirst().copy();
            SpellDeckContentsComponent deckContents = topStack.get(SPELL_DECK_CONTENTS);
            SpellDeckContentsComponent.Builder builder;
            if(deckContents == null) {
                builder = new SpellDeckContentsComponent.Builder();
            } else {
                builder = new SpellDeckContentsComponent.Builder(deckContents);
            }

            for(int i = 1; i < stackList.size(); i++) {
                builder.addStackToBase(stackList.get(i));
            }

            topStack.set(SPELL_DECK_CONTENTS, builder.build());
            return getStackWithCorrectItem(topStack);
        }
    }

    public static int getMaxAcceptedCards(ItemStack stack) {
        if(stack.isEmpty()) {
            return 0;
        } else if(stack.getItem() instanceof SpellCardAbstractItem) {
            SpellDeckContentsComponent deckContents = stack.get(SPELL_DECK_CONTENTS);
            if(deckContents == null) {
                return MAX_CARD_STACK_SIZE - 1;
            } else {
                int accepted = MAX_CARD_STACK_SIZE - 1 - deckContents.size();
                if(accepted < 0) accepted = 0;
                return accepted;
            }
        } else {
            return 0;
        }
    }

    public static ItemStack getMerged(ItemStack top, ItemStack bottom) {
        SpellDeckContentsComponent spellDeckContentsComponent = top.get(SPELL_DECK_CONTENTS);

        SpellDeckContentsComponent.Builder builder;
        if(spellDeckContentsComponent == null) {
            builder = new SpellDeckContentsComponent.Builder();
        } else {
            builder = new SpellDeckContentsComponent.Builder(spellDeckContentsComponent);
        }

        SpellDeckContentsComponent baseStackSpellDeckContentsComponent = bottom.get(SPELL_DECK_CONTENTS);
        if(baseStackSpellDeckContentsComponent == null) {
            builder.addStackToBase(bottom);
        } else {
            bottom.set(SPELL_DECK_CONTENTS, null);
            builder.addStackToBase(getStackWithCorrectItem(bottom));
            for(ItemStack itemStack : baseStackSpellDeckContentsComponent.iterate()) {
                builder.addStackToBase(itemStack);
            }
        }

        top.set(SPELL_DECK_CONTENTS, builder.build());
        return getStackWithCorrectItem(top);
    }

    public static ItemStack getStackWithCorrectItem(ItemStack stack) {
        // may return a copy of the original stack, or may just return a (potentially modified version of) the original stack

        if(stack.isOf(SPELL_CARD)) {
            SpellDeckContentsComponent spellDeckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
            if(spellDeckContentsComponent != null) {
                if(spellDeckContentsComponent.isEmpty()) {
                    stack.set(SPELL_DECK_CONTENTS, null);
                    return stack;
                } else {
                    ItemStack newStack = SPELL_DECK.getDefaultStack();
                    newStack.applyComponentsFrom(stack.getComponents());
                    return newStack;
                }
            }
        } else if(stack.isOf(SPELL_DECK)) {
            SpellDeckContentsComponent spellDeckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
            if(spellDeckContentsComponent == null || spellDeckContentsComponent.isEmpty()) {
                ItemStack newStack = SPELL_CARD.getDefaultStack();
                newStack.applyComponentsFrom(stack.getComponents());
                if(spellDeckContentsComponent != null) {
                    newStack.set(SPELL_DECK_CONTENTS, null);
                }
                return newStack;
            } else {
                return stack;
            }
        }

        return stack;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 1.0F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 1.0F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.3F + entity.getWorld().getRandom().nextFloat() * 0.3F);
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.mirthdew_encore.spell_card");
    }
}
