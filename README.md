# Cloud Load Balancing thorugh VM allocation policies

![San Jose State University](https://i.imgur.com/cShW5MA.gif?1)
![..](https://i.imgur.com/QIGOoLy.png?1)

A collaborative research project towards completion of CS218 - Topics in Cloud Computing.

This project consists of 3 VM allocation policies implemented in CloudSim-
 1. CloudSim's simple VM allocation policy (1)
 2. An advanced novel VM allocation policy which quintessentially uses best fit host (BFH) policy using the host's available 
    bandwidth, CPU and memory resource information to allocate incoming VMs to balance the overall load of the data centre (2)
 3. A further enhancement of the 2nd policy which uses fuzzy logic to allocate VMs using information about available CPU, 
    bandwidth and memory resources and additional feature weights to balance not just the computational resource but also 
    the memory and bandwidth. (3)

Experimental Results:
  Based on experiments performed on all three algorithms
  1. Algorithm (1) produced the standard results as expected
  2. Algorithm (2) was better able to optimize the bandwidth, memory and CPU utilization of the data centre and was able to 
     reduce energy consumption 
  3. Algorithm (3) performed the best in terms of optimizing the computational resource utilization of the data centre but didn't affect        the overall energy consumption 


Steps to run:
  Step 1 : Extract the zipped file.

  Step 2 : Open a new cloudsim workspace in your IDE of choice

  Step 3 : Create a New package within cloudsim directory called "implementation"

  Step 4 : Import all the files from the repository into the "implementation" package

  Step 5 : To run the program - run the FuzzySetSimulation.java file as a Java Application 

  Results will be displayed when the simulation finishes
