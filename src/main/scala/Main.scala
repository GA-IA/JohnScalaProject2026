import  extract._
import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import load.Load._
import transform.DemoTransform._
import transform.DateTransform._

def processSequential(inputPath: String, outputPath: String, currentDate: LocalDate): Unit =
    val data = Extract.extractSequential(inputPath)
    val ages = mapAge(data, currentDate)

    val jsonList = data.zip(ages).map { case (anime, age) =>
      toJson(anime, age)
    }

    val qs = quartiles(data)

    val quartileJson =
      if qs.nonEmpty then
        s"""|,
           |   "quartiles": {
           |      "Q1": ${qs(0)},
           |      "Q2": ${qs(1)},
           |      "Q3": ${qs(2)}
           |   }""".stripMargin
      else ""

    val finalJson =
      "{\n" +
        "  \"data\": [" +
        jsonList.mkString(",") +
        "\n  ]" +
        quartileJson +
        "\n}"

    saveToJson(finalJson, outputPath)
    println("Saved to anime.json successfully (Sequential).")


def processConcurrent(inputPath : String, outputPath : String, currentDate : LocalDate): Unit =
    val futureData = Extract.extractFuture(inputPath)

    val result = futureData.map: list =>
        val ages = Await.result(mapAgeFuture(list, currentDate), 5.minutes)

        val jsonList =
        list.zip(ages).map { case (anime, age) =>
            toJson(anime, age)
        }

        val qs = quartiles(list)

        val quartileJson =
            if qs.nonEmpty then
            s"""|,
                |   "quartiles": {
                |      "Q1": ${qs(0)},
                |      "Q2": ${qs(1)},
                |      "Q3": ${qs(2)}
                |   }""".stripMargin
            else ""

        "{\n" +
            "  \"data\": [" +
            jsonList.mkString(",") +
            "\n  ]" +
            quartileJson +
        "\n}"

    val finalJson = Await.result(result, 5.minutes)

    saveToJson(finalJson, outputPath)

    println("Saved to anime.json successfully.")

@main def main(processType: Int): Unit =
    val inputPath = "anime.csv"
    val outputPath = "anime.json"

    val currentDate = LocalDate.now()

    val start = System.currentTimeMillis()
    processType match
      case 0 => processSequential(inputPath, outputPath, currentDate)
      case 1 => processConcurrent(inputPath, outputPath, currentDate)
    println("Running time: " + (System.currentTimeMillis() - start))
 