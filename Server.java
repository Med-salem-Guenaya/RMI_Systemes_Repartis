import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements MatrixMultiplication {

    @Override
    public int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) throws RemoteException {
        try {
            // Connect to the registry of worker servers (ComputeServer instances)
            int numServers = 5;  // Number of available ComputeServer instances
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ComputeServer[] computeServers = new ComputeServer[numServers];

            // Lookup each compute server from the registry
            for (int i = 0; i < numServers; i++) {
                computeServers[i] = (ComputeServer) registry.lookup("ComputeServer" + i);
                System.out.println("Connected to ComputeServer" + i);
            }

            // Divide the rows of matrixA between the compute servers
            int numRows = matrixA.length;
            int rowsPerServer = numRows / numServers;


            int[][] partialResults = new int[matrixA.length][matrixB[0].length];

            // Assign the matrix multiplication work to each server
            for (int i = 0; i < numServers; i++) {
                int startRow = i * rowsPerServer;
                int endRow = (i + 1) * rowsPerServer;

                if (i == numServers - 1) {
                    endRow = numRows; // Ensure the last server gets the remaining rows
                }

                // Call the compute server to handle partial matrix multiplication
                int[][] resultPart = computeServers[i].multiplyPartial(matrixA, matrixB, startRow, endRow);

                // Collect partial results from each server
                for (int r = startRow; r < endRow; r++) {
                    partialResults[r] = resultPart[r - startRow];
                }
            }

            return partialResults; // Return the final result after collecting from all servers
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // Create the Server and register it with the RMI registry
            Server server = new Server();
            MatrixMultiplication stub = (MatrixMultiplication) UnicastRemoteObject.exportObject(server, 0);

            // Register the server under the name "MatrixMultiplication"
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MatrixMultiplication", stub);

            System.out.println("Server is ready and listening for client requests...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
