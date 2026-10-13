package Ptilopsis.ui;

import com.megacrit.cardcrawl.core.Settings;

public final class ThreadUiText {
    private ThreadUiText() {
    }

    public static String selectCardPrompt(int energyCost) {
        return isChinese() ? "挂载 1 张手牌（耗能 " + energyCost + "，可不选）"
                : "Mount a card (" + energyCost + " Energy; may skip).";
    }

    public static String insufficientEnergy(int energyCost) {
        return isChinese() ? "挂载需要 " + energyCost + " 点能量。"
                : "Mounting requires " + energyCost + " Energy.";
    }

    public static String emptyMutableTitle() {
        return isChinese() ? "空可变线程" : "Mutable Thread (Empty)";
    }

    public static String emptyMutableBody(int energyCost) {
        return effectsBody("", "", "", false) + " NL "
                + (isChinese() ? "点击挂载：" + energyCost + " 能量，保留原牌。"
                : "Click to mount: " + energyCost + " Energy. Keep the original.");
    }

    public static String mutableTitle() {
        return isChinese() ? "可变线程" : "Mutable Thread";
    }

    public static String effectsBody(String construct, String function, String destruct, boolean atStart) {
        String timing = isChinese() ? (atStart ? "回合开始，" : "回合结束，")
                : (atStart ? "At turn start: " : "At turn end: ");
        return (isChinese() ? "构造：" : "Construct: ") + orNone(construct)
                + " NL " + (isChinese() ? "函数：" : "Function: ")
                + (function.isEmpty() ? orNone(function) : timing + function)
                + " NL " + (isChinese() ? "析构：" : "Destruct: ") + orNone(destruct);
    }

    private static String orNone(String text) {
        return text.isEmpty() ? (isChinese() ? "无" : "None") : text;
    }

    public static String replayCards(int count) {
        if (count == 1) {
            return isChinese() ? "免费打出预览牌，随机目标。" : "Play the previewed card for free with a random target.";
        }
        return isChinese() ? "依次免费打出 " + count + " 张牌，随机目标。"
                : "Play these " + count + " cards in order for free with random targets.";
    }

    public static String expiresAfterPlayback() {
        return isChinese() ? "结算后移除线程。" : " Remove this Thread after playback.";
    }

    public static String previewPosition(int index, int count) {
        return (index + 1) + "/" + count + (isChinese() ? "（滚轮切换）" : " (scroll to browse)");
    }

    public static String mutableMarker() {
        return isChinese() ? "变" : "M";
    }

    public static String fixedMarker() {
        return isChinese() ? "固" : "F";
    }

    public static String emptyMarker() {
        return "+";
    }

    private static boolean isChinese() {
        return Settings.language == Settings.GameLanguage.ZHS;
    }
}
