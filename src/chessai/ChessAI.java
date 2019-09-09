/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessai;
import java.io.*;
import java.util.*;
import javafx.util.*;
/**
 *
 * @author elteir
 */
class Pos {
    public int x,y;
    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Pos(Pos cloned) {
        this.x=cloned.x;
        this.y = cloned.y;
    }
}
class Utilities {
    public static Piece[][] board;
    public static int[] colorDir;
    public static final int boardSize = 8;
    public static int[] power;
    public static NamePos[][] colPieces;
    
    public static void synch() {
        board = ChessAI.board;
        colorDir = ChessAI.colorDir;
        power = ChessAI.power;
        colPieces = ChessAI.colPieces;
    }
    
    public static boolean validPos(Pos pos) {
        return (pos.x>=0 && pos.x<boardSize && pos.y>=0 && pos.y<boardSize);
    }
    
    public static boolean canEat(Piece piece, Pos pos) {
        return (board[pos.x][pos.y].name != 0 && 
                board[pos.x][pos.y].color != piece.color);
    }
    
    public static boolean isEmpty(Pos p) {
        return (board[p.x][p.y].name == 0);
    }
    
    public static ArrayList<Pos> moveHelper(Piece piece, int fx, int fy) {
        ArrayList<Pos> positions = new ArrayList();
        Pos oldPos = piece.pos;
        for(int i=oldPos.x+fx, j=oldPos.y+fy; ;i+=fx,j+=fy) {
            Pos newPos = new Pos(i,j);
            if(!validPos(newPos)) {
                break;
            }
            if(isEmpty(newPos) || canEat(piece, newPos)) {
                positions.add(newPos);
                if(canEat(piece, newPos)) {
                    break;
                }
            }
            else {
                break;
            }
        }
        return positions;
    }
    
    public static ArrayList<Pos> moveAccumulator(Piece piece, Pos[] factors) {
        ArrayList<Pos> positions = new ArrayList();
        for(int i=0; i<factors.length; i++) {
            positions.addAll(moveHelper(piece, factors[i].x, factors[i].y));
        }
        return positions;
    }
    
    public static ArrayList<Pos> diagonalMoves(Piece piece) {
        Pos[] factors = {new Pos(1,1), new Pos(1,-1), new Pos(-1,1), new Pos(-1,-1)};
        return moveAccumulator(piece, factors);
    }
    
    public static ArrayList<Pos> lineMoves(Piece piece) {
        Pos[] factors = {new Pos(0,1), new Pos(0,-1), new Pos(1,0), new Pos(-1,0)};
        return moveAccumulator(piece, factors);
    }
    
    public static ArrayList<Pos> pawnMoves(Piece pawn) {
        ArrayList<Pos> positions = new ArrayList();
        int color = pawn.color;
        Pos oldPos = pawn.pos;
        Pos newPos1 = new Pos(oldPos.x, oldPos.y + colorDir[color]);
        if(validPos(newPos1) && isEmpty(newPos1)) {
            positions.add(newPos1);
            Pos newPos2 = new Pos(oldPos.x, oldPos.y+2*colorDir[color]);
            if(validPos(newPos2) && isEmpty(newPos2)
                    && ((colorDir[color]==-1 && oldPos.y==boardSize-2)||(colorDir[color]==1 && oldPos.y==1))) {
                positions.add(newPos2);
            }
        }
        Pos newPos3 = new Pos(oldPos.x+1, oldPos.y + colorDir[color]);
        Pos newPos4 = new Pos(oldPos.x-1, oldPos.y + colorDir[color]);
        if(validPos(newPos3) && canEat(pawn, newPos3)) {
            positions.add(newPos3);
        }
        if(validPos(newPos4) && canEat(pawn, newPos4)) {
            positions.add(newPos4);
        }        
        return positions;
    }
    public static ArrayList<Pos> queenMoves(Piece queen) {
        ArrayList<Pos> positions = new ArrayList();
        positions.addAll(lineMoves(queen));
        positions.addAll(diagonalMoves(queen));
        return positions;
    }
    
