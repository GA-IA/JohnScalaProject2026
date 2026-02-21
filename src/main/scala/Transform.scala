package transform

import extract.Extract._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.parallel.CollectionConverters._

object DateTransform:
  def transformDate(date : String) : LocalDate =
    date match
      case "null" => LocalDate.now()
      case _ => LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def getAge(date: LocalDate, current: LocalDate) : Int =
      current.getYear() - date.getYear()

  def mapAge(list: List[Anime], current: LocalDate) : List[Int] =
    list.map(anime => getAge(transformDate(anime.startDate), current))

  def mapAgePar(list: List[Anime], current: LocalDate) : List[Int] =
    list.par.map(anime => getAge(transformDate(anime.startDate), current)).toList

  def sortScore(list: List[Anime]): List[Anime] =
    list.sortBy(_.score)

  def sortScorePar(list: List[Anime]): List[Anime] =
    list.par.seq.sortBy(_.score).toList