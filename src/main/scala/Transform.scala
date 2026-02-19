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