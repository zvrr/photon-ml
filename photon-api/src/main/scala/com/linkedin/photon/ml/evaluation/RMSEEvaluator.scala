/*
 * Copyright 2017 LinkedIn Corp. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linkedin.photon.ml.evaluation

import org.apache.spark.rdd.RDD

import com.linkedin.photon.ml.Types.UniqueSampleId

/**
 * Evaluator for root mean squared error (RMSE).
 *
 * @param labelAndOffsetAndWeights A [[RDD]] of (id, (labels, offsets, weights)) pairs
 */
protected[ml] class RMSEEvaluator(
    override protected[ml] val labelAndOffsetAndWeights: RDD[(UniqueSampleId, (Double, Double, Double))]) extends Evaluator {

  val evaluatorType = EvaluatorType.RMSE

  private val squaredLossEvaluator = new SquaredLossEvaluator(labelAndOffsetAndWeights)

  /**
   * Evaluate scores with labels and weights.
   *
   * @param scoresAndLabelsAndWeights A [[RDD]] of pairs (uniqueId, (score, label, weight)).
   * @return Evaluation metric value
   */
  override protected[ml] def evaluateWithScoresAndLabelsAndWeights(
    scoresAndLabelsAndWeights: RDD[(UniqueSampleId, (Double, Double, Double))]): Double = {

    val squaredLoss = squaredLossEvaluator.evaluateWithScoresAndLabelsAndWeights(scoresAndLabelsAndWeights)
    math.sqrt( squaredLoss / labelAndOffsetAndWeights.count())
  }

  /**
   * Determine the best between two scores returned by the evaluator. In some cases, the better score is higher
   * (e.g. AUC) and in others, the better score is lower (e.g. RMSE).
   *
   * @param score1 The first score to compare
   * @param score2 The second score to compare
   * @return True if the first score is better than the second
   */
  override def betterThan(score1: Double, score2: Double): Boolean = score1 < score2

  /**
   * Compares two [[RMSEEvaluator]] objects.
   *
   * @param other Some other object
   * @return True if the both models conform to the equality contract and have the same model coefficients, false
   *         otherwise
   */
  override def equals(other: Any): Boolean = other match {
    case that: RMSEEvaluator => super.equals(that)
    case _ => false
  }
}
