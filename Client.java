import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try {
            // Connect to the server's (Coordinator) registry on localhost (assume port 1099)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MatrixMultiplication stub = (MatrixMultiplication) registry.lookup("MatrixMultiplication");

            // My matrices
            int[][] matrixA = {
                    {1, 2},
                    {3, 4},
                    {5, 6},
                    {7, 8},
                    {9, 10},
                    {9, 10}
            }; //  matrix A
            int[][] matrixB = {
                    {11, 9, 12},
                    {13, 8, 14}
            }; //  matrix B

            // Call the multiplyMatrices method on the remote server object
            int[][] result = stub.multiplyMatrices(matrixA, matrixB);

            // Print the result
            System.out.println("Result of matrix multiplication:");
            for (int[] row : result) {
                for (int val : row) {
                    System.out.print(val + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
