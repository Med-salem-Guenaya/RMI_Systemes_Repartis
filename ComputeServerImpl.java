import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeServerImpl implements ComputeServer {

    public ComputeServerImpl() throws RemoteException {
        super();
    }

    @Override
    public int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int startRow, int endRow) throws RemoteException {
        //
        int numColsB = matrixB[0].length;
        //
        int[][] result = new int[endRow - startRow][numColsB];

        // Print details about the partial work each server will perform
        System.out.println("Worker " + Thread.currentThread().getName() + " starting multiplication from row " + startRow + " to row " + (endRow - 1));

        //
        for (int i = startRow; i < endRow; i++) {
            System.out.println("Processing row " + i + " of matrixA: " + arrayToString(matrixA[i]));
            //
            for (int j = 0; j < numColsB; j++) {
                //
                result[i - startRow][j] = 0;
                System.out.print("Multiplying with column " + j + " of matrixB: " + arrayToString(getColumn(matrixB, j)) + " => ");
                for (int k = 0; k < matrixA[i].length; k++) {
                    //
                    result[i - startRow][j] += matrixA[i][k] * matrixB[k][j];
                    System.out.print(matrixA[i][k] + " * " + matrixB[k][j] + " + ");
                }
                // Clean up the last addition for each multiplication result
                System.out.println("= " + result[i - startRow][j]);
            }
        }

        return result;
    }

    // Utility method to print arrays
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int val : array) {
            sb.append(val).append(" ");
        }
        return sb.toString().trim();
    }

    // Utility method to extract a column from matrixB
    private int[] getColumn(int[][] matrix, int colIndex) {
        //
        int[] column = new int[matrix.length];
        //
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        //
        return column;
    }

    public static void main(String[] args) {
        try {
            //
            ComputeServerImpl obj = new ComputeServerImpl();
            //
            ComputeServer stub = (ComputeServer) UnicastRemoteObject.exportObject(obj, 0);

            int serverId = Integer.parseInt(args[0]); // Each server has a unique ID (0 to 4)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.rebind("ComputeServer" + serverId, stub);

            System.out.println("ComputeServer" + serverId + " ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