    public static ArrayList<Pos> rookMoves(Piece rook) {
        return lineMoves(rook);
    }
    
    public static ArrayList<Pos> bishopMoves(Piece bishop) {
        return diagonalMoves(bishop);
    }
    
    public static ArrayList<Pos> kingMoves(Piece king) {
        ArrayList<Pos> positions = new ArrayList();
        for(int i=-1; i<=1; i++) {
            for(int j=-1; j<=1; j++) {
                if(i==0 && j==0) {
                    continue;
                }
                Pos newPos = new Pos(king.pos.x + i, king.pos.y + j);
                if(validPos(newPos) && (isEmpty(newPos) || canEat(king, newPos))) {
                    positions.add(newPos);
                }
            }
        }
        return positions;
    }
    
    public static ArrayList<Pos> knightMoves(Piece knight) {
        ArrayList<Pos> positions = new ArrayList();
        Pos oldPos = knight.pos;
        Pos[] newPositions = {new Pos(oldPos.x+2, oldPos.y+1), new Pos(oldPos.x+2, oldPos.y-1), new Pos(oldPos.x-2, oldPos.y+1),
        new Pos(oldPos.x-2, oldPos.y-1), new Pos(oldPos.x+1, oldPos.y+2), new Pos(oldPos.x-1, oldPos.y+2), 
        new Pos(oldPos.x+1, oldPos.y-2), new Pos(oldPos.x-1, oldPos.y-2)};
        for(int i=0; i<newPositions.length; i++) {
            Pos newPos = newPositions[i];
            if(validPos(newPos) && (isEmpty(newPos) || canEat(knight, newPos))) {
                positions.add(newPos);
            }
        }
        return positions;
    }
    
    public static ArrayList<Pos> moveCaller(Piece piece) {
        switch(piece.name) {
            case 1: return pawnMoves(piece);
            case 2: return bishopMoves(piece);
            case 3: return knightMoves(piece);
            case 4: return rookMoves(piece);
            case 5: return queenMoves(piece);
            case 6: return kingMoves(piece);
        }
        return new ArrayList();
    }
    
