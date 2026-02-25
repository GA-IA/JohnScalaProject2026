// import extract._
// import transform._
// import java.time.LocalDate

// object MainApp:

//   def main(args: Array[String]): Unit =

//     val path = "test.csv"

//     println("\nRunning Parallel...")
//     val parData = Extract.extractParallel(path)
//     val t = System.nanoTime();
//     DateTransform.sortScorePar(parData)
//     println((System.nanoTime() - t) / 1000000f)
//     // val parResult = DemoTransform.process(parData)

//     println("\nRunning Sequential...")
//     val seqData = Extract.extractSequential(path)
//     val ageMap = DateTransform.mapAge(seqData, LocalDate.now())
//     val y = System.nanoTime();
//     DateTransform.sortScore(seqData)
//     println((System.nanoTime() - y) / 1000000f)
  

package main

import extract.Extract
import transform.DateTransform
import java.time.LocalDate

object Main:

  def main(args: Array[String]): Unit =

    val path = "test.csv"

    val animeSeq = Extract.extractSequential(path)

    val animePar = Extract.extractParallel(path)

    val currentDate = LocalDate.now()

    println("\n===== MAP AGE SEQUENTIAL =====")
    val ages = DateTransform.mapAge(animeSeq, currentDate)
    ages.take(5).foreach(println)

    println("\n===== MAP AGE PARALLEL =====")
    val agesPar = DateTransform.mapAgePar(animePar, currentDate)
    agesPar.take(5).foreach(println)

    println("\n===== SORT SCORE SEQUENTIAL =====")
    val sortedSeq = DateTransform.sortScore(animeSeq)
    sortedSeq.take(5).foreach(a =>
      println(s"${a.title} -> ${a.score}")
    )

    println("\n===== SORT SCORE PARALLEL =====")
    val sortedPar = DateTransform.sortScorePar(animePar)
    sortedPar.take(5).foreach(a =>
      println(s"${a.title} -> ${a.score}")
    )