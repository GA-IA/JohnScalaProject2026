import java.util.Date

def transformDate(date : String) : Date =
    Date.parse(date)

def getAge(date: Date, current: Current) : Int =
    date.getYear() - current.getYear()