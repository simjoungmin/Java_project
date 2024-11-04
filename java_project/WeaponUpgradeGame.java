package firstProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class WeaponUpgradeGame extends JFrame {
    // 게임 상태 필드
    private int money = 20000;
    private int level = 1;
    private int destructionCount = 0;
    private int successStreak = 0;
    private int failureStreak = 0;
    private boolean luckyOwner = false;
    private boolean unluckyOwner = false;
    private int protectionTokens = 0;
    private boolean protectionActive = false;
    public boolean starcatchSuccess = false;
    public JLabel resultLabel;

    // UI 구성 요소
    private JLabel moneyLabel, levelLabel, probabilityLabel;
    private JButton upgradeButton, achievementButton;
    private JCheckBox protectionCheckBox, starcatchBypassCheckBox;

    private final Random random = new Random();

    public WeaponUpgradeGame() {
        setTitle("무기 강화 게임");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1));

        // UI 요소 초기화 및 추가
        moneyLabel = new JLabel("잔액: " + money);
        levelLabel = new JLabel("현재 레벨: " + level);
        resultLabel = new JLabel("결과: 시작하세요!");
        probabilityLabel = new JLabel(getProbabilityText());

        upgradeButton = new JButton("강화하기");
        upgradeButton.addActionListener(new UpgradeListener());

        achievementButton = new JButton("업적 보기");
        achievementButton.addActionListener(e -> showAchievements());

        protectionCheckBox = new JCheckBox("파괴 방지권 사용");
        protectionCheckBox.setEnabled(false);

        starcatchBypassCheckBox = new JCheckBox("스타캐치 해제");
        starcatchBypassCheckBox.addActionListener(e -> toggleProtectionCheckBox());

        add(moneyLabel);
        add(levelLabel);
        add(probabilityLabel);
        add(resultLabel);
        add(upgradeButton);
        add(protectionCheckBox);
        add(starcatchBypassCheckBox);
        add(achievementButton);
    }

    private void showAchievements() {
        StringBuilder achievements = new StringBuilder("업적 목록:\n");
        if (destructionCount >= 3) achievements.append("파괴 방지권 5개 획득\n");
        if (luckyOwner) achievements.append("행운의 주인 보유\n");
        if (unluckyOwner) achievements.append("불운의 주인 보유\n");
        JOptionPane.showMessageDialog(this, achievements.toString());
    }

    private class UpgradeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (money <= 0) {
                resultLabel.setText("잔액이 부족합니다. 게임 오버!");
                upgradeButton.setEnabled(false);
                return;
            }
            if (starcatchBypassCheckBox.isSelected()) processUpgrade();
            else startStarcatchMiniGame();
        }
    }

    private void startStarcatchMiniGame() {
        StarcatchMiniGame miniGame = new StarcatchMiniGame(this);
        miniGame.start();
    }

    public void processUpgrade() {
        int cost = getUpgradeCost(level);
        if (money < cost) {
            resultLabel.setText("잔액이 부족합니다.");
            return;
        }
        money -= cost;
        double chance = calculateSuccessChance();

        double outcome = random.nextDouble() * 100;
        if (outcome < chance) upgradeSuccess();
        else if (outcome < chance + getFailureChance()) upgradeFailure();
        else destroyWeapon();

        updateUI();
        checkAchievements();
    }

    private double calculateSuccessChance() {
        double successChance = getSuccessChance(level);
        if (starcatchSuccess) {
            successChance += 2;
            starcatchSuccess = false;
        }
        if (luckyOwner) successChance += 3;
        return Math.min(100, successChance);
    }

    private void upgradeSuccess() {
        level++;
        successStreak++;
        failureStreak = 0;
        resultLabel.setText("강화 성공! 현재 레벨: " + level);
        if (successStreak == 4 && level >= 15) luckyOwner = true;
    }

    private void upgradeFailure() {
        failureStreak++;
        successStreak = 0;
        if (level >= 16) level--;
        resultLabel.setText("강화 실패! 현재 레벨: " + level);
    }

    private void destroyWeapon() {
        if (useProtectionToken()) {
            resultLabel.setText("파괴 방지권이 활성화되어 무기 파괴가 방지되었습니다.");
        } else {
            destructionCount++;
            level = 1;
            resultLabel.setText("무기 파괴! 1단계로 돌아갑니다.");
        }
    }

    private boolean useProtectionToken() {
        if (protectionCheckBox.isSelected() && protectionTokens > 0) {
            protectionTokens--;
            protectionCheckBox.setSelected(false);
            return true;
        }
        return false;
    }

    private void checkAchievements() {
        if (destructionCount == 3) {
            protectionTokens += 5;
            protectionCheckBox.setEnabled(true);
            resultLabel.setText(resultLabel.getText() + " 파괴 방지권 5개 획득!");
        }
        if (failureStreak >= 7 && level >= 15) unluckyOwner = true;
    }

    private void toggleProtectionCheckBox() {
        protectionCheckBox.setEnabled(!starcatchBypassCheckBox.isSelected());
    }

    private void updateUI() {
        moneyLabel.setText("잔액: " + money);
        levelLabel.setText("현재 레벨: " + level);
        probabilityLabel.setText(getProbabilityText());
    }

    private int getUpgradeCost(int level) {
        if (level <= 5) return 20;
        else if (level <= 10) return 40;
        else if (level <= 15) return 60;
        else if (level <= 17) return 70;
        else if (level <= 18) return 80;
        else if (level <= 20) return 90;
        else return 100;
    }

    private double getDestroyChance(int level) {
        return switch (level) {
            case 15 -> 3.0;
            case 16 -> 4.0;
            case 17 -> 5.0;
            case 18 -> 7.0;
            case 19 -> 8.0;
            case 20 -> 10.0;
            default -> 0.0;
        };
    }

    private String getProbabilityText() {
        return String.format("성공 확률: %.2f%%, 실패 확률: %.2f%%, 파괴 확률: %.2f%%",
                getSuccessChance(level), getFailureChance(), getDestroyChance(level));
    }

    private double getSuccessChance(int level) {
        return switch (level) {
            case 1, 2, 3, 4, 5 -> 80.0;
            case 6, 7, 8, 9, 10 -> 60.0;
            case 11, 12 -> 50.0;
            case 13, 14, 15 -> 40.0;
            case 16, 17, 18, 19, 20, 21 -> 30.0;
            default -> 0.0;
        };
    }

    private double getFailureChance() {
        return 100 - getSuccessChance(level) - getDestroyChance(level);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeaponUpgradeGame game = new WeaponUpgradeGame();
            game.setVisible(true);
        });
    }
}
