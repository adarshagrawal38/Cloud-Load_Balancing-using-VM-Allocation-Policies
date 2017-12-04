package implementation;

import java.util.ArrayList;
import java.util.List;

public class UsageCollector {
	List<Double> cpuUtilization = new ArrayList<Double>();
	List<Double> bwUtilization = new ArrayList<Double>();
	List<Double> memUtilization = new ArrayList<Double>();
	


	public UsageCollector(List<Double> cpuUtilization, List<Double> bwUtilization, List<Double> memUtilization) {
		super();
		this.cpuUtilization = cpuUtilization;
		this.bwUtilization = bwUtilization;
		this.memUtilization = memUtilization;
	}

	public List<Double> getCpuUtilization() {
		return cpuUtilization;
	}

	public void setCpuUtilization(List<Double> cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}

	public List<Double> getBwUtilization() {
		return bwUtilization;
	}

	public void setBwUtilization(List<Double> bwUtilization) {
		this.bwUtilization = bwUtilization;
	}

	public List<Double> getMemUtilization() {
		return memUtilization;
	}

	public void setMemUtilization(List<Double> memUtilization) {
		this.memUtilization = memUtilization;
	}
	
}
