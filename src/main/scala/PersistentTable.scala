
import scala.io._ 
import java.io._ 
import org.scala_tools.time.Imports._


// Case classes modeling database-style records
abstract class Record

// The case classes below are provided just as an example
// you can modify them and add more classes as needed

case class Customer(
  firstName: String,
  lastName: String,
  address: String,
  phone: String,
  email: String
) extends Record

case class MovieTitle(
  titleID: Int,
  title: String,
  director: String,
  year: Int,
  rating: String,
  copyNumber: Int //When a new title is added to the database, it is given a copy number of 0. This "copy" cannot actually be checked out.
  								//When DVDs of this title are added, they will have copy numbers of 1, 2, 3, ...
) extends Record

case class MovieRental(
  titleID: Int,     
  customerID: Int, // ID of renting customer 
  title : String,
  copynumber : Int,
  duedate : LocalDate
) extends Record

case class GameTitle(
  titleID: Int,
  title: String,
  developer: String,
  year: Int,
  rating: String,
  copyNumber: Int //When a new title is added to the database, it is given a copy number of 0. This "copy" cannot actually be checked out.
  								//When DVDs of this title are added, they will have copy numbers of 1, 2, 3, ...
) extends Record

case class GameRental(
  titleID: Int,     // ID of rented dvd title
  customerID: Int, // ID of renting customer 
  title : String,
  copynumber : Int,
  duedate : LocalDate
) extends Record

// Th parametric class Table models a table of records of type T.
// An instance of a concrete subclass of Table contains a table of records of type T.
// The table is initialized at object creation from data read from an input file.
// The file is a plain text file:
// each line contains the same number of elements, separated by a colon (:),
// and represents a record in the table.
// The first element of each line must be a unique integer that serves as 
// a unique ID for the record; the other elements contain the record's data.
// Lines are read from the file sequentially and translated into the corresponding 
// record of type T. Records are stored in the internal field 'table', a mutable map 
// from integers to T. The record ID is used as the key.
// If the file is non-existent or empty the table will be empty too.
// Once the object has been created, it is possible to lookup, add or remove records 
// using map-like operators.
// It is also possible to save the table to the same text file provided as input. 
// The file is rewritten with the contents of the current table.
//
// Note: The implementation below favors simplicity over efficiency.
//
abstract class PTable[T](
  // path of input text file in posix format (e.g., unix-style paths)
  val fileName: String
) {
  // expected element separator in input file
  protected val separator = ":"
  
  // field storing the initially empty table
  protected val table = collection.mutable.Map[Int, T]()

  // initialization code
  try { 
    // for each line in the input file
    for (l <- Source.fromFile(fileName).getLines) {
      // split l into its elements and return them in a string array
      // ex.: if line is "232:aa:bb:25" then a will be Array("232", "aa", "bb", "25")
      val a = l.split(separator)
      // convert a into a key/record pair of type (Int,T) 
      val (k, r) = toKeyRecord(a)
      // insert the pair into the table
      table.update(k, r)
    }
  } catch {
    // if fileName does not point to an existing file, do nothing
    case _:FileNotFoundException => ()
  }

  // returns the size of the table
  def size = table.size

  // returns an integer that is not currently used as a record ID in the table 
  def newRecordID (c: Int) = {
    val idSet = table.keySet
    def gen(n: Int): Int = if (idSet contains n) gen(n + 1) else n

    gen(c)
  }
	
  // returns Some(r) if table contains a value r with key id
  // returns None otherwise
  def get(id: Int) = table.get(id)

  // Remomes any mapping associated to key id
  // returns Some(r) if table contains a value r with key id
  // returns None otherwise
  def remove(id: Int) = table.remove(id)

  // adds record r with key id to the table, 
  // overriding any previous mapping of k
  def update(id: Int, r: T) = table.update(id, r)

  // returns an mutable map of the key/record pairs in table
  // where the record satisfies the predicate p
  def select(p: T => Boolean) = table.filter(kr => p(kr._2)) 

  // Removes all mappings from the table
  def clear = table.clear

  // converts table into a list
  def toList = table.toList
	
  // saves the table to file
  def save {
    val f = new PrintWriter(fileName)
    try {
      for (p <- table)
        f.println(printForm(p))
    } finally {
      f.close
    }
  }
  
  // converts input array a into a key/record pair
  // Must be implemented by a concrete subclass because the translation depends on T
  protected def toKeyRecord(a: Array[String]): (Int, T)

  // turns a key/record pair into a CSV string
  // Must be implemented by a concrete subclass because the translation depends on T
  protected def printForm(p: (Int, T)): String
}


