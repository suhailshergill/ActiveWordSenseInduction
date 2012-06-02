package edu.ucla.sspace

import edu.ucla.sspace.basis.StringBasisMapping
import edu.ucla.sspace.matrix.Matrices
import edu.ucla.sspace.matrix.MatrixIO
import edu.ucla.sspace.matrix.MatrixIO.Format
import edu.ucla.sspace.vector.CompactSparseVector

import scala.collection.JavaConversions.seqAsJavaList
import scala.io.Source


object ExtractWordsiContexts {
    def main(args: Array[String]) {
        // Load the basis mapping from a fixed set of words.  Any words not in
        // this list will not be represented.
        val basis = new StringBasisMapping()
        Source.fromFile(args(0))
              .getLines
              .foreach(basis.getDimension)
        basis.setReadOnly(true)

        def toVector(wordIds: Array[Int]) = {
            val v = new CompactSparseVector(basis.numDimensions)
            wordIds.foreach(i => v.add(i, 1d))
            v
        }

        val docs = Source.fromFile(args(1))
                         .getLines
                         .mkString("\n")
                         .split("\\n\\n")
        val contexts = docs.map(_.split("\n").tail)
                           .map(_.map(_.split("\\s+")(1)))
                           .map(_.map(basis.getDimension)
                                 .filter(_>=0))
                           .map(toVector)

        MatrixIO.writeMatrix(Matrices.asSparseMatrix(contexts.toList),
                             args(2), Format.SVDLIBC_SPARSE_TEXT)
    }
}
