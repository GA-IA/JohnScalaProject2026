object MainApp {

  def main(args: Array[String]): Unit = {

    val path = "test.csv"

    println("\nRunning Parallel...")
    val parData = Extract.ExtractParallel(path)
    val parResult = DemoTransform.process(parData)

    println("Running Sequential...")
    val seqData = Extract.ExtractSequential(path)
    val seqResult = DemoTransform.process(seqData)

    println(s"Parallel result size: ${parResult.size}")
    println(s"\nSequential result size: ${seqResult.size}")
  }
}