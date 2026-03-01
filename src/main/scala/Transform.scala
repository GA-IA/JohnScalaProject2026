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

  def quartiles(list: List[Anime]): List[Double] =
    val sorted = sortScore(list)
    val scores = sorted.map(_.score)
    val n = scores.length

    if n == 0 then List()
    else
      val q2 =
        if n % 2 == 1 then scores(n / 2)
        else (scores(n / 2 - 1) + scores(n / 2)) / 2

      val lowerHalf =
        if n % 2 == 1 then scores.take(n / 2)
        else scores.take(n / 2)

      val upperHalf =
        if n % 2 == 1 then scores.drop(n / 2 + 1)
        else scores.drop(n / 2)

      def median(data: List[Double]): Double =
        val m = data.length
        if m % 2 == 1 then data(m / 2)
        else (data(m / 2 - 1) + data(m / 2)) / 2

      val q1 = median(lowerHalf)
      val q3 = median(upperHalf)

      List(q1, q2, q3)

  def quartilesFuture(list: List[Anime]): Future[List[Double]] =
    sortScoreFuture(list).map: sorted =>
      val scores = sorted.map(_.score)
      val n = scores.length

      if n == 0 then List()
      else
        val q2 =
          if n % 2 == 1 then scores(n / 2)
          else (scores(n / 2 - 1) + scores(n / 2)) / 2

        val lowerHalf = scores.take(n / 2)

        val upperHalf =
          if n % 2 == 1 then scores.drop(n / 2 + 1)
          else scores.drop(n / 2)

        def median(data: List[Double]): Double =
          val m = data.length
          if m % 2 == 1 then data(m / 2)
          else (data(m / 2 - 1) + data(m / 2)) / 2

        val q1 = median(lowerHalf)
        val q3 = median(upperHalf)

        List(q1, q2, q3)