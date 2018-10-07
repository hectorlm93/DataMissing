import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkGenerator;

public class DataMissing {
    public static void main(String[] args) throws Exception {

        //
         // Cargamos el dataset de los inmuebles
        //

        DataStream<DataInstance> data = DataStreamLoader.open("datasets/INMUEBLE_SIN_OUTLIER_LIMPIO.arff");

        /**
         * 1. Una vez que se carguen los  datos, se crea una variable aleatoria para cada uno de los
         * atributos (es decir, columna de datos) en nuestros datos
         *
         *
         * 2. Podemos extraer los objetos variables usando el método getVariableByName()

         */
        Variables variables = new Variables(data.getAttributes());

        /* Variable CLAVECLIENTE = variables.getVariableByName("CLV_ID_UNI");*/
        Variable HABITACIONES = variables.getVariableByName("NUM_HAB");
        Variable BANYO = variables.getVariableByName("NUM_BAN");
        Variable ASCENSOR = variables.getVariableByName("NUM_ASC");
        Variable METROS_CUADRADOS = variables.getVariableByName("NUM_VAL_M2_CONST");
        Variable METROS_UTILES = variables.getVariableByName("NUM_VAL_M2_UTIL");
        Variable IMP_TASACION = variables.getVariableByName("IMP_TASA");
        Variable TIPO_VIVIENDA = variables.getVariableByName("TXT_SUB_CATE_ACTI");
        /*        Variable VIVIENDA = variables.getVariableByName("TXT_CATE_ACTI");
         */

        /**
         * 1.  Una vez que se ha definido la estructura objeto, el siguiente paso es crear:
         *          - Una estructura DAG sobre este conjunto de variables

         *
         * 2. Para agregar padres a cada variable, primero recuperamos el objeto ParentSet por el
         *     método getParentSet y luego llamamos al metodo addParent()
         */
        DAG dag = new DAG(variables);
            /*  IZQUIERDA -> HIJOS       DERECHA -> PADRES */

                                /* TASACIÓN */

        dag.getParentSet(IMP_TASACION).addParent(METROS_UTILES);
        dag.getParentSet(IMP_TASACION).addParent(ASCENSOR);

                                /* ASCENSOR */

        dag.getParentSet(ASCENSOR).addParent(TIPO_VIVIENDA);

                                /* METROS_UTILES */

        dag.getParentSet(METROS_UTILES).addParent(METROS_CUADRADOS);

                                /* METROS_UTILES */

        dag.getParentSet(METROS_CUADRADOS).addParent(HABITACIONES);
        dag.getParentSet(METROS_CUADRADOS).addParent(BANYO);
        dag.getParentSet(METROS_CUADRADOS).addParent(TIPO_VIVIENDA);

                                /* BAÑOS */

        dag.getParentSet(BANYO).addParent(HABITACIONES);
        dag.getParentSet(BANYO).addParent(TIPO_VIVIENDA);

                                /* HABITACIONES */

        dag.getParentSet(HABITACIONES).addParent(TIPO_VIVIENDA);


        /**
         * 1. Nos fijamos si el grafo contiene ciclos
         *
         *
         * 2. Dibujamos la salida del DAG creados y vemos si está todoo como esperamos.
         * */
        if (dag.containCycles()) {
            try {
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        System.out.println(dag.toString());


        BayesianNetwork bn = new BayesianNetwork(dag);
        System.out.println(bn.toString());



        double logProb = 0;
        for (DataInstance instance : data) {
            logProb += bn.getLogProbabiltyOf(instance);
        }
        System.out.println(logProb);

        BayesianNetworkWriter.save(bn, "networks/BNExampleAAA.bn");
    }
}
/* Nueva Modificacion GITHUB*/