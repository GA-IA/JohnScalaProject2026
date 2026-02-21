import extract._
import transform._
import java.time.LocalDate

object MainApp:

  def main(args: Array[String]): Unit =

    val path = "anime.csv"

    println("\nRunning Parallel...")
    val parData = Extract.extractParallel(path)
    val t = System.nanoTime();
    DateTransform.sortScorePar(parData)
    println((System.nanoTime() - t) / 1000000f)
    // val parResult = DemoTransform.process(parData)

    println("\nRunning Sequential...")
    val seqData = Extract.extractSequential(path)
    val ageMap = DateTransform.mapAge(seqData, LocalDate.now())
    val y = System.nanoTime();
    DateTransform.sortScore(seqData)
    println((System.nanoTime() - y) / 1000000f)
    