// CustomerPTable is a concrete subclass of PTable[Customer]
// that just implements the abstract methods of PTable[Customer]

class CustomerPTable(fileName: String) extends PTable[Customer](fileName) {
	
  protected def printForm(pair: (Int, Customer)) = {
    val (id, p) = pair
    id + separator + 
    p.firstName + separator + 
    p.lastName + separator + 
    p.address + separator + 
    p.phone + separator +
    p.email
  } 

  protected def toKeyRecord(a: Array[String]) = {
    val k = a(0).toInt
    val r = Customer(firstName = a(1),
            	        lastName  = a(2),
                     address   = a(3),
                     phone     = a(4),
                     email = a(5)
                    )
    (k, r)
  }
}
  
class MovieTitlePTable(fileName: String) extends PTable[MovieTitle](fileName) {
	
  protected def toKeyRecord(a: Array[String]) = {
    val k = a(0).toInt
    val r = MovieTitle(titleID = a(1).toInt,
                        title = a(2).toString,
                        director=a(3).toString,
                        year=a(4).toInt,
                        rating = a(5).toString,
                        copyNumber = a(6).toInt
                       )
    (k, r)
  }
  
	// returns true if a movie or game is found with the given title ID
	// returns false otherwise
	protected def findTitleID(id: Int): Boolean = {
		val selection = select(p => p.titleID == id)
		!(selection.isEmpty)
	}
	
	protected def findTitle(title: String): Boolean = {
		val selection = select(p => p.title == title)
		!(selection.isEmpty)
	}
	
  // returns an integer that is not currently used as a title ID in the table
  def newTitleID (c: Int) = {
  	def gen(n: Int): Int = if (findTitleID(n)) gen(n + 1) else n
  	gen(c)
  }
	
  protected def printForm(pair: (Int, MovieTitle)) = {
    val (id, p) = pair
    id + separator + 
    p.titleID + separator + 
    p.title + separator + 
    p.director + separator + 
    p.year + separator + 
    p.rating + separator + 
    p.copyNumber
  }
}

class MovieRentalPTable(fileName: String) extends PTable[MovieRental](fileName) {
	
  protected def toKeyRecord(a: Array[String]) = {
    val k = a(0).toInt
    val r = MovieRental(titleID = a(1).toInt,
                        customerID = a(2).toInt,
                        title=a(3).toString,
                        copynumber = a(4).toInt,
                        duedate=new LocalDate(a(5))
                       )
    (k, r)
  }
	
  protected def printForm(pair: (Int, MovieRental)) = {
    val (id, p) = pair
    id + separator + 
    p.titleID + separator + 
    p.customerID + separator + 
    p.title + separator + 
    p.copynumber + separator + 
    p.duedate
  }
}

class GameTitlePTable(fileName: String) extends PTable[GameTitle](fileName) {
	
  protected def toKeyRecord(a: Array[String]) = {
    val k = a(0).toInt
    val r = GameTitle(titleID = a(1).toInt,
                        title = a(2).toString,
                        developer=a(3).toString,
                        year=a(4).toInt,
                        rating = a(5).toString,
                        copyNumber = a(6).toInt
                       )
    (k, r)
  }

  // returns true if a movie or game is found with the given title ID
	// returns false otherwise
	protected def findTitleID(id: Int): Boolean = {
		val selection = select(p => p.titleID == id)
		!(selection.isEmpty)
	}
	
	protected def findTitle(title: String): Boolean = {
		val selection = select(p => p.title == title)
		!(selection.isEmpty)
	}
	
  // returns an integer that is not currently used as a title ID in the table
  def newTitleID (c: Int) = {
  	def gen(n: Int): Int = if (findTitleID(n)) gen(n + 1) else n
  	gen(c)
  }
	
  protected def printForm(pair: (Int, GameTitle)) = {
    val (id, p) = pair
    id + separator + 
    p.titleID + separator + 
    p.title + separator + 
    p.developer + separator + 
    p.year + separator + 
    p.rating + separator + 
    p.copyNumber
  }
}

class GameRentalPTable(fileName: String) extends PTable[GameRental](fileName) {
	
  protected def toKeyRecord(a: Array[String]) = {
    val k = a(0).toInt
    val r = GameRental(titleID = a(1).toInt,
                        customerID = a(2).toInt,
                        title=a(3).toString,
                        copynumber = a(4).toInt,
                        duedate=new LocalDate(a(5))
                       )
    (k, r)
  }
	
  protected def printForm(pair: (Int, GameRental)) = {
    val (id, p) = pair
    id + separator + 
    p.titleID + separator + 
    p.customerID + separator + 
    p.title + separator + 
    p.copynumber + separator +
    p.duedate
  }
}