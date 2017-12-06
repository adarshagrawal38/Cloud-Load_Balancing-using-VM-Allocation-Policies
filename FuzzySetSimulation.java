/*
 * Title:        Fuzzy clustering simulation
 * Description:  simulation and setup for LB-VC-FC
 *               for CS 218 Project 2 
 * Authors : Saketh Saxena, Piyush Bajaj
 *
 * 
 */
package implementation;
import java.util.concurrent.ThreadLocalRandom;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
//import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.omg.Messaging.SyncScopeHelper;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A simple example showing how to create
 * a datacenter with two hosts and run two
 * cloudlets on it. The cloudlets run in
 * VMs with different MIPS requirements.
 * The cloudlets will take different time
 * to complete the execution depending on
 * the requested VM performance.
 */

//

public class FuzzySetSimulation{


	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static VmAllocationPolicySimple nativeObj;
	
	private static advancedVMAllocationPolicy advancedObj;

	private static lbfs lbfsObj;

	
	private static List<Double> natmeanCPUUtilization = new ArrayList<>();

	private static List<Double> natmeanbwtilization = new ArrayList<>();

	private static List<Double> natmeanmemUtilization = new ArrayList<>();
	
	private static List<Double> admeanCPUUtilization = new ArrayList<>();

	private static List<Double> admeanbwtilization = new ArrayList<>();

	private static List<Double> admeanmemUtilization = new ArrayList<>();

	private static List<Double> lbmeanCPUUtilization = new ArrayList<>();

	private static List<Double> lbmeanbwtilization = new ArrayList<>();

	private static List<Double> lbmeanmemUtilization = new ArrayList<>();

//	private static Datacenter datacenter0;
	/**
	 * Creates main() to run this example
	 */
	
	
	public static void main(String[] args) {

		Log.printLine("Starting simulation...");
		

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createNativeDatacenter("Datacenter_0");

			
			//Third step: Create Broker
			DatacenterBroker broker = createBroker();

			int brokerId = broker.getId();
			
			
			//Fourth step: Create  virtual machine
			vmlist = new ArrayList<Vm>();
			
			//Fifth step: Create VMs and Cloudlets and send them to broker

			vmlist = createVM(brokerId, 500, 0); //creating 5 vms

			cloudletList = createCloudlet(brokerId, 1000, 0); // creating 10 cloudlets
		
			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);
			
			
			for (int i=0;i<500;i++)
			{
				Vm vm1 = vmlist.get(i);
				Cloudlet cloudlet1 = cloudletList.get(i);
				broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
				
			}



			
			// Seventh step : Starts the simulation
			CloudSim.startSimulation();			
			
			// Final step: Print results when simulation is over
			// Sixth step: perform LB-VC-FC 			
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			
			
