package ThePokerPlayer.screens;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.PokerCard;
import ThePokerPlayer.patches.CurrentScreenEnum;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PokerManualScreen {
	private static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerManual"));
	public static String[] TEXT = uiStrings.TEXT;

	public static Texture cardTexture = new Texture(PokerPlayerMod.makePath("ui/ManualCard.png"));
	public static final int CARD_WIDTH = 62;
	public static final int CARD_HEIGHT = 74;

	public static final int[] numCardDraws = new int[]{2, 4, 3, 5, 5, 4, 5, 5};
	public int[][] exampleHands = new int[8][5];

	public PokerManualScreen() {
	}

	public void open() {
		AbstractDungeon.player.releaseCard();
		AbstractDungeon.screen = CurrentScreenEnum.POKER_MANUAL;
		AbstractDungeon.overlayMenu.showBlackScreen();
		AbstractDungeon.overlayMenu.proceedButton.hide();
		AbstractDungeon.overlayMenu.cancelButton.show(TEXT[13]);
		AbstractDungeon.isScreenUp = true;

		CardCrawlGame.sound.play(MathUtils.randomBoolean() ? "MAP_OPEN" : "MAP_OPEN_2", 0.1f);

		randomizePokerHands();
	}

	public void close() {
		AbstractDungeon.overlayMenu.cancelButton.hide();
		CardCrawlGame.sound.play(MathUtils.randomBoolean() ? "MAP_OPEN" : "MAP_OPEN_2", 0.1f);
	}

	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE);

		FontHelper.renderFontLeft(sb, FontHelper.panelNameFont, TEXT[2], 400 * Settings.scale, 850 * Settings.scale, Color.WHITE);
		FontHelper.renderFontLeft(sb, FontHelper.panelNameFont, TEXT[3], 400 * Settings.scale, 800 * Settings.scale, Color.WHITE);

		FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, TEXT[4], 720 * Settings.scale, 700 * Settings.scale, Color.WHITE);

		float scale = Settings.scale * 0.75f;
		for (int i = 0; i < 8; i++) {
			int index1 = i + 1;
			if (i == 7) index1++;
			float y = 610 * Settings.scale - (CARD_HEIGHT + 5) * scale * i;
			FontHelper.renderFontCentered(sb, FontHelper.cardDescFont_L, ShowdownAction.TEXT[index1], 480 * Settings.scale, y, Color.WHITE);

			for (int j = 0; j < numCardDraws[i]; j++) {
				float x = 720 * Settings.scale + (CARD_WIDTH + 2) * (j - 2) * scale;
				sb.draw(
						cardTexture,
						x - CARD_WIDTH / 2.0f,
						y - CARD_HEIGHT / 2.0f,
						CARD_WIDTH / 2.0f,
						CARD_HEIGHT / 2.0f,
						CARD_WIDTH,
						CARD_HEIGHT,
						scale,
						scale,
						0.0F,
						0,
						0,
						CARD_WIDTH,
						CARD_HEIGHT,
						false,
						false);

				if (i < 7) {
					String s = Integer.toString(exampleHands[i][j]);
					FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, s, x, y, Color.LIME);
				} else {
					sb.draw(
							PokerCard.SUIT_TO_IMG[exampleHands[i][j]],
							x - PokerCard.SUIT_WIDTH / 2.0f,
							y - PokerCard.SUIT_HEIGHT / 2.0f,
							PokerCard.SUIT_WIDTH / 2.0f,
							PokerCard.SUIT_HEIGHT / 2.0f,
							PokerCard.SUIT_WIDTH,
							PokerCard.SUIT_HEIGHT,
							Settings.scale * 2,
							Settings.scale * 2,
							0.0F,
							0,
							0,
							PokerCard.SUIT_WIDTH,
							PokerCard.SUIT_HEIGHT,
							false,
							false);
				}
			}
			FontHelper.renderFontLeft(sb, FontHelper.cardDescFont_L, TEXT[i + 5], 900 * Settings.scale, y, Color.YELLOW);
		}
	}

	public void randomizePokerHands() {
		exampleHands[0][0] = exampleHands[0][1] = MathUtils.random(1, 10);
		exampleHands[1][0] = exampleHands[1][1] = MathUtils.random(1, 10);
		exampleHands[1][2] = exampleHands[1][3] = MathUtils.random(1, 9);
		if (exampleHands[1][2] == exampleHands[1][0]) {
			exampleHands[1][2] = exampleHands[1][3] = 10;
		}
		exampleHands[2][0] = exampleHands[2][1] = exampleHands[2][2] = MathUtils.random(1, 10);
		exampleHands[3][0] = MathUtils.random(1, 6);
		for (int i = 1; i < 5; i++) {
			exampleHands[3][i] = exampleHands[3][i - 1] + 1;
		}
		exampleHands[4][0] = exampleHands[4][1] = exampleHands[4][2] = MathUtils.random(1, 10);
		exampleHands[4][3] = exampleHands[4][4] = MathUtils.random(1, 9);
		if (exampleHands[4][3] == exampleHands[4][0]) {
			exampleHands[4][3] = exampleHands[4][4] = 10;
		}
		exampleHands[5][0] = exampleHands[5][1] = exampleHands[5][2] = exampleHands[5][3] = MathUtils.random(1, 10);
		exampleHands[6][0] = exampleHands[6][1] = exampleHands[6][2] = exampleHands[6][3] = exampleHands[6][4] = MathUtils.random(1, 10);
		exampleHands[7][0] = exampleHands[7][1] = exampleHands[7][2] = exampleHands[7][3] = exampleHands[7][4] = MathUtils.random(0, 3);
	}
}
