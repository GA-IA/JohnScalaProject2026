import com.sun.net.httpserver.{HttpServer, HttpHandler, HttpExchange}
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import scala.io.Source
import ujson.*

@main def webApp(): Unit =
  val server = HttpServer.create(new InetSocketAddress(3000), 0)

  server.createContext("/", new HttpHandler:
    def handle(exchange: HttpExchange): Unit =

      if exchange.getRequestMethod == "GET" then
        sendResponse(exchange, homePage())

      else if exchange.getRequestMethod == "POST" then
        try
          val body = Source.fromInputStream(exchange.getRequestBody).mkString

          val params =
            body.split("&")
              .map(_.split("=", 2))
              .collect { case Array(k, v) =>
                k -> URLDecoder.decode(v, "UTF-8")
              }
              .toMap

          val processType = params.getOrElse("processType", "0").toInt
          val file = params.getOrElse("file", "")

          val start = System.currentTimeMillis()

          if processType == 0 then
            processSequential(file, "file.json", LocalDate.now())
          else
            processConcurrent(file, "file.json", LocalDate.now())

          val time = System.currentTimeMillis() - start

          val html =
            s"""
            <html>
              <head>
              </head>

              <body>
                <h1 style="font-weight: 1000;">Table</h1>

                <h2>Running Time : <span style="font-size: 30px; color: rgb(212, 0, 0);">$time</span> ms</h2>

                <div id="tableArea"></div>

              </body>
              <script>
                fetch("/data")
                  .then(res => res.json())
                  .then(data => {

                    let table = "<table>";
                    table += "<tr>"
                    table += "<th>Title</th><th>Info</th><th>Synopsis</th>"
                    table += "</tr>";

                    data.data.forEach(a => {

                      table += `
                        <tr>
                          <td>
                            <div class="title">
                              <img src="$${a.imageUrl}" width="80">
                              <div>
                                <b>$${a.title}</b><br>
                                <b>ID : </b>$${a.animeId}
                              </div>
                            </div>
                          </td>
                          <td>
                            <div class="info">
                                <b>Type : </b>$${a.type}<br>
                                <b>Episodes : </b>$${a.episodes}<br>
                                <b>Start : </b>$${a.startDate}<br>
                                <b>End : </b>$${a.endDate}<br>
                                <b>Age : </b>$${a.age}<br>
                                <b>Score : </b>$${a.score}<br>
                                <b>Rank : </b>$${a.rank}<br>
                                <b>Popularity : </b>$${a.popularity}<br>
                                <b>Members : </b>$${a.members}
                              </div>
                          </td>
                          <td>
                            <div class="sys">
                              $${a.synopsis}
                            </div>
                          </td>
                        </tr>
                      `;
                    });

                    table += "</table>";

                    document.getElementById("tableArea").innerHTML = table;
                  });
              </script>
              <style>
                @import url('https://fonts.googleapis.com/css2?family=Nunito:ital,wght@0,200..1000;1,200..1000&display=swap');
                body{
                    padding: 50px;
                    text-align: center;
                    font-family: "Nunito", sans-serif;
                }

                h1{
                    font-size: 40px;
                }

                table{
                    border-collapse: collapse;
                }

                th{
                    font-size: 30px;
                    border-bottom: 2px solid rgb(83, 83, 83);
                    padding: 11px;
                }

                td{
                    border-bottom: 2px solid rgb(83, 83, 83);
                }

                .title{
                    margin: 20px;
                    text-align: center;
                    font-size: 21px;
                }

                .title img{
                    width: 150px;
                    margin-bottom: 13px;
                }

                .info{
                    width: 200px;
                    margin: 20px 50px;
                    font-size: 18px;
                    line-height: 1.8;
                }

                .sys{
                    margin: 30px;
                    text-indent: 30px;
                }
            </style>
            </html>
            """

          sendResponse(exchange, html)

        catch
          case e: Exception =>
            sendResponse(exchange,
              s"<h2>Error:</h2><pre>${e.getMessage}</pre><a href='/'>Back</a>"
            )
  )

  server.setExecutor(null)
  server.createContext("/data", new HttpHandler:
    def handle(exchange: HttpExchange): Unit =
      val json = Source.fromFile("file.json").mkString
      val bytes = json.getBytes(StandardCharsets.UTF_8)

      exchange.getResponseHeaders.add("Content-Type", "application/json")
      exchange.sendResponseHeaders(200, bytes.length)

      val os = exchange.getResponseBody
      os.write(bytes)
      os.close()
  )
  server.start()
  println("Server started at http://localhost:3000")
  Thread.currentThread().join()


// ---------------- HOME PAGE ----------------

def homePage(): String =
  """
  <html>
  <body>
      <h1>Dataset Processor</h1>
      <form method="POST">
          <h2>File Name : <input name="file">
          Process Type : 
          <select name="processType">
              <option value="0">Sequential</option>
              <option value="1">Concurrent</option>
          </select>
          <br><br>

          <button type="submit">Run</button>
      </form>
  </body>
  <style>
      @import url('https://fonts.googleapis.com/css2?family=Nunito:ital,wght@0,200..1000;1,200..1000&display=swap');
      body{
          padding: 50px;
          text-align: center;
          font-family: "Nunito", sans-serif;
          }

      h1{
          font-size: 40px;
          font-weight: 1000;
          margin-bottom: 50px;
      }

      form{
          justify-self: center;
          padding: 20px;
          background-color: rgb(173, 199, 216);
          width: 800px;
          border-radius: 15px;
      }

      input{
          padding: 8px;
          border-radius: 6px;
          margin-right: 14px;
      }

      select{
          padding: 8px 20px;
          border-radius: 6px;
          margin-right: 14px;
      }

      button{
          padding: 9px 25px;
          border-radius: 6px;
          background-color: rgb(212, 0, 0);
          color: #ffffff;
          border: none;
          font-size: 17px;
          font-family: "Nunito", sans-serif;
          font-weight: 800;
      }

      button::hover{
          background-color: rgb(172, 0, 0);
      }
  </style>
  </html>
  """

// ---------------- RESPONSE ----------------

def sendResponse(exchange: HttpExchange, response: String): Unit =
  val bytes = response.getBytes(StandardCharsets.UTF_8)
  exchange.getResponseHeaders.add("Content-Type", "text/html; charset=UTF-8")
  exchange.sendResponseHeaders(200, bytes.length)
  val os = exchange.getResponseBody
  os.write(bytes)
  os.close()