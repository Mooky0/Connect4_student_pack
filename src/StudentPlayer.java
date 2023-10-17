import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;


public class StudentPlayer extends Player{
    int otherPlayerIndex = 2;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        ArrayList<Integer> steps = board.getValidSteps();
        Integer bestStep = 0;
        Integer maxChance = 0;
        for (Integer step : steps) {
            Board child = new Board(board);
            child.step(playerIndex, step);
            int nextStep = minimax(child, 6,  true, playerIndex);
            if (nextStep > maxChance){
                maxChance = nextStep;
                bestStep = step;
            }
        }
        return bestStep;
    }

    private int minimax(Board board, int depth, boolean maxOrMin, int playerIndex){
        if (depth == 0){
            return eval(board, playerIndex);
        }
        else if(board.gameEnded()){
            if(playerIndex == board.getWinner()){
                return 1000;
            }else{
                return -1000;
            }
        }


        ArrayList<Integer> validSteps = board.getValidSteps();
        // A robot a max, szóval ő az O
        if (maxOrMin){
            int maxEval = -10000;
            for (Integer step : validSteps) {
                Board child = new Board(board);
                child.step(playerIndex, step);
                int evalOfBoard = minimax(child, depth-1, false, playerIndex);
                maxEval = max(maxEval, evalOfBoard);
            }
            return maxEval;
        }
        // Az ember az X azaz a min
        else{
            int minEval = 10000;
            for (Integer step : validSteps) {
                Board child = new Board(board);
                child.step(playerIndex, step);
                int evalOfBoard = minimax(child, depth-1, true, otherPlayerIndex);
                minEval = min(minEval, evalOfBoard);
            }
            return minEval;
        }
    }

    private int eval(Board board, int playerIndex){        
        int bestChance = 0;
        for (int i=0; i<boardSize[0]; i++){
            int r = howManyInARow(i, 1, board);
            if (r == 3){
                bestChance += 50;
            } else if (r == 2){
                bestChance += 10;
            } else if (r == 1){
                bestChance += 1;
            }
        }
        for (int j=0; j<boardSize[1]; j++){
            int c = howManyInACol(j, 1, board);
            if (c == 3){
                bestChance += 50;
            } else if (c == 2){
                bestChance += 10;
            } else if (c == 1){
                bestChance += 1;
            }
        }
        for (int i=0; i<boardSize[0]; i++){
            int r = howManyInARow(i, otherPlayerIndex, board);
            if (r == 3){
                bestChance -= 50;
            } else if (r == 2){
                bestChance -= 10;
            } else if (r == 1){
                bestChance -= 1;
            }
        }
        for (int j=0; j<boardSize[1]; j++){
            int c = howManyInACol(j, otherPlayerIndex, board);
            if (c == 3){
                bestChance -= 50;
            } else if (c == 2){
                bestChance -= 10;
            } else if (c == 1){
                bestChance -= 1;
            }
        }
        //System.out.println(bestChance);
        return bestChance;
    }

    private int howManyInARow(int row, int playerIndex, Board board) {
        int nInARow = 0;

        int startCol = 0;
        int endCol = boardSize[1];

        for (int c = startCol; c < endCol; c++) {
            if (board.getState()[row][c] == playerIndex) {
                if (c == 0){
                    if (board.getState()[row][c+1] == playerIndex || board.getState()[row][c+1] == playerIndex)
                        nInARow++;
                } else if(c == 6){
                    if (board.getState()[row][c-1] == playerIndex || board.getState()[row][c-1] == playerIndex)
                        nInARow++;
                } else {
                    if ((board.getState()[row][c-1] == playerIndex || board.getState()[row][c-1] == playerIndex) && (board.getState()[row][c+1] == playerIndex || board.getState()[row][c+1] == playerIndex))
                        nInARow++;
                }

                nInARow++;
            } else if (board.getState()[row][c] == otherPlayerIndex){
                nInARow = 0;
            } else {
                nInARow *= 2;
            }
        }
        return nInARow;
    }

    private int howManyInACol(int col, int playerIndex, Board board) {
        int nInACol = 0;

        int startRow = 0;
        int endRow = boardSize[0];

        for (int r = startRow; r < endRow; r++) {
            if (board.getState()[r][col] == playerIndex) {
                if (r == 5){
                    if (board.getState()[r-1][col] == playerIndex || board.getState()[r-1][col] == 0)
                    nInACol++;
                }
                else if (r == 0){
                    if (board.getState()[r+1][col] == playerIndex || board.getState()[r+1][col] == 0)
                    nInACol++;
                }
                else{
                    if ((board.getState()[r-1][col] == playerIndex || board.getState()[r-1][col] == 0) && (board.getState()[r+1][col] == playerIndex || board.getState()[r+1][col] == 0))
                        nInACol++;
                }
                    
            } else if (board.getState()[r][col] == otherPlayerIndex){
                nInACol = 0;
            } else {
                nInACol *= 2;
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
