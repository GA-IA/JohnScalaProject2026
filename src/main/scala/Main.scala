import extract._
import transform._
import java.time.LocalDate
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object MainApp:

  def main(args: Array[String]): Unit =

    val path = "test.csv"

    println("\nRunning Future...")
    val start = System.nanoTime()
    val result =
      Extract.extractFuture(path)
        .flatMap(data =>
          DateTransform.sortScoreFuture(data)
        )
    result.onComplete:
      case Success(_) =>
        println((System.nanoTime() - start) / 1000000f)
      case Failure(e) =>
        println(e.getMessage)
    Thread.sleep(3000)


    println("\nRunning Sequential...")
    val seqStart = System.nanoTime()
    val seqData = Extract.extractSequential(path)

    DateTransform.sortScore(seqData)
    println((System.nanoTime() - seqStart) / 1000000f)