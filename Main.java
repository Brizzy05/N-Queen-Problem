package LocalSearch;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Main {

    public static void main(String[] args) {
        // ploting function
        plotRunTime();

        int N = 70;
        // indexes represent col and the val represents the row
//        int[] alist = new int[N];
//        int[] dp = new int[N];
//        genQueen(N, alist);
//        System.out.println("N: "+ N + "\n" +Arrays.toString(alist) + "\n");
//        int currH = totalHeuristic(alist);
//        alist = solveNQueen(N, alist, currH, dp);
//        System.out.println("Solution: " + Arrays.toString(alist) + " " + totalHeuristic(alist));

    }

    public static int[] solveNQueen(int N, int[] prevList, int prevH, int[] dp) {
        //System.out.println("In solve " + Arrays.toString(prevList) + " " + prevH);
        // base cases
        if (N <= 1) {
            return prevList;
        }
        // no solution
        if (N > 1 && N < 4) {
            return prevList;
        }

       // to prevent our loop to run forever in a local Optima
        int count = 5;
        while (count > 0) {
            // cloning prevList
            int[] minList = prevList.clone();
            // for each col
            for (int i = 0; i < N; i++) {
                int currH = prevH;
                boolean check = true;
                // we move placement of the queen until we have a better heuristic
                while (dp[i] < N && prevH > 0 && check) {
                    // we keep track of how many moves we have done
                    // at particular col
                    dp[i] += 1;
                    //cloning minlist
                    int[] cList = minList.clone();

                    //perform the move on the newclone
                    cList[i] = dp[i] % N;
                    //calculte heurist
                    currH = totalHeuristic(cList);

                    //if curren heuristic is lower than prevH
                    if (prevH > currH) {
                        // set minList to be this new state
                        minList = cList;
                        prevH = currH;
                        // reset count at 10
                        count = 10;
                        // terminate inner loop
                        check = false;
                    }
                }
            }
            // reset move tracker
            for (int j = 0; j < N; j++) {
                dp[j] = 0;
            }
            // set prevList to be our new state
            if (totalHeuristic(minList) < totalHeuristic(prevList)) {
                prevList = minList;
            }
            //could be at a local optima
            else {
                // provide a random move
                dp[new Random().nextInt(N - 1)] = new Random().nextInt(N - 1);
                // this new random move may gets out of the local Optima
                count--;
            }
        }


        // solution found
        if (prevH == 0) {
            return prevList;
        }

        //find ourseleves at a Local Optima and can't improve
        // regenerate the list or find a new search space
        genQueen(prevList.length, prevList);

        return solveNQueen(N, prevList, totalHeuristic(prevList), dp);
    }

    //attempting to improve the above algorithm
    public static int[] solve(int N, int[] prevL, int prevH)
    {

        if (N <= 1) {
            return prevL;
        }
        // no solution
        if (N > 1 && N < 4) {
            return prevL;
        }

        int count = 10;
        while(count > 0 && prevH > 0)
        {
            int[] minL = prevL.clone();

            for (int i = 0; i < N; i++) {
                //find a queen with the most conflicts then move it randomly
                int col = findQueen(minL);
                int[] currL = minL.clone();
                //if they all have the same heuristic randomize the movement
                if(col != -1)
                {
                    currL[col] = new Random().nextInt(N);
                }
                else
                {
                    currL[new Random().nextInt(N)] = new Random().nextInt(N);
                }

                int currH = totalHeuristic(currL);

                if(currH < totalHeuristic(minL))
                {
                    prevL = minL;
                    prevH = currH;
                    count = 10;
                }
            }
            if(totalHeuristic(prevL) > totalHeuristic(minL))
            {
                prevL = minL;
            }
            //potential local Optima
            else
            {
                count--;
            }


        }

        // solution found
        if (prevH == 0) {
            return prevL;
        }

        //find ourseleves at a Local Optima and can't improve
        // regenerate the list or find a new search space
        genQueen(prevL.length, prevL);

        return solve(N, prevL, totalHeuristic(prevL));
    }

    //finds queen with most conflict
    public static int findQueen(int[] aList)
    {
        // [col, row]
        int ans = -1;
        int maxH = 1;
        for(int i=0; i < aList.length; i++)
        {
            int tmpH = heuristic(aList, i, aList[i]);
            if(maxH < tmpH)
            {
                maxH = tmpH;
                ans = i;
            }
        }
        return ans;
    }


    public static int totalHeuristic(int[] alist) {
        int h = 0;

        for (int i = 0; i < alist.length; i++) {
            h += heuristic(alist, i, alist[i]);
        }

        return h / 2;
    }

    private static void genQueen(int N, int[] alist) {
        // place queens s.t no two share a row and column
        for (int i = 0; i < N; i++) {
            int row = new Random().nextInt(N);
//            while(checkRow(alist, i, row) == 1)
//            {
//                row = new Random().nextInt(N);
//            }
            alist[i] = row;

        }
    }

    public static int heuristic(int[] pList, int col, int row) {
        int h = 0;

        //checks the row
        h += checkRow(pList, col, row);
        // checks pos diagonal "/"
        h += posUpDiagCheck(pList, col, row);
        h += posDownDiagCheck(pList, col, row);
        // checks neg diagonal "\"
        h += negDownDiagCheck(pList, col, row);
        h += negUpDiagCheck(pList, col, row);


        //System.out.println("h: " + h + " c: " + col + " r: " + row);
        return h;
    }

    // checks pairs that attack each other on the same row
    public static int checkRow(int[] pList, int col, int row) {

        for (int i = 0; i < pList.length; i++) {
            if (i != col && row == pList[i]) {
                return 1;
            }
        }

        return 0;
    }

    // checks pairs that attack each other on the same positive upward diagonal
    public static int posUpDiagCheck(int[] pList, int col, int row) {
        int firstCol = col;
        while (row > -1 && col < pList.length) {
            if (pList[col] == row & firstCol != col) {
                return 1;
            }
            col++;
            row--;
        }

        return 0;
    }

    //checks pairs that attack each other on the same positive downward diagonal
    public static int posDownDiagCheck(int[] pList, int col, int row) {
        int firstCol = col;
        while (col < pList.length && row < pList.length) {
            if (pList[col] == row & firstCol != col) {
                return 1;
            }
            col++;
            row++;
        }

        return 0;
    }

    //checks pairs that attack each other on the same negative downward diagonal
    public static int negDownDiagCheck(int[] pList, int col, int row) {
        int firstCol = col;
        while (col > -1 && row < pList.length) {
            if (pList[col] == row & firstCol != col) {
                return 1;
            }
            col--;
            row++;
        }

        return 0;
    }

    //checks pairs that attack each other on the same negative upward diagonal
    public static int negUpDiagCheck(int[] pList, int col, int row) {
        int firstCol = col;
        while (col > -1 && row > -1) {
            if (pList[col] == row & firstCol != col) {
                return 1;
            }
            col--;
            row--;
        }

        return 0;
    }

    // calculates runtime
    public static double complexityTime(int[] plist, int h, int[] dp, int N){
        double startTime = System.nanoTime();
        int[] alist = solveNQueen(N, plist, h, dp);
        double end = System.nanoTime();
        //System.out.println(Arrays.toString(alist) + " " + totalHeuristic(alist));
        //System.out.println(" ");
        double duration = (end - startTime)/1000000000;
        return duration;
    }

    // ploting method
    public static void plotRunTime()
    {
        // N = max - 1
        int max = 25;

        // x and y values
        double[] xval = new double[max];
        // y is a 2d array so we can take the average of each N
        double[] yval = new double[max];

        double[][] avg = new double[max][5];


        // starting from N=1
        for(int i=1; i <= max; i++)
        {
            int N = i;
            xval[i - 1] = N;
            // indexes represent col and the val represents the row
            int[] alist = new int[N];
            int[] dp = new int[N];
            // place the queens randomly
            genQueen(N, alist);
            //System.out.println("N: "+ N + "\n" +Arrays.toString(alist));
            // calculate current heuristic
            int currH = totalHeuristic(alist);

            // multiple run time to get the avg
            for (int j = 0; j < 5; j++) {
                avg[i - 1][j] = complexityTime(alist, currH, dp, N);
            }


        }

        // represents O(N)
        //double[] yval2 = new double[max];

        for (int i = 0; i < max; i++) {
            yval[i] = DoubleStream.of(avg[i]).sum() / 10.0;
        }

        // graphing
        XYChart chart = QuickChart.getChart("N Queen Problem", "N Queen",
                "Execution Time (sec) ", "Solving Time",xval, yval);


        //chart.addSeries("Avg time", xval, yval2).setMarker(SeriesMarkers.NONE);

        // display chart
        new SwingWrapper(chart).displayChart();

    }


}
