package firstProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StarcatchMiniGame {
    private WeaponUpgradeGame parentGame; // 부모 게임 객체
    private JLabel starLabel;

    public StarcatchMiniGame(WeaponUpgradeGame parentGame) {
        this.parentGame = parentGame; // 부모 게임 객체 초기화
    }

    public void start() {
        JDialog miniGameDialog = new JDialog(parentGame, "Starcatch 미니게임", true);
        miniGameDialog.setSize(500, 200); // 다이얼로그 크기 조정
        miniGameDialog.setLayout(new BorderLayout());

        starLabel = new JLabel("★", SwingConstants.CENTER);
        starLabel.setFont(new Font("Serif", Font.BOLD, 40));

        JButton stopButton = new JButton("스탑!");
        stopButton.addActionListener(e -> {
            // 미니게임 종료
            miniGameDialog.dispose(); // 다이얼로그 종료

            // 1초 후에 결과 메시지와 강화 진행
            Timer successTimer = new Timer( 0 , e1 -> {
                int starPosition = starLabel.getX(); // 별의 현재 x 위치를 가져옴
                int centerPosition = miniGameDialog.getWidth() / 2 - starLabel.getWidth() / 2; // 다이얼로그 가운데 위치 계산

                String message;
                // 별이 가운데 근처에 멈추면 성공
                if (Math.abs(starPosition - centerPosition) <= 10) {
                    message = "스타캐치에 성공하였습니다. +2% "; // 성공 메시지
                    parentGame.starcatchSuccess = true; // 미니게임 성공 여부 설정
                } else {
                    message = "실패하였습니다."; // 실패 메시지
                }

                // 결과 메시지를 팝업으로 표시
                JOptionPane.showMessageDialog(parentGame, message);

                parentGame.processUpgrade(); // 강화 시도
            });
            successTimer.setRepeats(false); // 한 번만 실행
            successTimer.start();
        });

        miniGameDialog.add(starLabel, BorderLayout.CENTER);
        miniGameDialog.add(stopButton, BorderLayout.SOUTH);

        Timer timer = new Timer(30, new ActionListener() {
            private int direction = 1; // 초기 방향을 오른쪽으로 설정
            private int movementRange = 200; // 왼쪽으로 100, 오른쪽으로 100 이동

            @Override
            public void actionPerformed(ActionEvent e) {
                int x = starLabel.getX();
                int centerX = miniGameDialog.getWidth() / 2 - starLabel.getWidth() / 2; // 중앙 위치 계산

                // 이동 범위: 중앙 기준으로 왼쪽으로 100, 오른쪽으로 100 이동
                if (x >= centerX + movementRange || x <= centerX - movementRange) {
                    direction *= -1; // 방향을 반전
                }

                starLabel.setLocation(x + direction * 13, starLabel.getY()); // 별 이동
            }
        });

        timer.start(); // 타이머 시작
        miniGameDialog.setVisible(true); // 다이얼로그 보이기
    }
}  