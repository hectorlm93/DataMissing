import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkGenerator;

public class DataMissing {
    public static void main(String[] args) {
        DynamicBayesianNetworkGenerator.setNumberOfContinuousVars(2);
        DynamicBayesianNetworkGenerator.setNumberOfDiscreteVars(5);
        DynamicBayesianNetworkGenerator.setNumberOfStates(3);

        DynamicBayesianNetwork extendedDBN = DynamicBayesianNetworkGenerator.generateDynamicBayesianNetwork();
        System.out.println(extendedDBN.toString());
    }
}
/* Este no es*/