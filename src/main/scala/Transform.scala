package transform

import java.time.LocalDate
import java.time.format.DateTimeFormatter

def transformDate(date : String) : LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

def getAge(date: LocalDate, current: LocalDate) : Int =
    current.getYear() - date.getYear()

object DemoTransform {

  def process(data: List[Extract.Anime]): List[Extract.Anime] = {

    // 🔹 1. กรอง score > 8.5
    val filtered = data.filter(_.score > 8.5)

    // 🔹 2. เรียงคะแนนจากมากไปน้อย
    val sorted = filtered.sortBy(- _.score)

    // 🔹 3. แสดงผล
    println("===== Transform Result =====")
    sorted.foreach(println)

    sorted
  }

}