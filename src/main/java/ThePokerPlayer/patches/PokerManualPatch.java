package ThePokerPlayer.patches;

import ThePokerPlayer.PokerPlayerMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PokerManualPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
	public static class CloseManual {
		public static void Prefix() {
			if (AbstractDungeon.screen == CurrentScreenEnum.POKER_MANUAL) {
				try {
					Method overlayReset = AbstractDungeon.class.getDeclaredMethod("genericScreenOverlayReset");
					overlayReset.setAccessible(true);
					overlayReset.invoke(AbstractDungeon.class);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				PokerPlayerMod.pokerManualScreen.close();
			}
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "render")
	public static class RenderManual {
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon __instance, SpriteBatch sb) {
			if (AbstractDungeon.screen == CurrentScreenEnum.POKER_MANUAL) {
				PokerPlayerMod.pokerManualScreen.render(sb);
			}
		}
	}
}
