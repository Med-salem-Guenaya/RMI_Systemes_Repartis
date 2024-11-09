import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            MatrixMultiplication stub = (MatrixMultiplication) registry.lookup("MatrixMultiplication");

            int[][] matrixA = {{1, 2}, {3, 4}}; // Exemple de matrices
            int[][] matrixB = {{5, 6}, {7, 8}};

            int[][] result = stub.multiplyMatrices(matrixA, matrixB);

            // Affichage du résultat
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