			CloudSim.stopSimulation();
			
		    
			Log.printLine("Native Simulation finished!");
			int xt = 0;
			for(UsageCollector uselists : nativeObj.getUsageCollectors()) {
				
				List<Double> currentCPU = new ArrayList<>();
				List<Double> currentBw = new ArrayList<>();
				List<Double> currentMem = new ArrayList<>();
				
				for(int i = 0; i < uselists.getCpuUtilization().size();i++)
				{
//					if(uselists.getCpuUtilization().get(i) != 0)
					currentCPU.add(uselists.getCpuUtilization().get(i));					
				}

				for(int i = 0; i < uselists.getBwUtilization().size();i++)
				{
//					if(uselists.getBwUtilization().get(i) != 0)
					currentBw.add(uselists.getBwUtilization().get(i));					
				}
				
				for(int i = 0; i < uselists.getMemUtilization().size();i++)
				{
//					if(uselists.getMemUtilization().get(i) != 0)
					currentMem.add(uselists.getMemUtilization().get(i));					
				}
//				System.out.println(uselists.getCpuUtilization());
//				System.out.println(currentCPU);
//				System.out.println(natmeanCPUUtilization);
//				if(xt == 50) {
//					System.exit(0);
//				}
				natmeanCPUUtilization.add(MathUtil.mean(currentCPU));				
				natmeanbwtilization.add(MathUtil.mean(currentBw));
				natmeanmemUtilization.add(MathUtil.mean(currentMem));

			}
			

			
//			PrintWriter out = new PrintWriter(new FileWriter("./fuzzyoutputfile.txt",true)); 
//			out.println(MathUtil.mean(meanCPUUtilization)+","+MathUtil.mean(meanbwtilization)+","+MathUtil.mean(meanmemUtilization));  
//			out.close();

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);
			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			
			@SuppressWarnings("unused")
			Datacenter datacenter1 = createAdvancedDatacenter("Datacenter_1");

			
			//Third step: Create Broker
			DatacenterBroker broker1 = createBroker();

			brokerId = broker1.getId();
			
			
			//Fourth step: Create  virtual machine
			vmlist = ReCreateVM(brokerId,500,0,vmlist);

			cloudletList = createCloudlet(brokerId, 500, 0); // creating 10 cloudlets
		
			broker1.submitVmList(vmlist);
			broker1.submitCloudletList(cloudletList);
			
			
			for (int i=0;i<500;i++)
			{
				Vm vm1 = vmlist.get(i);
				Cloudlet cloudlet1 = cloudletList.get(i);
				broker1.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
				
			}



			
			// Seventh step : Starts the simulation
			CloudSim.startSimulation();			
			
			// Final step: Print results when simulation is over
			// Sixth step: perform LB-VC-FC 			
			newList = broker.getCloudletReceivedList();
			
			
			CloudSim.stopSimulation();
			
		    
			Log.printLine("Advanced Simulation finished!");

			 xt = 0;
			
			for(UsageCollector uselists : advancedObj.getUsageCollectors()) {
				
						List<Double> currentCPU = new ArrayList<>();
				List<Double> currentBw = new ArrayList<>();
				List<Double> currentMem = new ArrayList<>();
				
				for(int i = 0; i < uselists.getCpuUtilization().size();i++)
				{
//					if(uselists.getCpuUtilization().get(i) != 0)
					currentCPU.add(uselists.getCpuUtilization().get(i));					
				}

				for(int i = 0; i < uselists.getBwUtilization().size();i++)
				{
//					if(uselists.getBwUtilization().get(i) != 0)
					currentBw.add(uselists.getBwUtilization().get(i));					
				}
				
				for(int i = 0; i < uselists.getMemUtilization().size();i++)
				{
//					if(uselists.getMemUtilization().get(i) != 0)
					currentMem.add(uselists.getMemUtilization().get(i));					
				}
//				System.out.println(uselists.getCpuUtilization());

//				System.out.println(currentCPU);
//				System.out.println(admeanCPUUtilization);
					
				admeanCPUUtilization.add(MathUtil.mean(currentCPU));				
				admeanbwtilization.add(MathUtil.mean(currentBw));
				admeanmemUtilization.add(MathUtil.mean(currentMem));

//				if(xt == 50) {System.exit(0);}
				xt++;
			}
			
//			PrintWriter out = new PrintWriter(new FileWriter("./fuzzyoutputfile.txt",true)); 
//			out.println(MathUtil.mean(meanCPUUtilization)+","+MathUtil.mean(meanbwtilization)+","+MathUtil.mean(meanmemUtilization));  
//			out.close();

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);
			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			

			@SuppressWarnings("unused")
			Datacenter datacenter2 = createFuzzyDatacenter("Datacenter_2");
			
			//Third step: Create Broker
			DatacenterBroker broker2 = createBroker();
			brokerId = broker2.getId();
			
			
			//Fourth step: Create  virtual machine
			vmlist = ReCreateVM(brokerId,500,0,vmlist);

			cloudletList = createCloudlet(brokerId, 500, 0); // creating 10 cloudlets
		
			broker2.submitVmList(vmlist);
			broker2.submitCloudletList(cloudletList);
			
			
			for (int i=0;i<500;i++)
			{
				Vm vm1 = vmlist.get(i);
				Cloudlet cloudlet1 = cloudletList.get(i);
				broker2.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
				
			}



			
			// Seventh step : Starts the simulation
			CloudSim.startSimulation();			
			
			// Final step: Print results when simulation is over
			// Sixth step: perform LB-VC-FC 			
			newList = broker.getCloudletReceivedList();
			
			
			CloudSim.stopSimulation();
			
		    
			Log.printLine("LBFS Simulation finished!");

			
			for(UsageCollector uselists : lbfsObj.getUsageCollectors()) {
				
						List<Double> currentCPU = new ArrayList<>();
				List<Double> currentBw = new ArrayList<>();
				List<Double> currentMem = new ArrayList<>();
				
				for(int i = 0; i < uselists.getCpuUtilization().size();i++)
				{
					if(uselists.getCpuUtilization().get(i) != 0)
					currentCPU.add(uselists.getCpuUtilization().get(i));					
				}

				for(int i = 0; i < uselists.getBwUtilization().size();i++)
				{
					if(uselists.getBwUtilization().get(i) != 0)
					currentBw.add(uselists.getBwUtilization().get(i));					
				}
				
				for(int i = 0; i < uselists.getMemUtilization().size();i++)
				{
					if(uselists.getMemUtilization().get(i) != 0)
					currentMem.add(uselists.getMemUtilization().get(i));					
				}
				
				lbmeanCPUUtilization.add(MathUtil.mean(currentCPU));				
				lbmeanbwtilization.add(MathUtil.mean(currentBw));
				lbmeanmemUtilization.add(MathUtil.mean(currentMem));

			}
			
