package transform

import extract.Extract._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, ExecutionContext}

object DateTransform:
  def transformDate(date : String) : LocalDate =
    date match
      case "null" => LocalDate.now()
      case _ => LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def getAge(date: LocalDate, current: LocalDate) : Int =
      current.getYear() - date.getYear()

  def mapAge(list: List[Anime], current: LocalDate) : List[Int] =
    list.map(anime => getAge(transformDate(anime.startDate), current))

  def mapAgeFuture(list: List[Anime], current: LocalDate): Future[List[Int]] =
    Future.sequence:
      list.map(anime =>
        Future:
          getAge(transformDate(anime.startDate), current)
      )

  def sortScore(list: List[Anime]): List[Anime] =
    list.sortBy(_.score)

  def sortScoreFuture(list: List[Anime]): Future[List[Anime]] =
    Future:
      list.sortBy(_.score)