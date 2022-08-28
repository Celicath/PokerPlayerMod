package ThePokerPlayer.patches;

import ThePokerPlayer.actions.PokerCardDiscoveryAction;
import ThePokerPlayer.cards.FakeCards.BrokenClockChoice;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.relics.BrokenClock;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

public class DiscoveryPatch {
	private static float CARD_TARGET_Y = (float) Settings.HEIGHT * 0.45F;

	public static PokerCard getRandomPokerCard() {
		return new PokerCard(
			PokerCardDiscoveryAction.suit == null ?
				PokerCard.Suit.values()[AbstractDungeon.cardRandomRng.random(2) + 1] :
				PokerCardDiscoveryAction.suit,
			AbstractDungeon.cardRandomRng.random(PokerCardDiscoveryAction.min, PokerCardDiscoveryAction.max),
			PokerCardDiscoveryAction.ethereal);
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "discoveryOpen", paramtypez = {})
	public static class DiscoveryOptionCountPatch {
		@SpireInsertPatch(locator = OptionCountLocator.class, localvars = {"derp"})
		public static void Insert(CardRewardScreen __instance, ArrayList derp) {
			if (PokerCardDiscoveryAction.isActive) {
				derp.clear();
				while (derp.size() < PokerCardDiscoveryAction.choices) {
					boolean dupe = false;
					AbstractCard tmp = getRandomPokerCard();

					for (Object obj : derp) {
						AbstractCard c = (AbstractCard) obj;
						if (c.cardID.equals(tmp.cardID)) {
							dupe = true;
							break;
						}
					}

					if (!dupe) {
						derp.add(tmp.makeCopy());
					}
				}
				if (AbstractDungeon.player.hasRelic(BrokenClock.ID)) {
					AbstractDungeon.player.getRelic(BrokenClock.ID).flash();
					derp.add(new BrokenClockChoice());
				}
			}
		}
	}

	private static class OptionCountLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardRewardScreen.class, "rewardGroup");
			final int[] all = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			return new int[]{all[0]};
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "placeCards")
	public static class DiscoveryPositionPatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance, float x, float y) {
			if (PokerCardDiscoveryAction.isActive) {
				int len = __instance.rewardGroup.size();
				if (len >= 5) {
					for (int i = 0; i < __instance.rewardGroup.size(); i++) {
						__instance.rewardGroup.get(i).current_x = x;
					}
				}
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "update")
	public static class DiscoveryHandUpdatePatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance) {
			if (PokerCardDiscoveryAction.isActive) {
				AbstractDungeon.player.hand.update();
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "render")
	public static class DiscoveryHandVisiblePatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance, SpriteBatch sb) {
			if (PokerCardDiscoveryAction.isActive) {
				AbstractDungeon.player.hand.render(sb);
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "shouldShowScrollBar")
	public static class DiscoveryDisableScrollBarPatch {
		@SpirePostfixPatch
		public static boolean Postfix(boolean __result, CardRewardScreen __instance) {
			if (PokerCardDiscoveryAction.isActive) {
				return false;
			}
			return __result;
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
	public static class DiscoveryDisableCardScrollPatch {
		@SpirePostfixPatch
		public static void Postfix(CardRewardScreen __instance, SpriteBatch sb) {
			if (PokerCardDiscoveryAction.isActive) {
				int len = __instance.rewardGroup.size();
				if (len >= 5) {
					final float PAD_X = (330.0F - len * 60.0f) * Settings.scale;
					for (int i = 0; i < len; i++) {
						__instance.rewardGroup.get(i).target_x = Settings.WIDTH / 2.0F + (AbstractCard.IMG_WIDTH + PAD_X) * (i - (len / 2.0F - 0.5F));
						__instance.rewardGroup.get(i).target_y = CARD_TARGET_Y;
					}
				}
			}
		}
	}
}
