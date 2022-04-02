package com.multiKnapsackAlgorithm;

import com.multiKnapsackAlgorithm.PGreedy.PGreedyAlgorithm;
import com.multiKnapsackAlgorithm.hm.ResultWriter;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCaseRunner {
    private String instanceDirectoryName;
    private int numberOfInstances;
    private String[] inputFilePaths;
    private String currentInputFilePath;
    private GreedyAlgorithmBase[] greedyAlgorithmInstances;
    private ResultWriter resultWriter;
    {
        // running on multiple files
         instanceDirectoryName="instance";
        // numberOfInstances=10;

        //instanceDirectoryName="init";
        numberOfInstances=3;
        inputFilePaths=new String[]{
               // "testCase00.txt"
       // };
      //  /*nill -> running on multiple files
        "1.LowDemand-LowSharing.txt"
        ,"2.LowDemand-AverageSharing.txt","3.LowDemand-HighSharing.txt",
                "4.AverageDemand-LowSharing.txt","5.AverageDemand-AverageSharing.txt","6.AverageDemand-HighSharing.txt",
                "7.HighDemand-LowSharing.txt", "8.HighDemand-AverageSharing.txt","9.HighDemand-HighSharing.txt"
               }; //
//*/
        currentInputFilePath=inputFilePaths[0];
        resultWriter=ResultWriter.getInstance();
    }

    public TestCaseRunner(String instanceDirectoryName, int numberOfInstances, String[] inputFilePaths) {
        this.instanceDirectoryName = instanceDirectoryName;
        this.numberOfInstances = numberOfInstances;
        this.inputFilePaths = inputFilePaths;
    }

    public ArrayList<AlgorithmRunResult> runAlgorithms(String testCaseFilePath){
        ArrayList<AlgorithmRunResult> algorithmsRunResults=new ArrayList<>();
        System.out.println("----------------------------------------------------------");
        System.out.println("TestCaseFilePath: "+testCaseFilePath +"\t-> Begin");
        System.out.println("----------------------------------------------------------");
        greedyAlgorithmInstances= new GreedyAlgorithmBase[]{
                new GreedyAlgorithmPhase1(testCaseFilePath),
                //new DGreedyAlgorithm(testCaseFilePath),
                //new PGreedyAlgorithm(testCaseFilePath),



        };
        for (GreedyAlgorithmBase oGreedyAlgorithmItem:greedyAlgorithmInstances) {
            AlgorithmRunResult algorithmRunResult=new AlgorithmRunResult();
            System.out.println("----------------------------------------------------------");
            String algorithmName=oGreedyAlgorithmItem.getClass().getSimpleName();
            System.out.println("Algorithm: "+algorithmName);
            algorithmRunResult.setAlgorithmName(algorithmName);
            Instant runStart=Instant.now();
            oGreedyAlgorithmItem.run();
            Instant runEnd=Instant.now();
            long runTimeElapsedMillis=Duration.between(runStart,runEnd).toMillis() ;
            System.out.println("Run: TimeElapsed: "+runTimeElapsedMillis);
            algorithmRunResult.setElapsedTime(runTimeElapsedMillis);

            System.out.println("TotalProfit: "+oGreedyAlgorithmItem.getTotalProfit());
            algorithmRunResult.setTotalProfit(oGreedyAlgorithmItem.getTotalProfit());

            System.out.println("TotalDataSize: "+oGreedyAlgorithmItem.getTotalDataSize());
            algorithmRunResult.setTotalDataSize(oGreedyAlgorithmItem.getTotalDataSize());


            //System.out.println(oGreedyAlgorithmItem.toString());
            System.out.println("----------------------------------------------------------");
            System.out.println();

            //System.out.println("ServerAssignment: "+oGreedyAlgorithmItem.getserversAssignment());
            //nil
            String []serverDSTA= oGreedyAlgorithmItem.getserversAssignment( oGreedyAlgorithmItem.getTaskItems());
           List<String> sDSTA= Arrays.asList(serverDSTA);
            algorithmRunResult.setServerAssignment(serverDSTA);
            algorithmRunResult.setThroughput(oGreedyAlgorithmItem.getThroughput(oGreedyAlgorithmItem.getTaskItems())); // gets tasks


            /// for phase 2 results:
            algorithmRunResult.setTotalProfitphase2(oGreedyAlgorithmItem.getTotalProfitset());
            algorithmRunResult.setTotalDataSizephase2(oGreedyAlgorithmItem.getTotalDataSizeSet());
            String []serverLocalS= oGreedyAlgorithmItem.getserversSetAssignment( oGreedyAlgorithmItem.getSetItems());
            List<String> sLocal= Arrays.asList(serverLocalS);
            int count= checkDataSharing(sDSTA,sLocal);
            algorithmRunResult.setServerAssignmentphase2(serverLocalS);
            algorithmRunResult.setDeletedTasks(count);


            algorithmRunResult.setThroughputphase2(oGreedyAlgorithmItem.getSetThroughput(oGreedyAlgorithmItem.getSetItems())); // gets set



            algorithmsRunResults.add(algorithmRunResult);

        }
        System.out.println("----------------------------------------------------------");
        System.out.println("TestCaseFilePath: "+testCaseFilePath+"\t-> End");
        System.out.println("----------------------------------------------------------");
        return algorithmsRunResults;
    }


    public int checkDataSharing(List<String> sDSTA, List<String> sLocal){// returns count of tasks that are not preserved from DSTA
        // we use this function to see if all data from DSTA is preserved, we are sure that the increase in datasize is for increase in overall throughput

        int count=0;
        for(int i=0;i< sDSTA.size();i++)
        {
            String val=sDSTA.get(i);
            if(val!="null" && !sLocal.contains(val))
                count++;

        }
        return count;
    }

    public ArrayList<AlgorithmRunResult> runTestCases(String  instanceDirectoryName,int currentInstanceNumber,String[] inputFilePaths){
        ArrayList<AlgorithmRunResult> algorithmsRunResults=new ArrayList<>();
        instanceDirectoryName="data\\"+instanceDirectoryName+currentInstanceNumber+"\\";
        //String[] temp_inputFilePaths=modifyInputFilePathWithCurrentDirectoryName(instanceDirectoryName,inputFilePaths);
        for (String currentTestCaseInputFileName: inputFilePaths){
            String currentTestCaseInputFilePath=inputFilePathBuilder( instanceDirectoryName, currentTestCaseInputFileName) ;
            ArrayList<AlgorithmRunResult>  currentInputFileAlgorithmsRunResult= runAlgorithms(currentTestCaseInputFilePath);
            currentInputFileAlgorithmsRunResult.forEach(current->{
                current.setTestCaseName(currentTestCaseInputFileName);
            });
            algorithmsRunResults.addAll(currentInputFileAlgorithmsRunResult);
        }
        algorithmsRunResults.forEach(current->{
            current.setInstanceNo(currentInstanceNumber);
        });
        return algorithmsRunResults;
    }
    public ArrayList<AlgorithmRunResult> executeForAllInstances(String instanceDirectoryName,int numberOfInstances,String[] inputFilePaths){
        ArrayList<AlgorithmRunResult> algorithmRunResults=new ArrayList<>();
        for (int currentInstanceNumber = 0; currentInstanceNumber < numberOfInstances; currentInstanceNumber++) {
            ArrayList<AlgorithmRunResult> testCasesRunResults=new ArrayList<>();
            System.out.println("**********************************************************");
            System.out.println("Instance: #"+currentInstanceNumber+"\t-> End");
            System.out.println("**********************************************************");

            testCasesRunResults= runTestCases(instanceDirectoryName,currentInstanceNumber,inputFilePaths);
            algorithmRunResults.addAll(testCasesRunResults);

            System.out.println("**********************************************************");
            System.out.println("Instance: #"+currentInstanceNumber+"\t-> End");
            System.out.println("**********************************************************");
        }
        return algorithmRunResults;
    }

    private String inputFilePathBuilder(String instanceDirectoryName,String inputFileName) {

            String inputFilePath=instanceDirectoryName+inputFileName;
            return inputFilePath;
    }

    public void executeAutomation(){

        resultWriter.data=new ArrayList<>(){{
            add("#Instance");
            add("TestCaseName");
            add("Algorithm");
            add("ExecutionTime");
            add("TotalProfit");
            add("TotalDataSize");
            add("ServerAssignment");
            add("Throughput");

            add("Phase2TotalProfit");
            add("Phase2TotalDataSize");
            add("Phase2ServerAssignment");
            add("DeletedTasksFromDSTA");


        }};
        resultWriter.write();
        ArrayList<AlgorithmRunResult> runResults= executeForAllInstances(instanceDirectoryName,numberOfInstances, inputFilePaths);
        ArrayList<String> result=null;
        for (AlgorithmRunResult runResult:runResults) {
            result=new ArrayList<>(){{
                add(String.valueOf(runResult.getInstanceNo()));
                add(runResult.getTestCaseName());
                add(runResult.getAlgorithmName());
                add(String.valueOf( runResult.getElapsedTime()));
                add(String.valueOf( runResult.getTotalProfit()));
                add(String.valueOf(runResult.getTotalDataSize()));
                add(Arrays.toString(runResult.getServerAssignment()));
                add(String.valueOf(runResult.getThroughput()));
                add(String.valueOf( runResult.getTotalProfitphase2()));// phase 2 profit
                add(String.valueOf(runResult.getTotalDataSizephase2()));// phase2 datasharing
                add(Arrays.toString(runResult.getServerAssignmentphase2())); // phase 2  server ass
                add(String.valueOf(runResult.getThroughputphase2())); //phase 2  thr
                add(String.valueOf(runResult.getDeletedTasks()));

            }};
            resultWriter.data=result;
            resultWriter.write();
        }

        resultWriter.close();
    }

}