package load

import extract._
import  extract.Extract.Anime
import transform.DateTransform._
import java.time.LocalDate
import java.io.PrintWriter
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object Load:

  def toJson(anime: Anime, age: Int): String =
    s"""
       |    {
       |    "animeId": ${anime.animeId},
       |    "title": "${anime.title}",
       |    "score": ${anime.score},
       |    "rank": ${anime.rank},
       |    "popularity": ${anime.popularity},
       |    "members": ${anime.members},
       |    "synopsis": "${anime.synopsis}",
       |    "startDate": "${anime.startDate}",
       |    "endDate": "${anime.endDate}",
       |    "age": "$age year",
       |    "type": "${anime.animeType}",
       |    "episodes": ${anime.episodes},
       |    "imageUrl": "${anime.imageUrl}"
       |    }""".stripMargin

  def saveToJson(path: String, output: String): Unit =
    val writer = new PrintWriter(output)
    try writer.write(path)
    finally writer.close()

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