//			PrintWriter out = new PrintWriter(new FileWriter("./nativeoutputfile.txt",true)); 
//			out.println(MathUtil.stDev(natmeanCPUUtilization)+","+MathUtil.stDev(natmeanbwtilization)+","+MathUtil.stDev(natmeanmemUtilization));  
//			out.close();
//			PrintWriter out = new PrintWriter(new FileWriter("./advancedoutputfile.txt",true)); 
//			out.println(MathUtil.stDev(admeanCPUUtilization)+","+MathUtil.stDev(admeanbwtilization)+","+MathUtil.stDev(admeanmemUtilization));  
//			out.close();
//			PrintWriter out = new PrintWriter(new FileWriter("./fuzzyoutputfile.txt",true)); 
//			out.println(MathUtil.stDev(lbmeanCPUUtilization)+","+MathUtil.stDev(lbmeanbwtilization)+","+MathUtil.stDev(lbmeanmemUtilization));  
//			out.close();

			System.out.println("------- for Native ----------");
			//System.out.println(natmeanCPUUtilization);
			//System.out.println(admeanCPUUtilization);

			System.out.println("SD of Mean of CPU utilization "+MathUtil.stDev(natmeanCPUUtilization));
			System.out.println("SD of Mean of Bandwidth utilization "+MathUtil.stDev(natmeanbwtilization));
			System.out.println("SD of Mean of memory utilization "+MathUtil.stDev(natmeanmemUtilization));
			System.out.println("-----------------");

			System.out.println("----- for Advanced --------------");
//			System.out.println(admeanCPUUtilization);
			System.out.println("SD of Mean of CPU utilization "+MathUtil.stDev(admeanCPUUtilization));
			System.out.println("SD of Mean of Bandwidth utilization "+MathUtil.stDev(admeanbwtilization));
			System.out.println("Mean of memory utilization "+MathUtil.stDev(admeanmemUtilization));
			System.out.println("-----------------");
			

			System.out.println("----- for LBFS --------------");
			System.out.println("SD of Mean of CPU utilization "+MathUtil.stDev(lbmeanCPUUtilization));
			System.out.println("SD of Mean of Bandwidth utilization "+MathUtil.stDev(lbmeanbwtilization));
			System.out.println("SD of Mean of memory utilization "+MathUtil.stDev(lbmeanmemUtilization));
			System.out.println("-----------------");
			
			
