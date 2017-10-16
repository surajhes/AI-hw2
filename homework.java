import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by suraj on 10/13/2017.
 */
public class homework {

    private static int id = 0;

    public static void main(String[] args) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\suraj\\IdeaProjects\\AI\\src\\input.txt"))) {
            long startTime = System.currentTimeMillis();
            ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
            long[] allThreadIds = bean.getAllThreadIds();
            long start = bean.getCurrentThreadCpuTime();
            Node root = populateStateFromInputFile(bufferedReader);
            if(root == null){
                System.out.println("Error creating root node..");
                return;
            }
            root.setPlayer(-1);
            root.setScore(Integer.MIN_VALUE);
            Stack<Node> nodeStack = new Stack<>();
            root.setId(id);
            nodeStack.push(root);
            int maxDepth = 3;
            while (!nodeStack.isEmpty()){
                while (!nodeStack.peek().getLeafBit() && (!nodeStack.peek().isVisited())){
                    nodeStack.peek().setVisited(true);
                    List<Node> childNodes = getChildNodes(nodeStack.peek(),maxDepth);
                    for(Node child: childNodes){
                        if(!child.isVisited()){
                            nodeStack.add(child);
                        }
                    }
                }
                Node top = nodeStack.peek();
                if(top.getLeafBit()){
                    Node temp = top;
                    top.setVisited(true);
                    top.setScore(0);
                    while (temp != root){
                        top.setScore(top.getScore() + (temp.getNumberOfSelectedStars()*temp.getNumberOfSelectedStars())*temp.getPlayer());
                        temp = temp.getParent();
                    }
                    setParentScore(top);
                    nodeStack.pop();
                }else if(allChildExplored(top)){
                    if(top == root){
                        nodeStack.pop();
                        top.setVisited(true);
                        continue;
                    }
                    setParentScore(top);
                    nodeStack.pop();
                    top.setVisited(true);
                }
            }
            writeOutputToFile(root.getSolutionNode());
            long endTime = System.currentTimeMillis();
            long end = bean.getCurrentThreadCpuTime();
            System.out.println("Total CPU time = "+ ((float)(end-start)/1000000000)+ " s");
            System.out.println(" Total time taken = " + ((float)(endTime - startTime)/1000) + " s");
        } catch (FileNotFoundException f) {
            System.out.println(f.getStackTrace().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean allChildExplored(Node node) {
        for(Node child : node.getChildNodes()){
            if(!child.isVisited()){
                return false;
            }
        }
        return true;
    }


    private static void setParentScore(Node node) {
        if(node.getParent().getPlayer() == -1){
            if(node.getParent().getScore() < node.getScore()){
                node.getParent().setScore(Math.max(node.getParent().getScore(),node.getScore()));
                node.getParent().setAlpha((int) Math.max(node.getParent().getAlpha(),node.getParent().getScore()));
                node.getParent().setSolutionNode(node);
/*
                if(node.getParent().getAlpha() >= node.getParent().getBeta()){
                    visited.addAll(node.getParent().getChildNodes().stream().map(Node::hashCode).collect(Collectors.toList()));
                }
*/
            }
        }else{
            if(node.getParent().getScore() > node.getScore()){
                node.getParent().setScore(Math.min(node.getParent().getScore(),node.getScore()));
                node.getParent().setBeta((int) Math.min(node.getParent().getBeta(),node.getParent().getScore()));
                node.getParent().setSolutionNode(node);
/*
                if(node.getParent().getAlpha() >= node.getParent().getBeta()){
                    visited.addAll(node.getParent().getChildNodes().stream().map(Node::hashCode).collect(Collectors.toList()));
                }
*/

            }
        }
    }


    private static List<Node> getChildNodes(Node root, int maxDepth) {
        List<Node> childNodes = new LinkedList<>();
        boolean[][] availability = new boolean[root.getBoard().length][root.getBoard().length];
        for(int i = 0; i < root.getBoard().length; i++){
            for(int j = 0; j < root.getBoard().length; j++){
                if(root.getBoard()[i][j] != '*'){
                    availability[i][j] = true;
                }
            }
        }
        while(true){
            Index index = getFirstEmptySpace(availability);
            if(index == null){
                break;
            }
            Node child = new Node(root.getBoard().length,root.getBoard());
            char[][] board = getBoardAfterGravity(child.getBoard(),index.r,index.c,availability);

            child.setBoard(board);
            int starsCount = countNumberOfStars(board);
            child.setParent(root);
            child.setNumberOfSelectedStars(starsCount - countNumberOfStars(child.getParent().getBoard()));
            child.setSelectedRow(index.r);
            child.setSelectedCol(index.c);
            child.setDepth(root.getDepth()+1);
            child.setId(++id);
            boolean isLeaf = checkChildIsLeaf(child,maxDepth);
            child.setLeafBit(isLeaf);
            if(child.getDepth() %2 == 0){
                child.setPlayer(-1);
                child.setScore(Integer.MIN_VALUE);
            }else{
                child.setPlayer(1);
                child.setScore(Integer.MAX_VALUE);
            }
            childNodes.add(child);
        }
        return childNodes;
    }

    private static boolean checkChildIsLeaf(Node child, int maxDepth) {
        if(child.getDepth() == maxDepth)
            return true;
        for(int i = 0; i < child.getBoard().length; i++){
            for(int j = 0; j < child.getBoard().length; j++){
                if(child.getBoard()[i][j] != '*')
                    return false;
            }
        }
        return true;
    }

    private static Index getFirstEmptySpace(boolean[][] board){
        Index index = null;
        for(int i = 0; i < board[0].length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j]){
                    index = new Index(i,j);
                    return index;
                }
            }
        }
        return index;
    }

    private static int countNumberOfStars(char[][] board) {
        int numberOfStars = 0;
        for (char[] aBoard : board) {
            for (int j = 0; j < board[0].length; j++) {
                if (aBoard[j] == '*') {
                    numberOfStars++;
                }
            }
        }
        return numberOfStars;
    }


    public static char[][] getBoardAfterGravity(char[][] board, int row, int col,boolean[][] availability){
        Set<Index> indices = new TreeSet<>();
        board = getConnectedComponents(indices,board,row,col);
        for(Index index : indices){
            int j = index.c;
            int i = index.r;
            availability[i][j] = false;
            while (i >= 1){
                board[i][j] = board[i-1][j];
                i--;
            }
            board[0][j] = '*';
        }
        return board;
    }

    private static char[][] getConnectedComponents(Set<Index> indices, char[][] board, int row, int col) {
        Index index = new Index(row,col);
        Stack<Index> stack = new Stack<>();
        stack.push(index);
        while (!stack.isEmpty()){
            Index item = stack.pop();
            indices.add(item);
            if(item.c - 1 >= 0 && (board[item.r][item.c -1] == board[row][col])){
                Index newNeighbhour = new Index(item.r,item.c -1);
                if(!indices.contains(newNeighbhour)){
                    stack.push(newNeighbhour);
                }
            }
            if(item.c + 1 < board[0].length && (board[item.r][item.c + 1] == board[row][col])){
                Index newNeighbhour = new Index(item.r,item.c + 1);
                if(!indices.contains(newNeighbhour)){
                    stack.push(newNeighbhour);
                }
            }
            if(item.r - 1 >= 0 && (board[row][col] == board[item.r - 1][item.c])){
                Index newNeighbhour = new Index(item.r - 1,item.c);
                if(!indices.contains(newNeighbhour)){
                    stack.push(newNeighbhour);
                }
            }
            if(item.r + 1 < board.length && (board[row][col] == board[item.r + 1][item.c])){
                Index newNeighbhour = new Index(item.r + 1,item.c);
                if(!indices.contains(newNeighbhour)){
                    stack.push(newNeighbhour);
                }
            }
        }
        return board;
    }



    private static void writeOutputToFile(Node output) {
        HashMap<Integer,Character> columnMap = new HashMap<>();
        char ch = 'A';
        for(int i = 0; i < 26;i++){
            columnMap.put(i,ch);
            ch++;
        }
        File outFile = new File("output.txt");
        try {
            if (!outFile.exists()) {
                outFile.createNewFile();
            } else {
                outFile.delete();
                outFile.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile));
            bufferedWriter.write(columnMap.get(output.getSelectedCol()) +""+ (output.getSelectedRow()+1));
            bufferedWriter.newLine();
            for (int i = 0; i < output.getBoard().length;i++) {
                for (int j = 0; j < output.getBoard().length; j++) {
                    bufferedWriter.write(""+output.getBoard()[i][j]);
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Node populateStateFromInputFile(BufferedReader bufferedReader) throws IOException {
        int boardDimensions = Integer.parseInt(bufferedReader.readLine());
        int numberOfFruits = Integer.parseInt(bufferedReader.readLine());
        double timeLeft = Double.parseDouble(bufferedReader.readLine());
        char [][] board = new char[boardDimensions][boardDimensions];
        int i = 0;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null){
                int j = 0;
                for (char c:line.toCharArray()) {
                    board[i][j] = c;
                    j++;
                }
                i++;
            }
        }catch (Exception e){
            System.out.println("Exception while reading the file. Incorrect file format");
            return null;
        }
        return new Node(boardDimensions,board);
    }

}

class Index implements Comparable<Index>{
    int r,c;

    Index(int r,int c){
        this.r = r;
        this.c = c;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Index index = (Index) o;

        if (r != index.r) return false;
        return c == index.c;

    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + c;
        return result;
    }

    @Override
    public int compareTo(Index o) {
        if(this.c == o.c){
            return this.r - o.r;
        }
        return (this.c - o.c);
    }
}