package transform

import extract.Extract._
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, ExecutionContext}

object DemoTransform:

  def process(data: List[Anime]): List[Anime] =

    // 🔹 0. ลบข้อมูลที่ซ้ำกันก่อนเลย
    val uniqueData = removeDuplicatesSequential(data)

    // 🔹 1. กรองเอาเฉพาะที่ score > 8.5
    val filtered = uniqueData.filter(_.score > 8.5)

    // 🔹 2. เรียงคะแนนจากมากไปน้อย (ใส่เครื่องหมายลบหน้า score เพื่อให้เรียงจากมากไปน้อย)
    val sorted = filtered.sortBy(anime => -anime.score)

    // 🔹 3. แสดงผล
    println("===== Transform Result =====")
    sorted.foreach(println)
    
    sorted

  // 🔹 1. ลบข้อมูลซ้ำแบบ Sequential (ทำงานทีละแถว)
  def removeDuplicatesSequential(data: List[Anime]): List[Anime] = {
    data.distinct
  }
  // ลบข้อมูลซ้ำโดยเช็คแค่ animeId (Sequential)
  def removeDuplicatesIDSequential(data: List[Anime]): List[Anime] = {
    data.distinctBy(_.animeId)
  }

  // 🔹 2. ลบข้อมูลซ้ำแบบ Parallel (กระจายงานให้ CPU หลาย Core)
  def removeDuplicatesParallel(data: List[Anime], cores: Int): Future[List[Anime]] =
    val chunkSize = math.max(1, data.size / cores)
    val chunks = data.grouped(chunkSize).toList

    // โยนเข้า Future
    val futureChunks: List[Future[List[Anime]]] = chunks.map: chunk =>
      Future(chunk.distinct)

    // รอผลและรวมร่าง
    val aggregatedFuture = Future.sequence(futureChunks).map(_.flatten)
    aggregatedFuture
    // val mergedList = Await.result(aggregatedFuture, Duration.Inf)
    
    // // ลบตัวซ้ำตอนรวมร่างอีกรอบ
    // mergedList.distinct

  //ลบข้อมูลซ้ำโดยเช็คแค่ animeId (Parallel ด้วย Future)
  def removeDuplicatesIDParallel(data: List[Anime], cores: Int): List[Anime] =
    val chunkSize = math.max(1, data.size / cores)
    val chunks = data.grouped(chunkSize).toList

    // โยนเข้า Future และให้แต่ละก้อนลบตัวซ้ำโดยดูจาก animeId
    val futureChunks: List[Future[List[Anime]]] = chunks.map: chunk =>
      Future(chunk.distinctBy(_.animeId))

    // รอผลและรวมร่าง
    val aggregatedFuture = Future.sequence(futureChunks).map(_.flatten)
    val mergedList = Await.result(aggregatedFuture, Duration.Inf)
    
    // สำคัญ: ตอนเอากลับมารวมร่างกัน ต้องลบซ้ำด้วย animeId อีกรอบ
    mergedList.distinctBy(_.animeId)

object DateTransform:
  def transformDate(date : String) : Option[LocalDate] =
    try
      Some(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    catch
      case e: Exception => None

  def getAge(date: LocalDate, current: LocalDate) : Int =
      current.getYear() - date.getYear()

  def mapAge(list: List[Anime], current: LocalDate) : List[Int] =
    list.map(anime => transformDate(anime.startDate) match
      case Some(date) => getAge(date, current)
      case None => 0
    )

  def mapAgeFuture(list: List[Anime], current: LocalDate): Future[List[Int]] =
    Future.sequence:
      list.map(anime =>
        Future:
          transformDate(anime.startDate) match
            case Some(date) => getAge(date, current)
            case None => 0
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
  def percentile(list: List[Anime], p: Double): Double =
    val sorted = sortScore(list)
    val scores = sorted.map(_.score)
    val n = scores.length

    if n == 0 then 0.0
    else if p <= 0 then scores.head
    else if p >= 100 then scores.last
    else
      val rank = (p / 100.0) * (n - 1)
      val lowerIndex = rank.toInt
      val upperIndex = lowerIndex + 1
      val weight = rank - lowerIndex

      if upperIndex < n then
        scores(lowerIndex) * (1 - weight) + scores(upperIndex) * weight
      else
        scores(lowerIndex)

  def percentileFuture(list: List[Anime], p: Double): Future[Double] =
    sortScoreFuture(list).map: sorted =>
      val scores = sorted.map(_.score)
      val n = scores.length

      if n == 0 then 0.0
      else if p <= 0 then scores.head
      else if p >= 100 then scores.last
      else
        val rank = (p / 100.0) * (n - 1)
        val lowerIndex = rank.toInt
        val upperIndex = lowerIndex + 1
        val weight = rank - lowerIndex

        if upperIndex < n then
          scores(lowerIndex) * (1 - weight) + scores(upperIndex) * weight
        else
          scores(lowerIndex)
