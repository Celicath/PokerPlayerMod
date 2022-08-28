package ThePokerPlayer.modules;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.actions.ShowdownAction;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static ThePokerPlayer.cards.PokerCard.SUIT_HEIGHT;
import static ThePokerPlayer.cards.PokerCard.SUIT_WIDTH;

public class PokerScoreViewer {
	private Hitbox hb;
	private Hitbox[] childHb;
	public static String[] TEXT = null;

	private static final int width = 240;
	private static final int height = 100;
	private static final int distance = 50;

	// dragging
	private int moveState = 0;
	private float dx;
	private float dy;
	private float startx;
	private float starty;

	public static String genericTip;

	public PokerScoreViewer() {
		hb = new Hitbox(width * Settings.scale, height * Settings.scale);
		hb.move(Settings.WIDTH / 2.0f, Settings.HEIGHT * 0.79f);
		childHb = new Hitbox[4];
		for (int i = 0; i < 4; i++) {
			float dx = Settings.scale * distance;
			childHb[i] = new Hitbox(dx, height * Settings.scale);
			childHb[i].move(hb.cX + dx * (i - 1.5f), hb.cY);
			PokerPlayerMod.slayTheRelicsHitboxes.add(childHb[i]);
		}
		UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(PokerPlayerMod.makeID("PokerScoreViewer"));
		TEXT = uiStrings.TEXT;
	}

	public void update() {
		ShowdownAction.calculateShowdown();

		hb.update();
		for (int i = 0; i < 4; i++) {
			childHb[i].update();
		}
	}

	public void dragUpdate() {
		if (InputHelper.justClickedLeft) {
			if (hb.hovered) {
				dx = hb.cX - InputHelper.mX;
				dy = hb.cY - InputHelper.mY;
				moveState = 1;
				startx = InputHelper.mX;
				starty = InputHelper.mY;
			}
		}

		if (moveState > 0) {
			if (InputHelper.justReleasedClickLeft) {
				moveState = 0;
			} else {
				float x = Math.min(Math.max(InputHelper.mX + dx, 0.05f * Settings.WIDTH), 0.95f * Settings.WIDTH);
				float y = Math.min(Math.max(InputHelper.mY + dy, 0.3f * Settings.HEIGHT), 0.85f * Settings.HEIGHT);

				if ((startx - InputHelper.mX) * (startx - InputHelper.mX) + (starty - InputHelper.mY) * (starty - InputHelper.mY) > 64) {
					moveState = 2;
				}

				if (moveState == 2) {
					hb.move(x, y);
					for (int i = 0; i < 4; i++) {
						childHb[i].move(hb.cX + (i - 1.5f) * distance * Settings.scale, hb.cY);
					}
				}
			}
		}
	}

	private static final float DIST = Settings.scale * 10.0f;
	private static final float HARDEN_DIST = Settings.scale * 25.0f;

	public void render(SpriteBatch sb) {

		for (int i = 0; i < PokerCard.Suit.values().length; i++) {
			float dx = Settings.scale * (distance * (i - 1.5f));
			sb.draw(
				PokerCard.Suit.values()[i].getImage(),
				hb.cX + dx - DIST,
				hb.cY,
				SUIT_WIDTH / 2.0f,
				SUIT_HEIGHT / 2.0f,
				SUIT_WIDTH,
				SUIT_HEIGHT,
				Settings.scale * 2,
				Settings.scale * 2,
				0, 0, 0, SUIT_WIDTH, SUIT_HEIGHT, false, false);
			FontHelper.renderFontCentered(
				sb,
				FontHelper.topPanelAmountFont,
				String.valueOf(ShowdownAction.powView[i]),
				hb.cX + dx + DIST,
				hb.cY,
				Settings.BLUE_TEXT_COLOR);
		}

		if (ShowdownAction.hardenCount > 0) {
			float dx = Settings.scale * (distance * (1 - 1.5f));
			FontHelper.renderFontCentered(
				sb,
				FontHelper.powerAmountFont,
				"+" + ShowdownAction.hardenCount,
				hb.cX + dx + DIST,
				hb.cY + HARDEN_DIST,
				Settings.GREEN_TEXT_COLOR);
		}

		if (this.hb.hovered) {
			int index = -1;
			for (int i = 0; i < 4; i++) {
				if (childHb[i].hovered) {
					index = i;
					break;
				}
			}
			if (index >= PokerPlayerMod.slayTheRelicsPowerTips.size()) {
				index = -1;
			}
			if (index == -1) {
				TipHelper.renderGenericTip(
					(float) InputHelper.mX + 50.0F * Settings.scale,
					(float) InputHelper.mY,
					TEXT[0],
					genericTip);
			} else {
				TipHelper.renderGenericTip(
					(float) InputHelper.mX + 50.0F * Settings.scale,
					(float) InputHelper.mY,
					TEXT[0],
					PokerPlayerMod.slayTheRelicsPowerTips.get(index).get(0).body);
			}
		}

		hb.render(sb);
		for (int i = 0; i < 4; i++) {
			childHb[i].render(sb);
		}
	}

	public void generateTips() {
		PokerPlayerMod.slayTheRelicsPowerTips.clear();

		genericTip = TEXT[1] + " NL " + TEXT[2];

		for (int i = 1; i <= ShowdownAction.FIVE_CARD; i++) {
			if (i == ShowdownAction.hand) {
				genericTip += " NL " + highlightedText(ShowdownAction.TEXT[i]) + " : #b+" + ShowdownAction.modifierByHand(i) + "%";
			} else {
				genericTip += " NL " + ShowdownAction.TEXT[i] + " : #b+" + ShowdownAction.modifierByHand(i) + "%";
			}
		}

		if (ShowdownAction.flush) {
			genericTip += " NL" + highlightedText(ShowdownAction.TEXT[ShowdownAction.FLUSH]) + " : #b+" + ShowdownAction.rawModifierBonus(ShowdownAction.FLUSH) + "%";
		} else {
			genericTip += " NL" + ShowdownAction.TEXT[ShowdownAction.FLUSH] + " : #b+" + ShowdownAction.rawModifierBonus(ShowdownAction.FLUSH) + "%";
		}

		for (int i = 0; i < 4; i++) {
			ArrayList<PowerTip> tooltips = new ArrayList<>();
			if (TEXT.length > 3) {
				String thisTip = TEXT[i * 2 + 3] + ShowdownAction.powView[i] + TEXT[i * 2 + 4];
				if (i == 1 && ShowdownAction.hardenCount > 0) {
					thisTip += " NL " + TEXT[11] + ShowdownAction.hardenCount + TEXT[12];
				}
				tooltips.add(new PowerTip(TEXT[0], genericTip + " NL NL " + thisTip));
			} else {
				tooltips.add(new PowerTip(TEXT[0], genericTip));
			}
			PokerPlayerMod.slayTheRelicsPowerTips.add(tooltips);
		}
	}

	public String highlightedText(String text) {
		return text.replaceAll("(?<=\\s|^)(?=\\S)", "#y");
	}
}
