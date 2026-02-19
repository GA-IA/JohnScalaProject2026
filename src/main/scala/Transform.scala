package transform

import extract._
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Transform:
  def transformDate(date : String) : LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  def getAge(date: LocalDate, current: LocalDate) : Int =
      current.getYear() - date.getYear()

// object DemoTransform:
//   def process(data: List[Extract.Anime]): List[Extract.Anime] =
//     // 🔹 2. เรียงคะแนนจากมากไปน้อย
//     val sorted = filtered.sortBy(anime => -anime.score)

//     // 🔹 3. แสดงผล
//     println("===== Transform Result =====")
//     sorted.foreach(println)
//     sorted
