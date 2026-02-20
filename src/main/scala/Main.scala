import extract._
import transform._
import java.time.LocalDate

object MainApp:

  def main(args: Array[String]): Unit =

    val path = "anime.csv"

    println("\nRunning Parallel...")
    val parData = Extract.extractParallel(path)
    // val parResult = DemoTransform.process(parData)

    println("\nRunning Sequential...")
    val seqData = Extract.extractSequential(path)
    val ageMap = DateTransform.mapAge(seqData, LocalDate.now())
    println(seqData.head)
    println(DateTransform.sortScore(seqData).head)
    