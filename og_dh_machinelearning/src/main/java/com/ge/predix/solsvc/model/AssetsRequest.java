package com.ge.predix.solsvc.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetsRequest implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7056881530235404629L;
	String uri;
	String modelName;
	String manufacturer;
	String pumpType;
	String serialNumber;
    int machineHorsePower;
    String latitude;
    String longitude;
    String image_url;
    int suctionPressureRequired;
    int maxPowerConsumption;
    String lastMaintenanceDate;
    String manufacturedDate;
    int capacity;
    int temperature;
    String tsIdentifier_ingest;
    String tsIdentifier_discharge;
    String tsIdentifier_energyConsumption;
    String tsIdentifier_vibration;
    String tsIdentifier_days_to_failure;

    AnalyticAttributes analyticAttributes;
    
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getPumpType() {
		return pumpType;
	}
	public void setPumpType(String pumpType) {
		this.pumpType = pumpType;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public int getMachineHorsePower() {
		return machineHorsePower;
	}
	public void setMachineHorsePower(int machineHorsePower) {
		this.machineHorsePower = machineHorsePower;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public int getSuctionPressureRequired() {
		return suctionPressureRequired;
	}
	public void setSuctionPressureRequired(int suctionPressureRequired) {
		this.suctionPressureRequired = suctionPressureRequired;
	}
	public int getMaxPowerConsumption() {
		return maxPowerConsumption;
	}
	public void setMaxPowerConsumption(int maxPowerConsumption) {
		this.maxPowerConsumption = maxPowerConsumption;
	}
	public String getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}
	public void setLastMaintenanceDate(String lastMaintenanceDate) {
		this.lastMaintenanceDate = lastMaintenanceDate;
	}
	public String getManufacturedDate() {
		return manufacturedDate;
	}
	public void setManufacturedDate(String manufacturedDate) {
		this.manufacturedDate = manufacturedDate;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public String getTsIdentifier_ingest() {
		return tsIdentifier_ingest;
	}
	public void setTsIdentifier_ingest(String tsIdentifier_ingest) {
		this.tsIdentifier_ingest = tsIdentifier_ingest;
	}
	public String getTsIdentifier_discharge() {
		return tsIdentifier_discharge;
	}
	public void setTsIdentifier_discharge(String tsIdentifier_discharge) {
		this.tsIdentifier_discharge = tsIdentifier_discharge;
	}
	public String getTsIdentifier_energyConsumption() {
		return tsIdentifier_energyConsumption;
	}
	public void setTsIdentifier_energyConsumption(String tsIdentifier_energyConsumption) {
		this.tsIdentifier_energyConsumption = tsIdentifier_energyConsumption;
	}
	public AnalyticAttributes getAnalyticAttributes() {
		return analyticAttributes;
	}
	public void setAnalyticAttributes(AnalyticAttributes analyticAttributes) {
		this.analyticAttributes = analyticAttributes;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public String getTsIdentifier_vibration() {
		return tsIdentifier_vibration;
	}
	public void setTsIdentifier_vibration(String tsIdentifier_vibration) {
		this.tsIdentifier_vibration = tsIdentifier_vibration;
	}
	public String getTsIdentifier_days_to_failure() {
		return tsIdentifier_days_to_failure;
	}
	public void setTsIdentifier_days_to_failure(String tsIdentifier_days_to_failure) {
		this.tsIdentifier_days_to_failure = tsIdentifier_days_to_failure;
	}
	@Override
	public String toString() {
		return "AssetsRequest [uri=" + uri + ", modelName=" + modelName + ", manufacturer=" + manufacturer
				+ ", pumpType=" + pumpType + ", serialNumber=" + serialNumber + ", machineHorsePower="
				+ machineHorsePower + ", latitude=" + latitude + ", longitude=" + longitude + ", image_url=" + image_url
				+ ", suctionPressureRequired=" + suctionPressureRequired + ", maxPowerConsumption="
				+ maxPowerConsumption + ", lastMaintenanceDate=" + lastMaintenanceDate + ", manufacturedDate="
				+ manufacturedDate + ", capacity=" + capacity + ", temperature=" + temperature
				+ ", tsIdentifier_ingest=" + tsIdentifier_ingest + ", tsIdentifier_discharge=" + tsIdentifier_discharge
				+ ", tsIdentifier_energyConsumption=" + tsIdentifier_energyConsumption + ", tsIdentifier_vibration="
				+ tsIdentifier_vibration + ", tsIdentifier_days_to_failure=" + tsIdentifier_days_to_failure
				+ ", analyticAttributes=" + analyticAttributes + "]";
	}
	
}
