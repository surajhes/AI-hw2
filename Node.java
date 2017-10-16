import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by suraj on 9/29/2017.
 */
public class Node implements Cloneable{
    private int id;
    private long score;
    private int selectedRow;
    private int selectedCol;
    private boolean leafBit;
    private boolean visited;

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getLeafBit() {
        return leafBit;
    }

    public void setLeafBit(boolean leafBit) {
        this.leafBit = leafBit;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void setSelectedCol(int selectedCol) {
        this.selectedCol = selectedCol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private char[][] board;
    private int player;
    private int alpha;
    private int beta;

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBeta() {
        return beta;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    private int depth;

    public Node getSolutionNode() {
        return solutionNode;
    }

    public void setSolutionNode(Node solutionNode) {
        this.solutionNode = solutionNode;
    }

    private Node solutionNode;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private int numberOfSelectedStars;

    public int getNumberOfSelectedStars() {
        return numberOfSelectedStars;
    }

    public void setNumberOfSelectedStars(int numberOfSelectedStars) {
        this.numberOfSelectedStars = numberOfSelectedStars;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (id != node.id) return false;
        if (score != node.score) return false;
        if (player != node.player) return false;
        if (depth != node.depth) return false;
        if (numberOfSelectedStars != node.numberOfSelectedStars) return false;
        if (!Arrays.deepEquals(board, node.board)) return false;
        if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
        return childNodes != null ? childNodes.equals(node.childNodes) : node.childNodes == null;

    }

    @Override
    public int hashCode() {
        int result;
        result = id;
        result = 31 * result + (int) (score ^ (score >>> 32));
        result = 31 * result + Arrays.deepHashCode(board);
        result = 31 * result + player;
        result = 31 * result + depth;
        result = 31 * result + numberOfSelectedStars;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    private Node parent;
    private List<Node> childNodes;

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }



    public char[][] getBoard() {
        return board;
    }


    public Node getParent() {
        return parent;
    }



    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Node> childNodes) {
        this.childNodes = childNodes;
    }

    public Node(int boardSize, char[][] board) {
        this.childNodes = new LinkedList<>();
        this.board = new char[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++){
            System.arraycopy(board[i],0,this.board[i],0,boardSize);
        }
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }
}
