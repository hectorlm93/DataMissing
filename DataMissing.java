import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.DataOnMemoryListContainer;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.io.BayesianNetworkWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.core.learning.parametric.bayesian.SVB;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.utils.DAGGenerator;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;

import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.utils.DynamicBayesianNetworkGenerator;



import eu.amidst.core.utils.FixedBatchParallelSpliteratorWrapper;
import javafx.scene.chart.PieChart;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class DataMissing {
    public static void main(String[] args) throws Exception {

        //
         // Cargamos el dataset de los inmuebles
        //DataStream<DataInstance> data = DataStreamLoader.open("datasets/INMUEBLE_SIN_OUTLIER_ANONIMO.arff");
        //

        DataStream<DataInstance> train1 = DataStreamLoader.open("datasets/INMUEBLES_TRAIN_ANONIMO.arff");
        DataStream<DataInstance> test1 = DataStreamLoader.open("datasets/INMUEBLE_TEST_ANONIMO.arff");

        /*DataOnMemory<DataInstance> train2 = new DataOnMemoryListContainer<DataInstance>(train1.getAttributes());
       /* DataOnMemory<DataInstance> test2 = new DataOnMemoryListContainer<DataInstance>(test1.getAttributes());

        /**
         *
         *
         *
         *
         */

        /**
         * 1. Una vez que se carguen los  datos, se crea una variable aleatoria para cada uno de los
         * atributos (es decir, columna de datos) en nuestros datos
         *
         *
         * 2. Podemos extraer los objetos variables usando el método getVariableByName()

         */
/*
        Variables variables = new Variables();
        Variable HABITACIONES = variables.newGaussianVariable(data.getAttributes().getAttributeByName("NUM_HAB"));
        Variable BANYO = variables.newGaussianVariable(data.getAttributes().getAttributeByName("NUM_BAN"));
        Variable ASCENSOR = variables.newGaussianVariable(data.getAttributes().getAttributeByName("NUM_ASC"));
        Variable METROS_CUADRADOS = variables.newGaussianVariable(data.getAttributes().getAttributeByName("NUM_VAL_M2_CONST"));
        Variable METROS_UTILES = variables.newGaussianVariable(data.getAttributes().getAttributeByName("NUM_VAL_M2_UTIL"));
        Variable IMP_TASACION = variables.newGaussianVariable(data.getAttributes().getAttributeByName("IMP_TASA"));
        Variable TIPO_VIVIENDA = variables.newGaussianVariable(data.getAttributes().getAttributeByName("TXT_SUB_CATE_ACTI"));
        */
        Variables variables = new Variables(train1.getAttributes());

        Variable HABITACIONES = variables.getVariableByName("NUM_HAB");
        Variable BANYO = variables.getVariableByName("NUM_BAN");
        Variable ASCENSOR = variables.getVariableByName("NUM_ASC");
        Variable METROS_CUADRADOS = variables.getVariableByName("NUM_VAL_M2_CONST");
        Variable METROS_UTILES = variables.getVariableByName("NUM_VAL_M2_UTIL");
        Variable IMP_TASACION = variables.getVariableByName("IMP_TASA");
        Variable TIPO_VIVIENDA = variables.getVariableByName("TXT_SUB_CATE_ACTI");



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
         * 2. Dibujamos la salida del DAG creados y vemos si está todo como esperamos.
         * */
        if (dag.containCycles()) {
            try {
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        System.out.println(dag.toString());



        //Codigo de Aprendizaje  SVB pasandole el DAG que has definido


            //Creamos el objeto SVB
            SVB Apredizaje = new SVB();
            //Se fija la estructura del DAG
            Apredizaje.setDAG(dag); //¿Se incluye aqui el DAG?

            //Se fija el tamaño de la muestra
            Apredizaje.setWindowsSize(10);



            //Vemos la salida
            Apredizaje.setOutput(true);

            // Hacemos uso del el dataset de nuestros datos
            Apredizaje.setDataStream(train1);

            //Se realiza el aprendizaje
            Apredizaje.runLearning();

            //
        Apredizaje.predictedLogLikelihood(test1.toDataOnMemory());


            //Y finalmente se consigue el modelo
            BayesianNetwork bnModel = Apredizaje.getLearntBayesianNetwork();


            // Se imprime el modelo
            System.out.println(bnModel.toString());

        //


        //Predict





        /*
        BayesianNetwork bn = new BayesianNetwork(dag);
        System.out.println(bn.toString());
*/
/*
        double logProb = 0;
        for (DataInstance instance : data) {
            logProb += bn.getLogProbabiltyOf(instance);
        }
        System.out.println(logProb);*/


        BayesianNetworkWriter.save(bnModel, "networks/BNExampleAAA.bn");
    }
}
/* Nueva Modificacion GITHUB*/