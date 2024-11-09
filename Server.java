import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements MatrixMultiplication {
    private final ComputeServer[] computeServers;

    public Server(ComputeServer[] computeServers) {
        this.computeServers = computeServers;
    }

    @Override
    public int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) throws RemoteException {
        int rowsA = matrixA.length;
        int colsB = matrixB[0].length;
        int[][] result = new int[rowsA][colsB];

        // Diviser le calcul entre les serveurs
        for (int i = 0; i < computeServers.length; i++) {
            int[][] partialResult = computeServers[i].multiplyPartial(matrixA, matrixB, i);
            mergePartialResult(result, partialResult);
        }

        return result;
    }

    private void mergePartialResult(int[][] result, int[][] partialResult) {
        for (int i = 0; i < partialResult.length; i++) {
            for (int j = 0; j < partialResult[i].length; j++) {
                result[i][j] += partialResult[i][j];
            }
        }
    }

    public static void main(String[] args) {
        try {
            ComputeServer[] computeServers = new ComputeServer[5];
            for (int i = 0; i < 5; i++) {
                computeServers[i] = (ComputeServer) LocateRegistry.getRegistry("localhost", 1099 + i).lookup("ComputeServer" + i);
            }

            Server obj = new Server(computeServers);
            MatrixMultiplication stub = (MatrixMultiplication) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("MatrixMultiplication", stub);

            System.out.println("Server ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
