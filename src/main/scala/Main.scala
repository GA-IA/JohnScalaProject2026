import extract._
import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import load.Load._
import transform.DemoTransform._
import transform.DateTransform._

def processSequential(inputPath: String, outputPath: String, currentDate: LocalDate): String =
    val data = removeDuplicatesSequential(Extract.extractSequential(inputPath))
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

    finalJson   // 👈 return JSON


def processConcurrent(inputPath: String, outputPath: String, currentDate: LocalDate): String =
    val futureData = removeDuplicatesParallel(
        Await.result(Extract.extractFuture(inputPath), 5.minutes), 4)

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

    finalJson   // 👈 return JSON
 