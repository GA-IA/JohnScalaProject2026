import scala.io.Source
import scala.collection.parallel.CollectionConverters.*

object Extract {

  case class Anime(
    animeId: Int,
    title: String,
    score: Double,
    rank: Int,
    popularity: Int,
    members: Int,
    synopsis: String,
    startDate: String,
    endDate: String,
    animeType: String,
    episodes: Int,
    imageUrl: String
  )

  def cleanString(s: String): String = {
    val cleaned = s.trim.replace("\"", "")
    if (cleaned.isEmpty) "null" else cleaned
  }

  def toIntOption(s: String): Int = {
    val cleaned = s.trim
    if (cleaned.isEmpty) 0
    else
      try cleaned.toInt
      catch { case _: Exception => 0 }
  }

  def toDoubleOption(s: String): Double = {
    val cleaned = s.trim
    if (cleaned.isEmpty) 0
    else
      try cleaned.toDouble
      catch { case _: Exception => 0 }
  }

  def ExtractParallel(path: String): List[Anime] = {

    val lines = Source.fromFile(path).getLines().toList
    val data = lines.tail

    val result = data.par.map { line =>
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)

      Anime(
        toIntOption(cols(0)),
        cleanString(cols(1)),
        toDoubleOption(cols(2)),
        toIntOption(cols(3)),
        toIntOption(cols(4)),
        toIntOption(cols(5)),
        cleanString(cols(6)),
        cleanString(cols(7)),
        cleanString(cols(8)),
        cleanString(cols(9)),
        toIntOption(cols(10)),
        cleanString(cols(11))
      )
    }
  }
  
  def ExtractSequential(path: String): List[Anime] = {

    val lines = Source.fromFile(path).getLines().toList
    val data = lines.tail

    data.map { line =>
      val cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)

      Anime(
        toIntOption(cols(0)),
        cleanString(cols(1)),
        toDoubleOption(cols(2)),
        toIntOption(cols(3)),
        toIntOption(cols(4)),
        toIntOption(cols(5)),
        cleanString(cols(6)),
        cleanString(cols(7)),
        cleanString(cols(8)),
        cleanString(cols(9)),
        toIntOption(cols(10)),
        cleanString(cols(11))
      )
    }
  }
}