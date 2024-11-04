package firstProject;

import javax.swing.*;
import java.util.Random;

// 추상 클래스 GameBase 선언
public abstract class GameBase {
    protected int money;
    protected int level;
    protected JLabel moneyLabel, levelLabel, resultLabel, probabilityLabel;
    protected Random random;

    public GameBase() {
        this.money = 20000; // 시작 금액
        this.level = 1;
        this.random = new Random();
    }

    // 강화 비용을 계산하는 메서드
    protected int getUpgradeCost(int level) {
        if (level <= 5) return 20;
        else if (level <= 10) return 40;
        else if (level <= 15) return 60;
        else if (level <= 17) return 70;
        else if (level <= 18) return 80;
        else if (level <= 20) return 90;
        else return 100;
    }

    // 성공 확률 계산 메서드
    protected double getSuccessChance(int level) {
        double baseChance = 60.0;
        return Math.min(100, baseChance);
    }

    // 실패 확률 계산 메서드
    protected double getFailureChance(int level) {
        return 100 - getSuccessChance(level);
    }

    // 추상 메서드: 상속받는 클래스에서 구체적으로 구현해야 함
    protected abstract void processUpgrade();
}
