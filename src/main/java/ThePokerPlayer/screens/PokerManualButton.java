package ThePokerPlayer.screens;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.characters.ThePokerPlayer;
import ThePokerPlayer.patches.CurrentScreenEnum;
import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class PokerManualButton extends TopPanelItem {
	private static final Texture IMG = new Texture(PokerPlayerMod.makePath("ui/PokerManual.png"));
	public static final String ID = PokerPlayerMod.makeID("PokerManualButton");

	public PokerManualButton() {
		super(IMG, ID);
	}

	@Override
	protected void onClick() {
		if (AbstractDungeon.player instanceof ThePokerPlayer && !CardCrawlGame.isPopupOpen) {
			toggleScreen();
		}
	}

	private static void toggleScreen() {
		if (AbstractDungeon.screen == CurrentScreenEnum.POKER_MANUAL) {
			AbstractDungeon.closeCurrentScreen();
		} else {
			if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
				AbstractDungeon.closeCurrentScreen();
				AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
			} else if (!AbstractDungeon.isScreenUp) {
			} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
				if (AbstractDungeon.previousScreen != null) {
					AbstractDungeon.screenSwap = true;
				}

				AbstractDungeon.closeCurrentScreen();
			} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.DEATH) {
				AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
				AbstractDungeon.deathScreen.hide();
			} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) {
				AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.BOSS_REWARD;
				AbstractDungeon.bossRelicScreen.hide();
			} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP) {
				AbstractDungeon.overlayMenu.cancelButton.hide();
				AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.SHOP;
			} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && !AbstractDungeon.dungeonMapScreen.dismissable) {
				AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.MAP;
			} else if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.SETTINGS && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
				if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.INPUT_SETTINGS) {
					if (AbstractDungeon.previousScreen != null) {
						AbstractDungeon.screenSwap = true;
					}

					AbstractDungeon.closeCurrentScreen();
				} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD) {
					AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.CARD_REWARD;
					AbstractDungeon.dynamicBanner.hide();
				} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID) {
					AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.GRID;
					AbstractDungeon.gridSelectScreen.hide();
				} else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
					AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.HAND_SELECT;
				}
			} else {
				if (AbstractDungeon.previousScreen != null) {
					AbstractDungeon.screenSwap = true;
				}

				AbstractDungeon.closeCurrentScreen();
			}
			PokerPlayerMod.pokerManualScreen.open();
		}
		InputHelper.justClickedLeft = false;
	}

	@Override
	public void render(SpriteBatch sb) {
		if (AbstractDungeon.player instanceof ThePokerPlayer) {
			super.render(sb);
			if (hitbox.hovered) {
				float x = 1550.0F * Settings.scale;
				float y = (float) Settings.HEIGHT - 120.0F * Settings.scale;
				TipHelper.renderGenericTip(x, y, PokerManualScreen.TEXT[0], PokerManualScreen.TEXT[1]);
			}
		}
	}
}
