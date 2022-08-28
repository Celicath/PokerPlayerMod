package ThePokerPlayer.powers;

import ThePokerPlayer.PokerPlayerMod;
import ThePokerPlayer.cards.PokerCard;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CardPrinterPower extends AbstractPower implements NonStackablePower {
	public AbstractCreature source;

	private static final String RAW_ID = "CardPrinterPower";
	public static final String POWER_ID = PokerPlayerMod.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
		ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 128)), 0, 0, 84, 84);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
		ImageMaster.loadImage(PokerPlayerMod.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	public CardPrinterPower(AbstractCreature owner, AbstractCreature source, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.type = PowerType.BUFF;
		this.isTurnBased = false;
		this.region128 = IMG128;
		this.region48 = IMG48;
		this.source = source;
	}

	@Override
	public void atStartOfTurn() {
		AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(
			new PokerCard(PokerCard.Suit.values()[AbstractDungeon.cardRandomRng.random(2) + 1], amount),
			1, true, true));
		this.flash();
	}

	@Override
	public void updateDescription() {
		this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
	}
}