//			System.out.println(natmeanCPUUtilization);
//
//			System.out.println(admeanCPUUtilization);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	
	private static Datacenter createNativeDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		


		int mips = 2800;
		int hostId=0;

		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList.add(new Pe(3, new PeProvisionerSimple(mips)));


		for (int i=0;i< 25;i++)
		{
			// 3. Create PEs and add these into a list.
			//peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

			//4. Create Hosts with its id and list of PEs and add them to the list of machines
			
			
			int ram = 36000; //host memory (MB)
			long storage = 1000000; //host storage
			int bw = 40000;
	
			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList,
	    				new VmSchedulerTimeShared(peList)
	    			)
	    		); // This is our first machine
			hostId++;
		}
		


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "CentOs";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		nativeObj = new VmAllocationPolicySimple(hostList);
		
		try {
			
			datacenter = new Datacenter(name, characteristics, nativeObj, storageList, 0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
}

	
	private static Datacenter createAdvancedDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		


		int mips = 2800;
		int hostId=0;
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList.add(new Pe(3, new PeProvisionerSimple(mips)));

		for (int i=0;i< 25;i++)
		{
			// 3. Create PEs and add these into a list.
			//peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

			//4. Create Hosts with its id and list of PEs and add them to the list of machines
			
			
			int ram = 36000; //host memory (MB)
			long storage = 1000000; //host storage
			int bw = 40000;
	
			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList,
	    				new VmSchedulerTimeShared(peList)
	    			)
	    		); // This is our first machine
			hostId++;
		}
		


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "CentOs";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		advancedObj = new advancedVMAllocationPolicy(hostList);
		try {			
			datacenter = new Datacenter(name, characteristics, advancedObj, storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
}

	
	
	
	
	private static Datacenter createFuzzyDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		


		int mips = 2800;
		int hostId=0;
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList.add(new Pe(3, new PeProvisionerSimple(mips)));

		for (int i=0;i< 25;i++)
		{
			// 3. Create PEs and add these into a list.
			//peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

			//4. Create Hosts with its id and list of PEs and add them to the list of machines
			
			
			int ram = 36000; //host memory (MB)
			long storage = 1000000; //host storage
			int bw = 40000;
	
			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList,
	    				new VmSchedulerTimeShared(peList)
	    			)
	    		); // This is our first machine
			hostId++;
		}
		


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "CentOs";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		lbfsObj = new lbfs(hostList);
		try {			
			datacenter = new Datacenter(name, characteristics, lbfsObj, storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datacenter;
}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
	
	
	private static List<Vm> createVM(int userId, int vms, int idShift) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		int randomNum = ThreadLocalRandom.current().nextInt(200, 1001);

		long size = 10000; //image size (MB)
		
		int ram = 512; //vm memory (MB)
//		int ram = ThreadLocalRandom.current().nextInt(200, 400);
//		int mips = ThreadLocalRandom.current().nextInt(200, 400);
//		randomNum = ThreadLocalRandom.current().nextInt(1000, 2501);
//		long bw = randomNum;
//		randomNum = ThreadLocalRandom.current().nextInt(1,5);
//		int pesNumber = randomNum; //number of cpus
		
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i < vms;i++){

			int mips = ThreadLocalRandom.current().nextInt(200, 400);
			randomNum = ThreadLocalRandom.current().nextInt(1000, 2501);
			long bw = randomNum;
			randomNum = ThreadLocalRandom.current().nextInt(1,5);
			int pesNumber = randomNum; //number of cpus

			vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			list.add(vm[i]);
		}

		return list;
	}
	

	private static List<Vm> ReCreateVM(int userId, int vms, int idShift, List<Vm> oldVm) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		int randomNum = ThreadLocalRandom.current().nextInt(200, 1001);

		long size = 10000; //image size (MB)
		int ram = 512; //vm memory (MB)
//		int mips = 200;
//		randomNum = ThreadLocalRandom.current().nextInt(1000, 2501);
//		long bw = randomNum;
//		randomNum = ThreadLocalRandom.current().nextInt(1,5);
//		int pesNumber = randomNum; //number of cpus
		
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i < vms;i++){
			vm[i] = new Vm(idShift + i, userId, oldVm.get(i).getMips(), oldVm.get(i).getNumberOfPes(), ram, oldVm.get(i).getBw(), size, vmm, new CloudletSchedulerTimeShared());
			list.add(vm[i]);
		}

		return list;
	}
	
	
	
	private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long length = 40000;
		long fileSize = 300;
		long outputSize = 300;
		Random randomNumberGenerator = new Random();
//		int random_number = randomNumberGenerator.nextInt(4);
//		if(random_number == 0) {
//			random_number +=1;
//		}
		int pesNumber = 2;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}

}
