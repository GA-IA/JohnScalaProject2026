object MainApp {

  def main(args: Array[String]): Unit = {

    val data = ExtractSequential.extract("anime.csv")

    DemoTransform.process(data)

  }
}
