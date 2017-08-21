package com.ge.predix.solsvc.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"Hyper parameter tuning for the Random Forest algorithm is done",
"Learning dataset is 75% of the Predict dataset",
"Test F1 Score",
"Threshold F1 score",
"Train F1 Score",
"The features in train and predict dataset are matching",
"Predicted data"
})
public class MLResultData {

@JsonProperty("Hyper parameter tuning for the Random Forest algorithm is done")
private boolean hyperParameterTuningForTheRandomForestAlgorithmIsDone;
@JsonProperty("Learning dataset is 75% of the Predict dataset")
private boolean learningDatasetIs75OfThePredictDataset;
@JsonProperty("Test F1 Score")
private TestF1Score testF1Score;
@JsonProperty("Threshold F1 score")
private ThresholdF1Score thresholdF1Score;
@JsonProperty("Train F1 Score")
private TrainF1Score trainF1Score;
@JsonProperty("The features in train and predict dataset are matching")
private boolean theFeaturesInTrainAndPredictDatasetAreMatching;
@JsonProperty("Predicted data")
private PredictedData predictedData;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("Hyper parameter tuning for the Random Forest algorithm is done")
public boolean isHyperParameterTuningForTheRandomForestAlgorithmIsDone() {
return hyperParameterTuningForTheRandomForestAlgorithmIsDone;
}

@JsonProperty("Hyper parameter tuning for the Random Forest algorithm is done")
public void setHyperParameterTuningForTheRandomForestAlgorithmIsDone(boolean hyperParameterTuningForTheRandomForestAlgorithmIsDone) {
this.hyperParameterTuningForTheRandomForestAlgorithmIsDone = hyperParameterTuningForTheRandomForestAlgorithmIsDone;
}

@JsonProperty("Learning dataset is 75% of the Predict dataset")
public boolean isLearningDatasetIs75OfThePredictDataset() {
return learningDatasetIs75OfThePredictDataset;
}

@JsonProperty("Learning dataset is 75% of the Predict dataset")
public void setLearningDatasetIs75OfThePredictDataset(boolean learningDatasetIs75OfThePredictDataset) {
this.learningDatasetIs75OfThePredictDataset = learningDatasetIs75OfThePredictDataset;
}

@JsonProperty("Test F1 Score")
public TestF1Score getTestF1Score() {
return testF1Score;
}

@JsonProperty("Test F1 Score")
public void setTestF1Score(TestF1Score testF1Score) {
this.testF1Score = testF1Score;
}

@JsonProperty("Threshold F1 score")
public ThresholdF1Score getThresholdF1Score() {
return thresholdF1Score;
}

@JsonProperty("Threshold F1 score")
public void setThresholdF1Score(ThresholdF1Score thresholdF1Score) {
this.thresholdF1Score = thresholdF1Score;
}

@JsonProperty("Train F1 Score")
public TrainF1Score getTrainF1Score() {
return trainF1Score;
}

@JsonProperty("Train F1 Score")
public void setTrainF1Score(TrainF1Score trainF1Score) {
this.trainF1Score = trainF1Score;
}

@JsonProperty("The features in train and predict dataset are matching")
public boolean isTheFeaturesInTrainAndPredictDatasetAreMatching() {
return theFeaturesInTrainAndPredictDatasetAreMatching;
}

@JsonProperty("The features in train and predict dataset are matching")
public void setTheFeaturesInTrainAndPredictDatasetAreMatching(boolean theFeaturesInTrainAndPredictDatasetAreMatching) {
this.theFeaturesInTrainAndPredictDatasetAreMatching = theFeaturesInTrainAndPredictDatasetAreMatching;
}

@JsonProperty("Predicted data")
public PredictedData getPredictedData() {
return predictedData;
}

@JsonProperty("Predicted data")
public void setPredictedData(PredictedData predictedData) {
this.predictedData = predictedData;
}

@Override
public String toString() {
return ToStringBuilder.reflectionToString(this);
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}