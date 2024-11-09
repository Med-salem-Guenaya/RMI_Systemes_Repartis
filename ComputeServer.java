import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComputeServer extends Remote {
    int[][] multiplyPartial(int[][] matrixA, int[][] matrixB, int serverId) throws RemoteException;
}
