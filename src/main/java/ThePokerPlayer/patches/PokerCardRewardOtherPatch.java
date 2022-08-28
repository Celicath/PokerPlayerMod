package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

public class PokerCardRewardOtherPatch {
	@SpirePatch(clz = TheLibrary.class, method = "buttonEffect")
	public static class TheLibraryPatch {
		@SpireInsertPatch(locator = LibraryLocator.class, localvars = {"group"})
		public static void Insert(TheLibrary __instance, int buttonPressed, CardGroup group) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				for (int i = 0; i < 10; i++) {
					PokerCard pc = null;
					boolean dup = true;
					while (dup) {
						dup = false;
						int num = AbstractDungeon.cardRng.random(39);
						PokerCard.Suit suit = PokerCard.Suit.values()[num / 10];
						int rank = num % 10 + 1;
						pc = new PokerCard(suit, rank);
						for (int j = 0; j < i; j++) {
							if (group.group.get(j).cardID.equals(pc.cardID)) {
								dup = true;
								break;
							}
						}
					}
					group.group.set(i, pc);
				}
			}
		}
	}

	public static class LibraryLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardGroup.class, "group");
			int[] result = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{result[1]};
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "draftOpen")
	public static class DraftPatch {
		@SpireInsertPatch(locator = DraftLocator.class, localvars = {"derp"})
		public static void Insert(CardRewardScreen __instance, ArrayList<AbstractCard> derp) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				PokerCardRewardPatch.replaceHalf(derp, false);
			}
		}
	}

	public static class DraftLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardRewardScreen.class, "rewardGroup");
			int[] result = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{result[0]};
		}
	}

	@SpirePatch(clz = NeowEvent.class, method = "dailyBlessing")
	public static class SealedDeckPatch {
		@SpireInsertPatch(locator = SealedDeckLocator.class, localvars = {"sealedGroup"})
		public static void Insert(NeowEvent __instance, CardGroup sealedGroup) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				PokerCardRewardPatch.replaceHalf(sealedGroup.group, true);
			}
		}
	}

	public static class SealedDeckLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "open");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(
		clz = AbstractDungeon.class,
		method = "returnTrulyRandomCardFromAvailable",
		paramtypez = {
			AbstractCard.class,
			Random.class
		})
	public static class TransformPatch {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result, AbstractCard prohibited, Random rng) {
			if (prohibited.color == CardColorEnum.POKER_PLAYER_GRAY) {
				int num = rng.random(79);
				if (num < 40) {
					PokerCard.Suit suit = PokerCard.Suit.values()[num / 10];
					int rank = num % 10 + 1;
					__result = new PokerCard(suit, rank);
				}
			}
			return __result;
		}
	}

	@SpirePatch(cls = "chronometry.patches.StartGamePatch", method = "chooseCards", optional = true)
	public static class SlayTheStreamerPatch {
		@SpireInsertPatch(locator = SealedDeckLocator.class, localvars = {"sealedGroup"})
		public static void Insert(NeowEvent self, CardGroup sealedGroup) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				PokerCardRewardPatch.replaceHalf(sealedGroup.group, true);
			}
		}
	}
}
