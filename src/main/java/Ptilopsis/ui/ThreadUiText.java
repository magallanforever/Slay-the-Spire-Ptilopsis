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
        return isChinese() ? "可变线程（未挂载）" : "Mutable Thread (Empty)";
    }

    public static String emptyMutableBody(int energyCost) {
        return isChinese() ? "点击挂载手牌，花费 " + energyCost + " 点能量，原牌保留。"
                : "Click to mount a card for " + energyCost + " Energy. Keep the original.";
    }

    public static String mutableTitle() {
        return isChinese() ? "可变线程" : "Mutable Thread";
    }

    public static String fixedTitle() {
        return isChinese() ? "固定线程" : "Fixed Thread";
    }

    public static String functionBody(String name, boolean expires) {
        String body = isChinese() ? "函数：" + name + "。回合结束时免费打出临时复制品，随机目标。"
                : "Function: " + name + ". At turn end, play a temporary copy for free with a random target.";
        if (expires) {
            body += isChinese() ? "结算后析构。" : " Then destruct this Thread.";
        }
        return body + (isChinese() ? "点击管理函数。" : " Click to manage.");
    }

    public static String fixedBody(String name) {
        return isChinese() ? "固定函数：" + name : "Fixed function: " + name;
    }

    public static String previewBody(boolean atStart, boolean expires, int index, int count) {
        String text = isChinese()
                ? (atStart ? "回合开始时免费打出。" : "回合结束时免费打出。")
                : (atStart ? "Play for free at turn start." : "Play for free at turn end.");
        if (expires) {
            text += isChinese() ? "结算后析构。" : " Then destruct.";
        }
        if (count > 1) {
            text += " NL " + (index + 1) + " / " + count
                    + (isChinese() ? "（滚轮切换）" : " (scroll to browse)");
        }
        return text;
    }

    public static String remove() {
        return isChinese() ? "去除函数" : "Remove";
    }

    public static String close() {
        return isChinese() ? "关闭" : "Close";
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
