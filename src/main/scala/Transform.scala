import Extract.Anime
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

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

  // 🔹 2. ลบข้อมูลซ้ำแบบ Parallel (กระจายงานให้ CPU หลาย Core)
  def removeDuplicatesParallel(data: List[Anime], cores: Int): List[Anime] =
    val chunkSize = math.max(1, data.size / cores)
    val chunks = data.grouped(chunkSize).toList

    // โยนเข้า Future
    val futureChunks: List[Future[List[Anime]]] = chunks.map: chunk =>
      Future(chunk.distinct)

    // รอผลและรวมร่าง
    val aggregatedFuture = Future.sequence(futureChunks).map(_.flatten)
    val mergedList = Await.result(aggregatedFuture, Duration.Inf)
    
    // ลบตัวซ้ำตอนรวมร่างอีกรอบ
    mergedList.distinct