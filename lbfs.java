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

public class lbfs  extends VmAllocationPolicy{

	

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
	
	public lbfs(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {			
		
			getFreePes().add(host.getNumberOfPes());		
		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
	}




	/**
	 * Allocates the host with less PEs in use for a given VM.
	 * 
	 * @param vm {@inheritDoc}
	 * @return {@inheritDoc}
	 * @pre $none
	 * @post $none
	 */
	
// allocatehostforvm - we implement fuzzy inside this	
@Override
public boolean allocateHostForVm(Vm vm) {

	
		
		// Initialization of variables
		int requiredPes = vm.getNumberOfPes();

		boolean result = false;
		int tries = 0;
		List<Integer> freePesTmp = new ArrayList<Integer>();

		List<FuzzyMapData> fuzzyMapDatas = new ArrayList<>();


		double VMRi = vm.getCurrentRequestedTotalMips()/vm.getCurrentRequestedRam();

		
		
		// Creating freespesTEmp array list
		for (Integer freePes : getFreePes()) 
		{
			
			freePesTmp.add(freePes);
		}

		
		
		if (!getVmTable().containsKey(vm.getUid())) 
		{ 
			// if this vm was not created
	
			do {

				// we still trying until we find a host or until we try all of them
				int moreFree = Integer.MIN_VALUE;
				int idx = -1;

				// we want the host with less pes in use
				for (int i = 0; i < freePesTmp.size(); i++) 
				{
						
					// Getting available mips and ram per host
					Host host = getHostList().get(i);

					double Hri = Math.sqrt(Math.pow((VMRi-host.getAvailableMips()/host.getRamProvisioner().getAvailableRam()),2));

					fuzzyMapDatas.add(new FuzzyMapData(freePesTmp.get(i), Hri));
					
				}
				
				Host host;
				moreFree = Integer.MIN_VALUE;
				Double closer = Double.MAX_VALUE;
				System.out.println(fuzzyMapDatas.size());
				for(int i = 0; i< fuzzyMapDatas.size(); i++){
					
					if (fuzzyMapDatas.get(i).getPes() >= moreFree && fuzzyMapDatas.get(i).getRi() <= closer) 
					{
						moreFree = fuzzyMapDatas.get(i).getPes();
						closer = fuzzyMapDatas.get(i).getRi();
						idx = i;
						
					}				
				
				}

					System.out.println("-----");
					System.out.println(idx);
					System.out.println(getHostList().size());
				

					host = getHostList().get(idx);
					result = host.vmCreate(vm);



				
				if (result) 
				{ 
					// if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					getUsedPes().put(vm.getUid(), requiredPes);
					getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
					
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
					freePesTmp.set(idx, Integer.MIN_VALUE);
				}
				tries++;
			} while (!result && tries < getFreePes().size());// 
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
		int idx = getHostList().indexOf(host);
		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
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

