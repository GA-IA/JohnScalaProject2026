package load

import  extract.Extract.Anime
import java.io.PrintWriter

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