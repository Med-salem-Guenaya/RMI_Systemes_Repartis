import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeServerImpl implements ComputeServer {

    @Override
    public int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int serverId) throws RemoteException {
        // Calcul partiel de la multiplication pour chaque serveur en fonction de son ID
        // (Chaque serveur fait une fraction de la multiplication des lignes)

        int rowsA = matrixA.length;
        int colsB = matrixB[0].length;
        int[][] partialResult = new int[rowsA / 5][colsB]; // Hypothèse : nombre de lignes divisible par 5

        for (int i = serverId * rowsA / 5; i < (serverId + 1) * rowsA / 5; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < matrixB.length; k++) {
                    partialResult[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return partialResult;
    }

    public static void main(String[] args) {
        try {
            ComputeServerImpl obj = new ComputeServerImpl();
            ComputeServer stub = (ComputeServer) UnicastRemoteObject.exportObject(obj, 0);

            int serverId = Integer.parseInt(args[0]); // Chaque serveur a un ID unique entre 0 et 4
            Registry registry = LocateRegistry.createRegistry(1100 + serverId);
            registry.bind("ComputeServer" + serverId, stub);

            System.out.println("ComputeServer" + serverId + " ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
