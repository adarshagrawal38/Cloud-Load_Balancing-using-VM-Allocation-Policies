/*
 * Title:        advanced VM allocation for load balancing 
 * Description: implementation of advanced load balancing algorithm
 *               for CS 218 Project 2 
 * Authors : Saketh Saxena, Piyush Bajaj
 *
 * 
 */


package implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.core.CloudSim;




import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class advancedVMAllocationPolicy   extends VmAllocationPolicy{

	

	/** The map between each VM and its allocated host.
         * The map key is a VM UID and the value is the allocated host for that VM. */
	private Map<String, Host> vmTable;

	/** The map between each VM and the number of Pes used. 
         * The map key is a VM UID and the value is the number of used Pes for that VM. */
	private Map<String, Integer> usedPes;

	/** The number of free Pes for each host from {@link #getHostList() }. */
	private List<Integer> freePes;
	
	public List<UsageCollector> usageCollectors = new ArrayList<>();
	
	
	
	/**
	 * Creates a new VmAllocationPolicySimple object.
	 * 
	 * @param list the list of hosts
	 * @pre $none
	 * @post $none
	 */
	
	public advancedVMAllocationPolicy(List<? extends Host> list) {
		super(list);


		setVmTable(new HashMap<String, Host>());
//		setUsedPes(new HashMap<String, Integer>());
		
	}




	/**
	 * Allocates the host with less PEs in use for a given VM.
	 * 
	 * @param vm {@inheritDoc}
	 * @return {@inheritDoc}
	 * @pre $none
	 * @post $none
	 */
	
// allocatehostforvm - we implement advanced allocation inside this	
	
@Override
public boolean allocateHostForVm(Vm vm) {

	
		
		// Initialization of variables

		boolean result = false;
		int tries = 0;

		List<FuzzyMapData> fuzzyMapDatas = new ArrayList<>();


		double requestedMips = vm.getCurrentRequestedTotalMips();
		double requestedRam = vm.getCurrentRequestedRam();
		double requestedbw = vm.getCurrentRequestedBw();
		
		List<Host> listOfHosts = getHostList();
		
		
		
		if (!getVmTable().containsKey(vm.getUid())) 
		{ 
			// if this vm was not created

			for(int i = 0; i < listOfHosts.size(); i++) {
				
					// calculating distance Ci/Mi
			
					// creating obj for host containing ram and available mips
					fuzzyMapDatas.add(new FuzzyMapData(listOfHosts.get(i).getRamProvisioner().getAvailableRam(),listOfHosts.get(i).getAvailableMips()));
			
			}
	
			do {

				int idx = -1;
						
				for(int i= 0; i < fuzzyMapDatas.size(); i++)
				{		
					Integer ram = fuzzyMapDatas.get(i).getPes();
					Double mips = fuzzyMapDatas.get(i).getRi();
					double bw =  getHostList().get(i).getBwProvisioner().getAvailableBw();
					if(ram >= requestedRam && mips >= requestedMips && bw >= requestedbw)
					{						
						idx = i;
						break;
					
					}
				
				}
				
				if(idx == -1) {
				
					double maxAvailableMips = Double.MIN_VALUE;
					for(Host host: getHostList()) {
						if(host.getAvailableMips() > maxAvailableMips && host.getRamProvisioner().getAvailableRam() > requestedRam) {
						idx = host.getId();
						maxAvailableMips = host.getAvailableMips();
					}
				}
			}
				Host host;
				host = getHostList().get(idx);
				result = host.vmCreate(vm);
				
				if (result) 
				{ 
					// if vm were succesfully created in the host
					System.out.println("Success");
					getVmTable().put(vm.getUid(), host);
					
					List<Double> cpuUsage = new ArrayList<Double>();
					List<Double> bwUsage = new ArrayList<Double>();
					List<Double> memUsage = new ArrayList<Double>();

					for(Host hostx : getHostList()) {

						cpuUsage.add((double) (hostx.getTotalMips()-hostx.getAvailableMips()));
						bwUsage.add((double) hostx.getBwProvisioner().getUsedBw());
						memUsage.add((double) hostx.getRamProvisioner().getUsedRam());
					}
					
					UsageCollector usageLists = new UsageCollector(cpuUsage, bwUsage, memUsage);
					usageCollectors.add(usageLists);
					
					result = true;
					break;
				} 
				else 
				{	
					fuzzyMapDatas.get(idx).setRi(0.0);
					fuzzyMapDatas.get(idx).setPes(0);
					
				}
				tries++;
			} while (!result && tries < listOfHosts.size());
			
		}

		return result;
		
}//end of allocate host

public List<UsageCollector> getUsageCollectors() {

	return usageCollectors;
}



public void setUsageCollectors(List<UsageCollector> usageCollectors) {
	this.usageCollectors = usageCollectors;
}



	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
//		int idx = getHostList().indexOf(host);
//		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
//			getFreePes().set(idx, getFreePes().get(idx) + pes);
		}
	}

	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	/**
	 * Sets the vm table.
	 * 
	 * @param vmTable the vm table
	 */
	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}

	/**
	 * Gets the used pes.
	 * 
	 * @return the used pes
	 */
	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	/**
	 * Sets the used pes.
	 * 
	 * @param usedPes the used pes
	 */
	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	/**
	 * Gets the free pes.
	 * 
	 * @return the free pes
	 */
	protected List<Integer> getFreePes() {
		return freePes;
	}

	/**
	 * Sets the free pes.
	 * 
	 * @param freePes the new free pes
	 */
	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();
			int idx = getHostList().indexOf(host);
			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			return true;
		}

		return false;
	}

	

}

