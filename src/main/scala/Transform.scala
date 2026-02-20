package transform

import extract.Extract._
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateTransform:
  def transformDate(date : String) : LocalDate =
    date match
      case "null" => LocalDate.now()
      case _ => LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def getAge(date: LocalDate, current: LocalDate) : Int =
      current.getYear() - date.getYear()

  def mapAge(list: List[Anime], current: LocalDate) : List[Int] =
    list.map(anime => getAge(transformDate(anime.startDate), current))

  def sortScore(list: List[Anime]): List[Anime] =
    list.sortBy(_.score)
