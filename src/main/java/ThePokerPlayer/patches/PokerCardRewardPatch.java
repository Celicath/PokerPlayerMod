package ThePokerPlayer.patches;

import ThePokerPlayer.cards.PokerCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.NlothsGift;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;

public class PokerCardRewardPatch {
	public static final int[] RATIO_RANK_NORMAL =
		new int[]{0, 20, 20, 20, 20, 10, 4, 2, 2, 1, 1};
	public static final int[] RATIO_RANK_NORMAL_NLOTH =
		new int[]{0, 16, 16, 16, 16, 10, 8, 6, 6, 3, 3};
	public static final int[] RATIO_RANK_ELITE =
		new int[]{0, 16, 16, 16, 16, 10, 8, 6, 6, 3, 3};
	public static final int[] RATIO_RANK_ELITE_NLOTH =
		new int[]{0, 11, 11, 11, 11, 10, 10, 9, 9, 9, 9};
	public static final int[] RATIO_RANK_BOSS =
		new int[]{0, 0, 0, 0, 0, 0, 0, 25, 25, 25, 25};
	public static final int[] RATIO_RANK_BOSS_NLOTH =
		new int[]{0, 0, 0, 0, 0, 0, 0, 25, 25, 25, 25};
	public static final int[] RATIO_SUIT = new int[]{1, 3, 2, 4};

	public static void replaceHalf(ArrayList<AbstractCard> list, boolean canDuplicate) {
		for (int pos = 0; pos < list.size(); pos++) {
			int value = AbstractDungeon.cardRng.random(1999);
			if (value < 1000) {
				continue;
			}
			int num = value % 1000;
			int suitNum = num % 10;
			int rankNum = num / 10;
			PokerCard.Suit suit = PokerCard.Suit.Diamond;
			int rank;
			for (int i = 0; i < 4; i++) {
				suit = PokerCard.Suit.values()[i];
				if (suitNum < RATIO_SUIT[i]) {
					break;
				}
				suitNum -= RATIO_SUIT[i];
			}

			int[] ratio = AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite ?
				(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_ELITE_NLOTH : RATIO_RANK_ELITE) :
				AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss ?
					(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_BOSS_NLOTH : RATIO_RANK_BOSS) :
					(AbstractDungeon.player.hasRelic(NlothsGift.ID) ? RATIO_RANK_NORMAL_NLOTH : RATIO_RANK_NORMAL);
			for (rank = 1; rank <= 10; rank++) {
				if (rankNum < ratio[rank]) {
					break;
				}
				rankNum -= ratio[rank];
			}
			boolean dup = false;
			if (!canDuplicate) {
				for (int i = 0; i < pos; i++) {
					AbstractCard c = list.get(i);
					if (c instanceof PokerCard) {
						PokerCard pc = (PokerCard) c;
						if (pc.suit == suit && pc.rank == rank) {
							dup = true;
							break;
						}
					}
				}
			}
			if (dup) {
				pos--;
			} else {
				list.set(pos, new PokerCard(suit, rank));
			}
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
	public static class RewardPatch {
		@SpirePostfixPatch
		public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
			if (AbstractDungeon.player.chosenClass == ThePokerPlayerEnum.THE_POKER_PLAYER) {
				replaceHalf(__result, false);
			}
			return __result;
		}
	}
}
