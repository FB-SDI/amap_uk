package com.ge.predix.analytics.customdto;

public class AnalyticAttributes {

	int averagePowerConsumption;
	Double machineEfficiencyLevel;
	String machineStatus;
	Double cavitation;
	int error_rate;
    int slope;

	public int getAveragePowerConsumption() {
		return averagePowerConsumption;
	}

	public void setAveragePowerConsumption(int averagePowerConsumption) {
		this.averagePowerConsumption = averagePowerConsumption;
	}

	public Double getMachineEfficiencyLevel() {
		return machineEfficiencyLevel;
	}

	public void setMachineEfficiencyLevel(Double sum) {
		this.machineEfficiencyLevel = sum;
	}

	public String getMachineStatus() {
		return machineStatus;
	}

	public void setMachineStatus(String machineStatus) {
		this.machineStatus = machineStatus;
	}

	public Double getCavitation() {
		return cavitation;
	}

	public void setCavitation(Double sum) {
		this.cavitation = sum;
	}

	public int getError_rate() {
		return error_rate;
	}

	public void setError_rate(int error_rate) {
		this.error_rate = error_rate;
	}

	public int getSlope() {
		return slope;
	}

	public void setSlope(int slope) {
		this.slope = slope;
	}

	@Override
	public String toString() {
		return "AnalyticAttributes [averagePowerConsumption=" + averagePowerConsumption + ", machineEfficiencyLevel="
				+ machineEfficiencyLevel + ", machineStatus=" + machineStatus + ", cavitation=" + cavitation
				+ ", error_rate=" + error_rate + ", slope=" + slope + "]";
	}



}
