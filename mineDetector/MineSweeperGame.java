package mineDetector;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
 

public class MineSweeperGame extends JFrame {
    private static final long serialVersionUID = 1L;
    
	private JButton[][] buttons; // JButton 이차원 배열로, 게임 보드의 각 칸. 사용자가 해당 버튼을 클릭하여 게임을 진행
    private int[][] board; //int 이차원 배열로, 게임 보드의 상태를 저장. -1은 지뢰가 있는 칸을, -2는 이미 열려있는 칸을, 그 외의 숫자는 해당 칸 주변의 지뢰 개수를 나타
    private int boardSize = 0; //게임 보드의 행과 열의 크기를 저장하는 변수
    private int numMines = 0; //게임 보드에 배치될 지뢰의 개수를 저장하는 변수
    private int numRevealed; //현재까지 오픈된 칸의 개수를 저장하는 변수
    private boolean gameOver; //게임이 종료되었는지를 나타내는 불리언 변수
    private boolean checkFirst = true; //첫 번째 클릭을 확인하는 변수, 첫 번째 클릭이 지뢰가 아니라면 주변 칸들을 자동으로 열어주기 위해 사용
    private int timeLimit; //게임 플레이 시간 제한을 저장하는 변수
    private Timer timer; //게임 플레이 시간을 측정하기 위한 Swing 타이머 객체
    private int elapsedTime; //경과된 게임 플레이 시간을 저장하는 변수 
    private long maxMines; // 사용자가 지정하는 게임 보드 내 최대 지뢰의 개수 

	//칸 설정
    public MineSweeperGame() { 
    	while (true) {
    	    try {
    	        String sizeInput = JOptionPane.showInputDialog("50 이하의 행/열의 수를 입력하세요. (정방행렬입니다) :");
    	        boardSize = Integer.parseInt(sizeInput);
    	        if (boardSize < 1 || boardSize > 50) {
    	            throw new NumberFormatException();
    	        }
    	        break;
    	    } catch (NumberFormatException e) {
    	        JOptionPane.showMessageDialog(null, "1 이상 50 이하의 자연수를 입력하세요~");
    	    } catch (Exception e) {
    	        JOptionPane.showMessageDialog(null, "1 이상 50 이하의 자연수를 입력하세요~");
    	    }
    	}

    	maxMines = (long) boardSize * boardSize;

    	//지뢰수 설정 
    	while (true) {
    	    try {
    	        String minesInput = JOptionPane.showInputDialog("지뢰의 수를 설정하세요 (최대 " + maxMines + "):");
    	        numMines = Integer.parseInt(minesInput);
    	        if (numMines <= 0 || numMines > maxMines) {
    	            throw new NumberFormatException();
    	        }
    	        break;
    	    } catch (NumberFormatException e) {
    	        JOptionPane.showMessageDialog(null, "1 이상 " + maxMines + " 이하의 자연수를 입력하세요~");
    	    }
    	}
    	//시간 설정 
    	while (true) {
    	    try {
    	        String timeInput = JOptionPane.showInputDialog("게임 플레이 시간 제한(초)을 입력하세요:");
    	        if (timeInput == null) {
    	            throw new Exception();
    	        }
    	        timeLimit = Integer.parseInt(timeInput);
    	        if (timeLimit <= 0 || timeLimit > Integer.MAX_VALUE) {
    	            throw new NumberFormatException();
    	        }
    	        break;
    	    } catch (NumberFormatException e) {
    	        JOptionPane.showMessageDialog(null, "1 이상 " + Integer.MAX_VALUE + " 이하의 자연수를 입력하세요~");
    	    } catch (Exception e) {
    	        JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
    	    }
    	}




        setTitle("지뢰찾기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buttons = new JButton[boardSize][boardSize];
        board = new int[boardSize][boardSize];
        gameOver = false;

        //0 ~ boardsize
        //1 ~ boardsize +1
        //0 ~ boardsize +2
       

        // 게임 보드 생성
        JPanel panel = new JPanel(new GridLayout(boardSize, boardSize));
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                buttons[i][j].setMargin(new Insets(0, 0, 0, 0));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                panel.add(buttons[i][j]);
            }
        }
        placeMines(); // 지뢰 랜덤 배치
        add(panel);
        pack();
        setVisible(true);
    }

    private void placeMines() {
        // 초기화
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = 0;                
            }
        }

        // 랜덤하게 지뢰 배치
        int count = 0;
        while (count < numMines) {
            int x = (int) (Math.random() * boardSize);
            int y = (int) (Math.random() * boardSize);
            if (board[x][y] != -1) { //중복을 확인 
                board[x][y] = -1;                
                count++;
            }
        }
        elapsedTime = 0;
        timer = new Timer(1000, new TimerListener());
        timer.start();
    }

    private void revealCell(int row, int col) {
    	
    	if(checkFirst && board[row][col] != -1) {
    		for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < boardSize && j >= 0 && j < boardSize && board[i][j] != -1) {
                        revealCell(i, j);
                    }
                }
    		checkFirst = false;
    	}
    } 
    	
        if (gameOver || row < 0 || row >= boardSize || col < 0 || col >= boardSize ||  elapsedTime >= timeLimit) {
            return;
        }

        if (board[row][col] == -1) {
            gameOver = true;
            showAllMines();
            JOptionPane.showMessageDialog(this, "게임 오버!");
            return;
        }

        else if(board[row][col] != -2){
        	board[row][col] = -2;
    		buttons[row][col].setText(Integer.toString(countMine(row,col)));
            numRevealed++;
            checkGameWin();
            return;
        }
        
        else {
        	return;
        }
    }

    private void showAllMines() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {   
            	if(board[i][j] == -1) 
            		buttons[i][j].setText("M");
            	else
            		buttons[i][j].setText(Integer.toString(countMine(i,j)));
            }
        }
    }
    //주변 8칸 지뢰수를 확인  
	private int countMine(int i, int j) {
	    int count = 0;
	
	    for (int x = Math.max(0, i - 1); x <= Math.min(i + 1, boardSize - 1); x++) {
	        for (int y = Math.max(0, j - 1); y <= Math.min(j + 1, boardSize - 1); y++) {
	            if (x != i || y != j) {
	                if (board[x][y] == -1) {
	                    count++;
	                }
	            }
	        }
	    }
	    return count;
	}



    private void checkGameWin() {
        if (numRevealed == (boardSize * boardSize - numMines)) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "게임 승리!");
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameOver) {
                return;
            }
            revealCell(row, col);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MineSweeperGame();
            }
        });
    }
    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            elapsedTime++;
            if (elapsedTime >= timeLimit) {
                timer.stop();
                gameOver = true;
                showAllMines();
                JOptionPane.showMessageDialog(MineSweeperGame.this, "시간 초과! 게임 오버!");
            }
        }
    }

}


