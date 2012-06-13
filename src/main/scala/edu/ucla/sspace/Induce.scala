package edu.ucla.sspace

import VectorUtil._
import graphical.gibbs.InfiniteMultinomialMixtureModel

object Induce {
    def main(args: Array[String]) {
        val data = loadSparseData(args(0))
        val nTrials = args(1).toInt
        val alpha = args(2).toDouble
        val learner = new InfiniteMultinomialMixtureModel(nTrials, alpha, 1) 
        val assignments = learner.train(data, 0)
        assignments.foreach(println)
    }
}