    public static boolean knightThreat(Piece king) {
        ArrayList<Pos> moves = knightMoves(king);
        for(int i=0; i< moves.size(); i++) {
            Pos pos = moves.get(i);
            if(board[pos.x][pos.y].name==3) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean pawnThreat(Piece king) {
        Pos[] positions = {new Pos(1,1), new Pos(1,-1), new Pos(-1,1), new Pos(-1,-1)};
        for(int i=0; i<positions.length; i++) {
            Pos pos = new Pos(king.pos.x+positions[i].x, king.pos.y+positions[i].y);
            if(!validPos(pos)) {
                continue;
            }
            if(board[pos.x][pos.y].name==1 && board[pos.x][pos.y].color != king.color
                    && ((pos.y > king.pos.y && colorDir[board[pos.x][pos.y].color]==-1)||
                    (pos.y < king.pos.y && colorDir[board[pos.x][pos.y].color]==1)))  {
                return true;
            }
        }
        return false;
    }
    
    public static boolean kingThreat(Piece king) {
        ArrayList<Pos> moves = kingMoves(king);
        for(int i=0; i<moves.size(); i++) {
            Pos pos = moves.get(i);
            if(board[pos.x][pos.y].name == 6) {
                return true;
            }
        }
        return false;
    }

    public static Piece identifyKing(int color) {
        for(int i=0 ; i<colPieces[color].length; i++) {
            if(colPieces[color][i].name==6) {
                return board[colPieces[color][i].pos.x][colPieces[color][i].pos.y];
            }
        }
        return null;
    }
    
    public static boolean threatHelper(Piece piece, int fx, int fy, boolean diagonal) {
        Pos oldPos = piece.pos;
        for(int i=oldPos.x+fx, j=oldPos.y+fy; ;i+=fx,j+=fy) {
            Pos newPos = new Pos(i,j);
            if(!validPos(newPos)) {
                return false;
            }
            if(!isEmpty(newPos)) {
                if(board[newPos.x][newPos.y].color != piece.color && (board[newPos.x][newPos.y].name==5||
                        (board[newPos.x][newPos.y].name==2 && diagonal) || (board[newPos.x][newPos.y].name==4 && !diagonal))) {
                    return true;
                }
                return false;
            }
        }
    }
    
    public static boolean isThreat(Piece piece, Pos[] factors, boolean diagonal) {
        ArrayList<Pos> positions = new ArrayList();
        for(int i=0; i<factors.length; i++) {
            if(threatHelper(piece, factors[i].x, factors[i].y, diagonal)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean diagonalThreat(Piece piece) {
        Pos[] factors = {new Pos(1,1), new Pos(1,-1), new Pos(-1,1), new Pos(-1,-1)};
        return isThreat(piece, factors, true);
    }
    
    public static boolean lineThreat(Piece piece) {
        Pos[] factors = {new Pos(0,1), new Pos(0,-1), new Pos(1,0), new Pos(-1,0)};
        return isThreat(piece, factors, false);
    }
    
    public static boolean validState(int color) {
        Piece king = identifyKing(color);
        return !(knightThreat(king) || pawnThreat(king) || kingThreat(king)
                || lineThreat(king) || diagonalThreat(king));
    }
    
    public static int existPowers(int color) {
        int result = 0;
        for(int i=0; i<2; i++) {
            for(int j=0; j<colPieces[i].length; j++) {
                if(colPieces[i][j].pos.x == -1) {
                    continue;
                }
                if(i == color) {
                    result += power[colPieces[i][j].name-1]*25;
                }
                else {
                    result -= power[colPieces[i][j].name-1]*25;
                }
            }
        }
        return result;
    }
    
    public static int potentialPowers(int color) {
        int result = 0;
        for(int c1=0; c1<2; c1++) {
            for(int c2=0; c2<colPieces[c1].length; c2++) {
                int i = colPieces[c1][c2].pos.x, j = colPieces[c1][c2].pos.y;
                //System.out.println(c1+", "+c2);
                if(i==-1)
                    continue;
                ArrayList<Pos> positions = Utilities.moveCaller(board[i][j]);
                for(int k=0; k<positions.size(); k++) {
                    Pos potentialPos = positions.get(k);
                    if(board[potentialPos.x][potentialPos.y].name==0) {
                        continue;
                    }
                    int potentialPower = power[board[potentialPos.x][potentialPos.y].name-1];
                    if(c1==color){
                        result += potentialPower*6;
                    }
                    else {
                        result -= potentialPower*10;
                    }
                }
            }
        }
        return result;
    }
    
    public static int getHeuristic(int color) {
        return existPowers(color)+potentialPowers(color);
    }
    
    public static int promote(Piece piece) {
        Piece promoted = null;
        int maxPower = 0;
        for(int i=0; i<colPieces[piece.color].length; i++) {
            NamePos potPromoted = colPieces[piece.color][i];
            if(potPromoted.pos.x == -1 && power[potPromoted.name-1]>maxPower) {
                maxPower = power[potPromoted.name-1];
                promoted = new Piece(potPromoted.name,piece.color, new Pos(piece.pos));
                promoted.ind = i;
            }
        }
        if(promoted!=null) {
            board[piece.pos.x][piece.pos.y] = promoted;
            colPieces[piece.color][promoted.ind].pos = new Pos(piece.pos);
            colPieces[piece.color][piece.ind].pos = new Pos(-1,-1);
            return piece.ind;
        }
        return -1;
    }
    
    public static boolean isDraw(int turn) throws IOException{
        Result result = ChessAI.alphaBeta(turn, Integer.MIN_VALUE, Integer.MAX_VALUE, true, 0);
        if(result.from.x==-1) {
            return true;
        }
        boolean weak = false;
        int alive = 0;
        for(int i=0; i<2; i++) {
            for(int j=0; j<colPieces[i].length; j++) {
                if(colPieces[i][j].pos.x != -1) {
                    int name = colPieces[i][j].name;
                    if(name==2 || name==3) {
                        weak = true;
                    }
                    alive++;
                }
            }
        }
        if(alive == 2 || (alive==3 && weak)) {
            return true;
        }
        return false;
    }
    
}

class Piece {
    public int name;
    public int color;
    public Pos pos;
    public int ind;

    public Piece(int name, int color, Pos pos) {
        this.name = name;
        this.color = color;
        this.pos = pos;
    }
    
    public Piece(Piece cloned) {
        this.name = cloned.name;
        this.color = cloned.color;
        this.pos = new Pos(cloned.pos.x, cloned.pos.y);
        this.ind = cloned.ind;
    }
}

class Result {
    public int value;
    public Pos from, to;
    public int howFar;

    public Result(int value, Pos from, Pos to, int howFar) {
        this.value = value;
        this.from = from;
        this.to = to;
        this.howFar = howFar;
    }
    
}

class AbstractPiece {
    public int name;
    public int color;

    public AbstractPiece(int name, int color) {
        this.name = name;
        this.color = color;
    }
    
}

class NamePos {
    public int name;
    public Pos pos;

    public NamePos(int name, Pos pos) {
        this.name = name;
        this.pos = pos;
    }
    
}

public class ChessAI {

    /**
     * @param args the command line arguments
     */
    public static int[] colorDir = new int[2];
    public static int boardSize = 8, depthLimit=5;
    public static Piece[][] board = new Piece[boardSize][boardSize];
    public static String[] piecesNames = {"pawn", "bishop", "knight", "rook", "queen", "king"};
    public static int[] power = {1, 3, 3, 5, 9, 20};
    public static int[] horizontalDevs = {0,2,1,0,3,3}; 
    public static NamePos[][] colPieces = new NamePos[2][boardSize*2];
    public static Random rand = new Random();
    public static boolean test;
    
    public static int min(int a, int b) {
        return a<b?a:b;
    }
    
    public static int max(int a, int b) {
        return a>b?a:b;
    }
    
    public static int makeMove(Pos from, Pos to) {
        int color = board[from.x][from.y].color, c=board[from.x][from.y].ind;
        colPieces[color][c].pos = new Pos(to);
        if(board[to.x][to.y].name!=0) {
            colPieces[1-color][board[to.x][to.y].ind].pos = new Pos(-1,-1);
        }
        board[to.x][to.y].name = board[from.x][from.y].name;             
        board[to.x][to.y].color =board[from.x][from.y].color;
        board[to.x][to.y].ind = board[from.x][from.y].ind;
        board[from.x][from.y] = new Piece(0,0,new Pos(from));
        if(board[to.x][to.y].name==1 && (to.y == 0 || to.y == boardSize-1)) {
            return Utilities.promote(board[to.x][to.y]);
        }
        return -1;
    }
    
    public static Result alphaBeta(int color, int alpha, int beta, boolean max, int depth) throws IOException {
        /*if(dp.get(currentPos)!=null) {
            System.out.println("dp");
            return dp.get(currentPos);
        }*/
        Result best = new Result((max?Integer.MIN_VALUE:Integer.MAX_VALUE), new Pos(-1,-1), new Pos(-1,-1), depth);
        if(depth==depthLimit) {
            best.value = Utilities.getHeuristic((max?color:(1-color)));
            /*System.out.println("utility: "+best.value);
            printBoard();
            System.in.read();*/
            return best;
        }
        for(int c = 0; c < colPieces[color].length; c++) {
            NamePos piece = colPieces[color][c];
            Pos oldPos = piece.pos;
            int i=oldPos.x, j = oldPos.y;
            if(i==-1) {
                continue;
            }
            ArrayList<Pos> positions = Utilities.moveCaller(board[i][j]);
            for(int k=positions.size()-1; k>=0; k--) {
                Pos newPos = positions.get(k);
                Piece tempPiece = new Piece(board[newPos.x][newPos.y]);
               int ind = makeMove(new Pos(i,j), newPos);
                if(test) {
                    printBoard();
                    System.in.read();
                }
                if(Utilities.validState(color)) {
                    Result newResult = alphaBeta((color==0?1:0), alpha, beta, !max, depth+1);
                    Result temp = new Result(newResult.value, new Pos(i,j), new Pos(newPos), newResult.howFar);
                    if((max && newResult.value > best.value)||(!max && newResult.value < best.value)) {
                        best = temp;
                    }
                    else if(best.from.x == -1) {
                        best = temp;
                    }
                    if((max && best.value >= beta)||(!max && best.value <= alpha)) {
                        if(ind != -1) {
                            colPieces[color][board[newPos.x][newPos.y].ind].pos = new Pos(-1,-1);
                            board[newPos.x][newPos.y].name = 1;
                            board[newPos.x][newPos.y].ind = ind;
                            colPieces[color][ind].pos = new Pos(newPos);
                        }
                        makeMove(newPos, new Pos(i,j));
                        board[newPos.x][newPos.y] = new Piece(tempPiece);
                        if(board[newPos.x][newPos.y].name!=0) {
                            colPieces[1-color][board[newPos.x][newPos.y].ind].pos = new Pos(newPos);
                        }
                        if(test) {
                            printBoard();
                            System.in.read();
                        }
                        return best;
                    }
                    if(max) {
                        alpha = max(alpha, best.value);
                    }
                    else {
                        beta = min(beta, best.value);
                    }
                }
                //currentPos.put(new AbstractPiece(board[newPos.x][newPos.y].name, board[newPos.x][newPos.y].color), new Pos(i,j));
                //currentPos.put(new AbstractPiece(tempPiece.name, tempPiece.color), newPos);
                if(ind != -1) {
                    colPieces[color][board[newPos.x][newPos.y].ind].pos = new Pos(-1,-1);
                    board[newPos.x][newPos.y].name = 1;
                    board[newPos.x][newPos.y].ind = ind;
                    colPieces[color][ind].pos = new Pos(newPos);
                }
                makeMove(newPos, new Pos(i,j));
                board[newPos.x][newPos.y] = new Piece(tempPiece);
                if(board[newPos.x][newPos.y].name!=0) {
                    colPieces[1-color][board[newPos.x][newPos.y].ind].pos = new Pos(newPos);
                }
                if(test) {
                    printBoard();
                    System.in.read();
                }
            }
        }
        //dp.put(currentPos, best);
        return best;
    }
    
    public static void initiateBoard(int topColor) {
        int[] base = {boardSize-1, 0};
        int[] change = {-1,1};
        for(int i=topColor, j=0; j<2; i=(i+1)%2, j++) {
            for(int k=base[j], l=0; l<2; k+=change[j], l++) {
                if(l==1) {
                    for(int m=0; m<boardSize; m++) {
                        board[m][k] = new Piece(1, i, new Pos(m, k));
                    }
                }
                else {
                    for(int m=0; m<piecesNames.length; m++) {
                        if(piecesNames[m].equals(1)) {
                            continue;
                        }
                        if(!piecesNames[m].equals("queen") && !piecesNames[m].equals("king")) {
                            board[horizontalDevs[m]][k] = new Piece(m+1, i, new Pos(horizontalDevs[m], k));
                            board[boardSize-horizontalDevs[m]-1][k] = new Piece(m+1, i, new Pos(boardSize-horizontalDevs[m]-1, k));
                         }
                        else {
                            if(piecesNames[m].equals("queen")) {
                                if(topColor==0) {
                                    board[horizontalDevs[m]][k] = new Piece(m+1, i, new Pos(horizontalDevs[m], k));
                                }
                                else {
                                    board[boardSize-horizontalDevs[m]-1][k] = new Piece(m+1, i, new Pos(boardSize-horizontalDevs[m]-1, k));
                                }
                            }
                            else {
                                if(topColor==1) {
                                    board[horizontalDevs[m]][k] = new Piece(m+1, i, new Pos(horizontalDevs[m], k));
                                }
                                else {
                                    board[boardSize-horizontalDevs[m]-1][k] = new Piece(m+1, i, new Pos(boardSize-horizontalDevs[m]-1, k));
                                }
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<boardSize; i++) {
            for(int j=2; j<boardSize-2; j++) {
                board[i][j] = new Piece(0,0,new Pos(i,j));
            }
        }
    } 
    
    public static void initiateColPieces() {
        int[] orig = {8,2,2,2,1,1};
        int[] c = new int[2];
        for(int i=0; i<boardSize; i++) {
            for(int j=0; j<boardSize; j++) {
                if(board[i][j].name != 0) {
                    int color = board[i][j].color;
                    colPieces[color][c[color]] = new NamePos(board[i][j].name, new Pos(board[i][j].pos));
                    board[i][j].ind = c[color]++;
                }
            }
        }
        for(int i=0; i<2; i++) {
           int[] act = new int[6]; 
           for(int j=0; j<c[i]; j++) {
               act[colPieces[i][j].name-1]++;
           }
           for(int j=0; j<6; j++) {
               while(act[j] < orig[j]) {
                   colPieces[i][c[i]++] = new NamePos(j+1, new Pos(-1,-1));
                   act[j]++;
               }
           }
        }
        System.out.println(c[0]+", " +c[1]);
    }
    
    public static void initiateGame(int topColor) {
        if(topColor!=-1)
            initiateBoard(topColor);
        initiateColPieces();
    }
    
    public static void printBoard() {
        for(int i=boardSize-1; i>=0; i--) {
            for(int j=0; j<boardSize; j++) {
                if(board[j][i].name==0){
                    System.out.print("...  ");
                }
                else {
                    System.out.print(piecesNames[board[j][i].name-1].substring(0, 2)+board[j][i].color+"  ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static void log() {
        System.out.println();
        printBoard();
        System.out.println("remained from 0:");
        for(int i=0; i<colPieces[0].length; i++)
            if(colPieces[0][i].pos.x != -1)
                System.out.print(piecesNames[colPieces[0][i].name-1] + " at " +colPieces[0][i].pos.x+", "+colPieces[0][i].pos.y+ "    ");
        System.out.println();
        System.out.println("remained from 1:");
        for(int i=0; i<colPieces[1].length; i++)
            if(colPieces[1][i].pos.x != -1)
                System.out.print(piecesNames[colPieces[1][i].name-1] + " at " +colPieces[1][i].pos.x+", "+colPieces[1][i].pos.y+ "    ");
        System.out.println();
        System.out.println("detailed board:");
        for(int i=boardSize-1; i>=0; i--) {
            for(int j=0; j<boardSize; j++) {
                if(board[j][i].name==0) {
                    System.out.print(".................   ");
                }
                else {
                    System.out.print(piecesNames[board[j][i].name-1]+" with col " + board[j][i].color+" with power "+
                            power[board[j][i].name-1]+"   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public static String readBoard(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } 
        catch(Exception e) {
            
        }
        return "";
    }
    
    public static Pair<Integer,Integer> customBoard(String custom) {
        Scanner sc = new Scanner(custom);
        for(int i=boardSize-1; i>=0; i--) {
            for(int j=0; j<boardSize; j++) 
            {
                String word = sc.next();
                if(word.charAt(0)=='.') {
                    board[j][i] = new Piece(0,0,new Pos(j,i));
                } 
                else {
                    String tempName = word.substring(0,2);
                    int name=-1;
                    switch(tempName) {
                        case "pa": name=1; break;
                        case "bi": name=2; break;
                        case "kn": name=3; break;
                        case "ro": name=4; break;
                        case "qu": name=5; break;
                        case "ki": name=6; break;
                    }
                    board[j][i] = new Piece(name, Character.getNumericValue(word.charAt(word.length()-1)),new Pos(j,i));
                }
            }
        }
        Pair<Integer, Integer> pair = new Pair<>(sc.nextInt(), sc.nextInt());
        return pair;
    }
    
    public static void main(String[] args) throws IOException {
        MainWindow mainWin = new MainWindow();
        mainWin.setVisible(true);
    }
    
}

