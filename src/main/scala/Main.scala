import  extract._
import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import load.Load._
import transform.DemoTransform._
import transform.DateTransform._

def main(args: Array[String]): Unit =
    val inputPath = "anime.csv"
    val outputPath = "anime.json"

    val currentDate = LocalDate.now()

    val futureData = Extract.extractFuture(inputPath)

    val result = futureData.map: list =>
        val ages = mapAge(list, currentDate)

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