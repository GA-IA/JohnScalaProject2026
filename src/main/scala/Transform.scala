package transform

import java.time.LocalDate
import java.time.format.DateTimeFormatter

def transformDate(date : String) : LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

def getAge(date: LocalDate, current: LocalDate) : Int =
    current.getYear() - date.getYear()