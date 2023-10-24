import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;


public class StudentPlayer extends Player{
    int otherPlayerIndex = 2;
    int robotPlayerIndex = 1;
    int startDepth = 5;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        ArrayList<Integer> steps = board.getValidSteps();
        Integer bestStep = 0;
        Integer maxChance = -10000000;
        for (Integer step : steps) {
            Board child = new Board(board);
            child.step(playerIndex, step);
            int nextStep = minimax(child, startDepth,  false);
            System.out.println(nextStep);
            if (nextStep > maxChance){
                maxChance = nextStep;
                bestStep = step;
            }
        }
        return bestStep;
    }

    private int minimax(Board board, int depth, boolean maxOrMin){
        if (depth == 0){
            int e = eval(board, playerIndex);
            return e;
        }
        else if(board.gameEnded()){
            if(playerIndex == board.getWinner()){
                return 10000 /* * (startDepth - depth) */;
            }else{
                return -10000  /* / (startDepth - depth) */;
            }
        }


        ArrayList<Integer> validSteps = board.getValidSteps();
        // A robot a max, szóval ő az O
        if (maxOrMin){
            int maxEval = -100000;
            for (Integer step : validSteps) {
                Board child = new Board(board);
                child.step(playerIndex, step);
                int evalOfBoard = minimax(child, depth-1, false);
                maxEval = max(maxEval, evalOfBoard);
            }
            return maxEval;
        }
        // Az ember az X azaz a min
        else{
            int minEval = 100000;
            for (Integer step : validSteps) {
                Board child = new Board(board);
                child.step(playerIndex, step);
                int evalOfBoard = minimax(child, depth-1, true);
                minEval = min(minEval, evalOfBoard);
            }
            return minEval;
        }
    }

    private int eval(Board board, int playerIndex){        
        int score = 0;
        for (int r=0; r<boardSize[0]; r++){
            for (int c=0; c<boardSize[1]; c++){
                int hmr = howManyInARow(r, c, playerIndex, board);
                if(hmr == 4)
                    score += 100;
                else if (hmr == 3)
                    score += 50;
                else if(hmr == 2)
                    score += 10;
                else if (hmr == 1)
                    score += 1;

                int hmc = howManyInACol(r, c, playerIndex, board);
                if(hmc == 4)
                    score += 100;
                else if (hmc == 3)
                    score += 50;
                else if(hmc == 2)
                    score += 10;
                else if (hmc == 1)
                    score += 1;
                
                int other_player_index = playerIndex == 2 ? 1 : 2;    
                hmr = howManyInARow(r, c, other_player_index, board);
                if(hmr == 4)
                    score -= 100;
                else if (hmr == 3)
                    score -= 50;
                else if(hmr == 2)
                    score -= 10;
                else if (hmr == 1)
                    score -= 1;

                hmc = howManyInACol(r, c, other_player_index, board);
                if(hmc == 4)
                    score -= 100;
                else if (hmc == 3)
                    score -= 50;
                else if(hmc == 2)
                    score -= 10;
                else if (hmc == 1)
                    score -= 1;
            }
        }
        return score;
    }

    private int howManyInARow(int row, int col, int playerIndex, Board board) {
        int nInARow = 0;

        int startCol = col;
        int endCol = min(boardSize[1], startCol+4);
        //int other_player_index = playerIndex == 1 ? 0 : 1;

        for (int c = startCol; c < endCol; c++) {
            if (board.getState()[row][c] == playerIndex){
                nInARow++;
            }
            else if(board.getState()[row][c] == 0 && (row == 0 || board.getState()[row-1][c] != 0)){
                nInARow++;
            }
             else {
                return 0;
            }
        }
        return nInARow;
    }

    private int howManyInACol(int row, int col, int playerIndex, Board board) {
        int nInACol = 0;

        int startRow = row;
        int endRow = min(boardSize[0], startRow+4);
        //int other_player_index = playerIndex == 1 ? 0 : 1;

        for (int r = startRow; r < endRow; r++) {
            if (board.getState()[r][col] == playerIndex) {
                nInACol++;
            } else if (board.getState()[r][col] == 0 && (row == 0 || board.getState()[r-1][col] != 0)){
                nInACol++;
            } else{
                return 0;
            }
        }
        return nInACol;
    }

    private int howManyDiagonally(int row, int col, int playerIndex, Board board) {
        int nInADiagonal = 0;

        int stepLeftUp = min(nToConnect - 1, min(row, col));
        int stepRightDown = min(nToConnect, min(boardSize[0] - row, boardSize[1] - col));

        if ((stepLeftUp + stepRightDown) < nToConnect)
            return 0;

        for (int diagonalStep = -stepLeftUp; diagonalStep < stepRightDown; diagonalStep++) {
            if (board.getState()[row + diagonalStep][col + diagonalStep] == playerIndex) {
                nInADiagonal++;
            } else {
                nInADiagonal = 0;
            }
        }
        return nInADiagonal;
    }

    private int howManykewDiagonally(int row, int col, int playerIndex, Board board) {
        int nInASkewDiagonal = 0;

        int stepLeftDown = min(nToConnect - 1, min(boardSize[0] - row - 1, col));
        int stepRightUp = min(nToConnect, min(row + 1, boardSize[1] - col));

        if ((stepRightUp + stepLeftDown) < nToConnect)
            return 0;

        for (int skewDiagonalStep = -stepLeftDown; skewDiagonalStep < stepRightUp; skewDiagonalStep++) {
            if (board.getState()[row - skewDiagonalStep][col + skewDiagonalStep] == playerIndex) {
                nInASkewDiagonal++;
            } else
                nInASkewDiagonal = 0;
        }
        return nInASkewDiagonal;
    }
}