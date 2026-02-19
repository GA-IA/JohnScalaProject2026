import extract._
import transform._

object MainApp:

  def main(args: Array[String]): Unit =

    val path = "anime.csv"

    println("\nRunning Parallel...")
    val parData = Extract.extractParallel(path)
    // val parResult = DemoTransform.process(parData)

    println("\nRunning Sequential...")
    val seqData = Extract.extractSequential(path)
    // val seqResult = DemoTransform.process(seqData)